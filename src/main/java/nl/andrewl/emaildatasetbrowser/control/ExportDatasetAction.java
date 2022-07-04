package nl.andrewl.emaildatasetbrowser.control;

import nl.andrewl.email_indexer.data.export.dataset.ZipExporter;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

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
			promptUserForExportFile(dsPath).ifPresent(this::doExport);
		} else {
			JOptionPane.showMessageDialog(
					browser,
					"There is currently no dataset open in the browser.\nOpen one first, then export it.",
					"No Open Dataset",
					JOptionPane.WARNING_MESSAGE
			);
		}
	}

	private Optional<Path> promptUserForExportFile(Path defaultFile) {
		JFileChooser fc = new JFileChooser(defaultFile.toFile());
		int result = fc.showSaveDialog(browser);
		if (result == JFileChooser.APPROVE_OPTION) {
			if (!fc.getSelectedFile().toPath().getFileName().toString().toLowerCase().endsWith(".zip")) {
				JOptionPane.showMessageDialog(
						browser,
						"Please save as a .zip file.",
						"Invalid File",
						JOptionPane.WARNING_MESSAGE
				);
				return Optional.empty();
			}
			Path file = fc.getSelectedFile().toPath();
			// Add a confirmation to ensure that if the file exists, it's not immediately overwritten.
			if (Files.exists(file)) {
				int confirmChoice = JOptionPane.showConfirmDialog(
						browser,
						"Are you sure you want to overwrite the ZIP file at\n" + file.toAbsolutePath(),
						"Confirm ZIP Export Overwrite",
						JOptionPane.OK_CANCEL_OPTION
				);
				if (confirmChoice != JOptionPane.OK_OPTION) return Optional.empty();
			}
			return Optional.of(file);
		}
		return Optional.empty();
	}

	private void doExport(Path file) {
		ProgressDialog progress = ProgressDialog.minimalText(browser, "Exporting");
		progress.append("Exporting dataset to " + file.toAbsolutePath());
		long start = System.currentTimeMillis();
		new ZipExporter().export(browser.getCurrentDataset(), file)
				.handle((unused, throwable) -> {
					if (throwable != null) {
						progress.append("An error occurred: " + throwable.getMessage());
					} else {
						long dur = System.currentTimeMillis() - start;
						progress.append("Export completed in %.1f seconds.".formatted((float) dur / 1000.0f));
					}
					progress.done();
					return null;
				});
	}
}
