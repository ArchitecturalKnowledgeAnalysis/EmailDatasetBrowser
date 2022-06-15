package nl.andrewl.emaildatasetbrowser.control;

import nl.andrewl.email_indexer.gen.EmailDatasetGenerator;
import nl.andrewl.email_indexer.util.FileUtils;
import nl.andrewl.email_indexer.util.Status;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.PathSelectField;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class GenerateDatasetAction extends AbstractAction {
	private final EmailDatasetBrowser browser;

	public GenerateDatasetAction(EmailDatasetBrowser browser) {
		super("Generate Dataset");
		this.browser = browser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = new JDialog(browser, "Generate Dataset", true);
		JPanel p = new JPanel(new BorderLayout());
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));

		var items = buildMBoxDirsPanel(dialog);
		inputPanel.add(items.getKey());
		JList<Path> mboxDirsList = items.getValue();

		PathSelectField datasetDirField = PathSelectField.directorySelectField();
		inputPanel.add(datasetDirField);
		p.add(inputPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(event -> dialog.dispose());
		JButton generateButton = new JButton("Generate");
		generateButton.addActionListener(event -> {
			if (!validateDialog(dialog, mboxDirsList, datasetDirField)) return;
			Collection<Path> paths = new ArrayList<>();
			for (int i = 0; i < mboxDirsList.getModel().getSize(); i++) {
				paths.add(mboxDirsList.getModel().getElementAt(i));
			}
			Path dsDir = datasetDirField.getSelectedPath();
			dialog.dispose();
			ProgressDialog progressDialog = new ProgressDialog(browser, "Generating...", "Generating the dataset.");
			progressDialog.start();
			Status status = new Status().withMessageConsumer(progressDialog);
			new EmailDatasetGenerator(status).generate(paths, dsDir).handle((unused, throwable) -> {
				progressDialog.done();
				if (throwable != null) {
					throwable.printStackTrace();
					progressDialog.accept("An exception occurred: " + throwable.getMessage());
				}
				return null;
			});
		});
		buttonPanel.add(generateButton);
		buttonPanel.add(cancelButton);
		p.add(buttonPanel, BorderLayout.SOUTH);

		dialog.setContentPane(p);
		dialog.pack();
		dialog.setLocationRelativeTo(browser);
		dialog.setVisible(true);
	}

	private Map.Entry<JPanel, JList<Path>> buildMBoxDirsPanel(JDialog owner) {
		DefaultListModel<Path> mboxDirsListModel = new DefaultListModel<>();
		JList<Path> mboxDirsList = new JList<>(mboxDirsListModel);
		mboxDirsList.setPreferredSize(new Dimension(500, 300));
		JPanel mboxDirsPanel = new JPanel(new BorderLayout());
		mboxDirsPanel.add(new JScrollPane(mboxDirsList), BorderLayout.CENTER);
		JPanel mboxDirsButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton addMboxDirButton = new JButton("Add Mbox Directory");
		addMboxDirButton.addActionListener(event -> {
			JFileChooser fc = new JFileChooser(Path.of(".").toFile());
			fc.setFileFilter(new DirectoryFileFilter());
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			int result = fc.showOpenDialog(browser);
			if (result == JFileChooser.APPROVE_OPTION) {
				Path mboxDir = fc.getSelectedFile().toPath();
				if (!mboxDirsListModel.contains(mboxDir)) {
					if (!FileUtils.dirContainsFileType(mboxDir, ".mbox")) {
						JOptionPane.showMessageDialog(
								fc,
								"This directory doesn't contain any MBox files.",
								"No Mbox Files",
								JOptionPane.WARNING_MESSAGE
						);
					} else {
						mboxDirsListModel.addElement(mboxDir);
					}
				}
			}
		});
		JButton removeMboxDirButton = new JButton("Remove Selected");
		removeMboxDirButton.addActionListener(event -> mboxDirsList.getSelectedValuesList().forEach(mboxDirsListModel::removeElement));
		mboxDirsButtonPanel.add(addMboxDirButton);
		mboxDirsButtonPanel.add(removeMboxDirButton);
		mboxDirsButtonPanel.add(new JButton(new DownloadEmailsAction(owner)));
		mboxDirsPanel.add(mboxDirsButtonPanel, BorderLayout.SOUTH);

		return new AbstractMap.SimpleEntry<>(mboxDirsPanel, mboxDirsList);
	}

	private boolean validateDialog(JDialog dialog, JList<Path> mboxDirsList, PathSelectField datasetDirField) {
		if (mboxDirsList.getModel().getSize() < 1) {
			JOptionPane.showMessageDialog(
					dialog,
					"No MBox directories have been added.",
					"No MBox Directories",
					JOptionPane.WARNING_MESSAGE
			);
			return false;
		}
		Path datasetDir = datasetDirField.getSelectedPath();
		if (datasetDir == null || !Files.isDirectory(datasetDir) || !FileUtils.isDirEmpty(datasetDir)) {
			JOptionPane.showMessageDialog(
					dialog,
					"You must select an empty directory to generate the dataset in.",
					"Invalid Dataset Directory",
					JOptionPane.WARNING_MESSAGE
			);
			return false;
		}
		return true;
	}
}
