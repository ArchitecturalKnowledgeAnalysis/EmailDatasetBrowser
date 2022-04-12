package nl.andrewl.emaildatasetbrowser.control;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;

public class DatasetOpenAction extends AbstractAction {
	private final EmailDatasetBrowser browser;

	public DatasetOpenAction(EmailDatasetBrowser browser) {
		super("Open Dataset");
		this.browser = browser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(Path.of(".").toFile());
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int result = fc.showOpenDialog(browser);
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			EmailDataset.open(f.toPath())
					.exceptionally(throwable -> {
						throwable.printStackTrace();
						JOptionPane.showMessageDialog(
								browser,
								"Could not open dataset: " + throwable.getMessage(),
								"Could not open dataset",
								JOptionPane.WARNING_MESSAGE
						);
						return null;
					})
					.thenAccept(ds -> {
						browser.setDataset(ds);
						var repo = new EmailRepository(ds);
						String message = "Opened dataset from %s with\n%d emails,\n%d tags,\n%d tagged emails".formatted(
								ds.getOpenDir(),
								repo.countEmails(),
								repo.countTags(),
								repo.countTaggedEmails()
						);
						JOptionPane.showMessageDialog(browser, message, "Dataset Opened", JOptionPane.INFORMATION_MESSAGE);
					});
		}
	}
}
