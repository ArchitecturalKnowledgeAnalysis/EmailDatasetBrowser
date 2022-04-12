package nl.andrewl.emaildatasetbrowser.control;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;

public class ExportDatasetAction extends AbstractAction {
	private final EmailDatasetBrowser browser;

	public ExportDatasetAction(EmailDatasetBrowser browser) {
		super("Export Dataset");
		this.browser = browser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.browser.getCurrentDataset() != null) {
			JFileChooser fc = new JFileChooser(Path.of(".").toFile());
			int result = fc.showSaveDialog(browser);
			if (result == JFileChooser.APPROVE_OPTION) {
				if (!fc.getSelectedFile().toPath().getFileName().toString().toLowerCase().endsWith(".zip")) {
					JOptionPane.showMessageDialog(
							browser,
							"Please save as a .zip file.",
							"Invalid File",
							JOptionPane.WARNING_MESSAGE
					);
				} else {
					Path file = fc.getSelectedFile().toPath();
					JDialog progressDialog = buildProgressDialog(file);
					ForkJoinPool.commonPool().execute(() -> progressDialog.setVisible(true));
					if (!closeBeforeExport()) return;
					long start = System.currentTimeMillis();
					EmailDataset.buildZip(this.browser.getCurrentDataset().getOpenDir(), file)
							.handle((unused, throwable) -> {
								System.out.println("Done!");
								if (throwable != null) {
									throwable.printStackTrace();
								} else {
									long dur = System.currentTimeMillis() - start;
									JOptionPane.showMessageDialog(
											browser,
											"Export completed in %.1f seconds.".formatted((float) dur / 1000.0f),
											"Export Complete",
											JOptionPane.INFORMATION_MESSAGE
									);
								}
								progressDialog.dispose();
								reopenAfterExport();
								return null;
							});
				}
			}
		} else {
			JOptionPane.showMessageDialog(
					browser,
					"There is currently no dataset open in the browser.\nOpen one first, then export it.",
					"No Open Dataset",
					JOptionPane.WARNING_MESSAGE
			);
		}
	}

	private JDialog buildProgressDialog(Path file) {
		JDialog progressDialog = new JDialog(browser, "Exporting...");
		JPanel p = new JPanel();
		JLabel label = new JLabel("Exporting dataset to " + file + " using highest compression settings.");
		p.add(label);
		progressDialog.setContentPane(p);
		progressDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		progressDialog.pack();
		progressDialog.setLocationRelativeTo(browser);
		return progressDialog;
	}

	private boolean closeBeforeExport() {
		try {
			this.browser.getCurrentDataset().close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(
					browser,
					"Could not close dataset's DB connection prior to export.",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			return false;
		}
	}

	private void reopenAfterExport() {
		try {
			this.browser.getCurrentDataset().establishConnection();
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(
					browser,
					"Could not reestablish dataset's DB connection after export.",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			browser.setDataset(null);
		}
	}
}
