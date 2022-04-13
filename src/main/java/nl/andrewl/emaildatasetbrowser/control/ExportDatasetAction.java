package nl.andrewl.emaildatasetbrowser.control;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.sql.SQLException;

public class ExportDatasetAction extends AbstractAction {
	private final EmailDatasetBrowser browser;

	public ExportDatasetAction(EmailDatasetBrowser browser) {
		super("Export Dataset");
		this.browser = browser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.browser.getCurrentDataset() != null) {
			var ds = browser.getCurrentDataset();
			Path parentDir = ds.getOpenDir().getParent();
			String fileName = ds.getOpenDir().getFileName().toString() + ".zip";
			Path dsPath = parentDir.resolve(fileName);
			JFileChooser fc = new JFileChooser(dsPath.toFile());
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
					if (!closeBeforeExport()) return;
					ProgressDialog progress = ProgressDialog.minimalText(browser, "Exporting");
					progress.activate();
					progress.append("Exporting dataset to " + file.toAbsolutePath());
					long start = System.currentTimeMillis();
					EmailDataset.buildZip(this.browser.getCurrentDataset().getOpenDir(), file)
							.handle((unused, throwable) -> {
								if (throwable != null) {
									progress.append("An error occurred: " + throwable.getMessage());
								} else {
									long dur = System.currentTimeMillis() - start;
									progress.append("Export completed in %.1f seconds.".formatted((float) dur / 1000.0f));
								}
								reopenAfterExport();
								progress.done();
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
