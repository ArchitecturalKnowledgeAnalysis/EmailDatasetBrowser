package nl.andrewl.emaildatasetbrowser.control;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.prefs.Preferences;

public class DatasetOpenAction extends AbstractAction {
	private static final String PREF_OPEN_DIR = "dataset_open_dir";

	private final EmailDatasetBrowser browser;

	public DatasetOpenAction(EmailDatasetBrowser browser) {
		super("Open Dataset");
		this.browser = browser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Preferences prefs = EmailDatasetBrowser.getPreferences();
		JFileChooser fc = new JFileChooser(prefs.get(PREF_OPEN_DIR, Path.of(".").toAbsolutePath().toString()));
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int result = fc.showOpenDialog(browser);
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			ProgressDialog progress = new ProgressDialog(
					browser,
					"Opening Dataset",
					null,
					true,
					false,
					true
			);
			progress.activate();
			progress.append("Opening dataset from " + f.getAbsolutePath());
			Path datasetPath = f.toPath();
			prefs.put(PREF_OPEN_DIR, datasetPath.getParent().toAbsolutePath().toString());
			var future = EmailDataset.open(datasetPath);
			future.handleAsync((dataset, throwable) -> {
				progress.done();
				if (throwable != null) {
					progress.append("Could not open dataset: " + throwable.getMessage());
				} else {
					browser.setDataset(dataset);
					var repo = new EmailRepository(dataset);
					String message = "Opened dataset from %s with\n%d emails,\n%d tags,\n%d tagged emails".formatted(
							dataset.getOpenDir(),
							repo.countEmails(),
							repo.countTags(),
							repo.countTaggedEmails()
					);
					progress.append(message);
				}
				return null;
			});
		}
	}
}
