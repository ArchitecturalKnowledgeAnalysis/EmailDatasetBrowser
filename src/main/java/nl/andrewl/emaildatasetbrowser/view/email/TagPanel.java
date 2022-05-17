package nl.andrewl.emaildatasetbrowser.view.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.Tag;
import nl.andrewl.email_indexer.data.TagRepository;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.util.concurrent.ForkJoinPool;

/**
 * Panel that's used to manage the tags belonging to a single email entry. It
 * shows the list of tags, and provides facilities to modify that list.
 */
public class TagPanel extends JPanel implements EmailViewListener {
	private final EmailViewPanel parent;
	private final DefaultListModel<Tag> tagListModel = new DefaultListModel<>();
	private final DefaultListModel<Tag> parentTagListModel = new DefaultListModel<>();
	private final DefaultListModel<Tag> childTagListModel = new DefaultListModel<>();
	private final DefaultComboBoxModel<Tag> tagComboBoxModel = new DefaultComboBoxModel<>();
	private EmailEntry email = null;
	private final JButton removeButton = new JButton("Remove");

	public TagPanel(EmailViewPanel parent) {
		super(new BorderLayout());
		this.parent = parent;
		this.setBorder(BorderFactory.createTitledBorder("Tags"));
		this.removeButton.setEnabled(false);

		JPanel centerPanel = new JPanel(new GridLayout(0, 2));

		JList<Tag> tagList = new JList<>(this.tagListModel);
		tagList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tagList.setCellRenderer(new TagListCellRenderer());
		tagList.getSelectionModel().addListSelectionListener(e -> {
			SwingUtilities.invokeLater(() -> {
				removeButton.setEnabled(tagList.getSelectedIndices().length > 0);
			});
		});
		centerPanel.add(new JScrollPane(tagList));

		JPanel otherTagsPanel = new JPanel(new GridLayout(2, 0));
		JList<Tag> parentTagList = new JList<>(this.parentTagListModel);
		parentTagList.setCellRenderer(new TagListCellRenderer());
		parentTagList.setEnabled(false);
		JScrollPane parentTagScrollPane = new JScrollPane(parentTagList);
		parentTagScrollPane.setBorder(BorderFactory.createTitledBorder("Parent Tags"));
		otherTagsPanel.add(parentTagScrollPane);
		JList<Tag> childTagList = new JList<>(this.childTagListModel);
		childTagList.setCellRenderer(new TagListCellRenderer());
		childTagList.setEnabled(false);
		JScrollPane childTagScrollPane = new JScrollPane(childTagList);
		childTagScrollPane.setBorder(BorderFactory.createTitledBorder("Child Tags"));
		otherTagsPanel.add(childTagScrollPane);
		centerPanel.add(otherTagsPanel);

		this.add(centerPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		JComboBox<Tag> tagComboBox = new JComboBox<>(this.tagComboBoxModel);
		tagComboBox.setEditable(false);
		tagComboBox.setToolTipText("Select tags to add to this email.");
		buttonPanel.add(tagComboBox, BorderLayout.CENTER);
		tagComboBox.addActionListener(e -> {
			if (e.getActionCommand().equals("comboBoxChanged")) {
				onTagSelected(tagComboBox);
			}
		});


		JPanel buttonCtlPanel = new JPanel();
		buttonCtlPanel.setLayout(new BoxLayout(buttonCtlPanel, BoxLayout.PAGE_AXIS));

		removeButton.addActionListener(e -> {
			var repo = new TagRepository(parent.getCurrentDataset());
			for (var tag : tagList.getSelectedValuesList()) {
				repo.removeTag(email.id(), tag.id());
			}
			parent.refresh();
		});
		JPanel topButtonPanel = new JPanel();
		topButtonPanel.add(removeButton);
		buttonCtlPanel.add(topButtonPanel);

		buttonPanel.add(buttonCtlPanel, BorderLayout.EAST);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void setEmail(EmailEntry email) {
		this.email = email;
		if (email != null) {
			refreshTags();
		}
	}

	public void refreshTags() {
		this.tagListModel.clear();
		this.tagComboBoxModel.removeAllElements();
		this.parentTagListModel.removeAllElements();
		this.childTagListModel.removeAllElements();
		ForkJoinPool.commonPool().execute(() -> {
			var repo = new TagRepository(parent.getCurrentDataset());
			var eligibleTags = repo.findAll();
			var thisTags = repo.getTags(email.id());
			eligibleTags.removeAll(thisTags); // Remove any tags that this email already has.
			var parentTags = repo.getAllParentTags(email.id());
			var childTags = repo.getAllChildTags(email.id());
			SwingUtilities.invokeLater(() -> {
				this.tagComboBoxModel.addAll(eligibleTags);
				this.tagListModel.addAll(thisTags);
				this.parentTagListModel.addAll(parentTags);
				this.childTagListModel.addAll(childTags);
			});
		});
	}

	private void onTagSelected(JComboBox<Tag> tagComboBox) {
		Tag tag = (Tag) tagComboBox.getSelectedItem();
		if (tag == null) return;
		new TagRepository(parent.getCurrentDataset()).addTag(email.id(), tag.id());
		parent.refresh();
	}

	@Override
	public void emailUpdated(EmailEntry email) {
		setEmail(email);
	}
}
