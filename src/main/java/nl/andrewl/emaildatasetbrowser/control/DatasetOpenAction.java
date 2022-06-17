package nl.andrewl.emaildatasetbrowser.control;

import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;

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
			Path datasetPath = f.toPath();
			prefs.put(PREF_OPEN_DIR, datasetPath.getParent().toAbsolutePath().toString());
			browser.openDataset(datasetPath);
		}
	}
}
