package nl.andrewl.emaildatasetbrowser.view.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ForkJoinPool;

/**
 * Panel that's used to manage the tags belonging to a single email entry. It
 * shows the list of tags, and provides facilities to modify that list.
 */
public class TagPanel extends JPanel implements EmailViewListener {
	private final EmailViewPanel parent;
	private final DefaultListModel<String> tagListModel = new DefaultListModel<>();
	private final DefaultListModel<String> parentTagListModel = new DefaultListModel<>();
	private final DefaultListModel<String> childTagListModel = new DefaultListModel<>();
	private final DefaultComboBoxModel<String> tagComboBoxModel = new DefaultComboBoxModel<>();
	private EmailEntry email = null;
	private final JButton removeButton = new JButton("Remove");

	public TagPanel(EmailViewPanel parent) {
		super(new BorderLayout());
		this.parent = parent;
		this.setBorder(BorderFactory.createTitledBorder("Tags"));
		this.removeButton.setEnabled(false);

		JPanel centerPanel = new JPanel(new GridLayout(0, 2));

		JList<String> tagList = new JList<>(this.tagListModel);
		tagList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tagList.setCellRenderer(new TagListCellRenderer());
		tagList.getSelectionModel().addListSelectionListener(e -> {
			SwingUtilities.invokeLater(() -> {
				removeButton.setEnabled(tagList.getSelectedIndices().length > 0);
			});
		});
		centerPanel.add(new JScrollPane(tagList));

		JPanel otherTagsPanel = new JPanel(new GridLayout(2, 0));
		JList<String> parentTagList = new JList<>(this.parentTagListModel);
		parentTagList.setCellRenderer(new TagListCellRenderer());
		parentTagList.setEnabled(false);
		JScrollPane parentTagScrollPane = new JScrollPane(parentTagList);
		parentTagScrollPane.setBorder(BorderFactory.createTitledBorder("Parent Tags"));
		otherTagsPanel.add(parentTagScrollPane);
		JList<String> childTagList = new JList<>(this.childTagListModel);
		childTagList.setCellRenderer(new TagListCellRenderer());
		childTagList.setEnabled(false);
		JScrollPane childTagScrollPane = new JScrollPane(childTagList);
		childTagScrollPane.setBorder(BorderFactory.createTitledBorder("Child Tags"));
		otherTagsPanel.add(childTagScrollPane);
		centerPanel.add(otherTagsPanel);

		this.add(centerPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		JComboBox<String> tagComboBox = new JComboBox<>(this.tagComboBoxModel);
		tagComboBox.setEditable(true);
		buttonPanel.add(tagComboBox, BorderLayout.CENTER);

		JPanel buttonCtlPanel = new JPanel();
		buttonCtlPanel.setLayout(new BoxLayout(buttonCtlPanel, BoxLayout.PAGE_AXIS));

		JButton addButton = new JButton("Add");
		addButton.addActionListener(e -> {
			String tag = (String) tagComboBox.getSelectedItem();
			if (tag == null)
				return;
			if (tag.contains(",")) {
				String message = String.format("The tag name \"%s\" is invalid", tag);
				JOptionPane.showMessageDialog(parent, message, "Invalid Tag", JOptionPane.ERROR_MESSAGE);
				return;
			}
			new EmailRepository(parent.getCurrentDataset()).addTag(email.messageId(), tag);
			parent.refresh();
		});
		removeButton.addActionListener(e -> {
			var repo = new EmailRepository(parent.getCurrentDataset());
			for (var tag : tagList.getSelectedValuesList()) {
				repo.removeTag(email.messageId(), tag);
			}
			parent.refresh();
		});
		JPanel topButtonPanel = new JPanel();
		topButtonPanel.add(addButton);
		topButtonPanel.add(removeButton);
		buttonCtlPanel.add(topButtonPanel);

		buttonPanel.add(buttonCtlPanel, BorderLayout.EAST);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void setEmail(EmailEntry email) {
		this.email = email;
		this.tagListModel.clear();
		this.tagComboBoxModel.removeAllElements();
		this.parentTagListModel.removeAllElements();
		this.childTagListModel.removeAllElements();
		if (email != null) {
			this.tagListModel.addAll(email.tags());
			ForkJoinPool.commonPool().execute(() -> {
				var repo = new EmailRepository(parent.getCurrentDataset());
				var tags = repo.getAllTags();
				var parentTags = repo.getAllParentTags(email.messageId());
				var childTags = repo.getAllChildTags(email.messageId());
				SwingUtilities.invokeLater(() -> {
					this.tagComboBoxModel.addAll(tags);
					this.parentTagListModel.addAll(parentTags);
					this.childTagListModel.addAll(childTags);
				});
			});
		}
	}

	@Override
	public void emailUpdated(EmailEntry email) {
		setEmail(email);
	}
}
