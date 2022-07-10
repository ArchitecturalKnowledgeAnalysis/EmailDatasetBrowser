package nl.andrewl.emaildatasetbrowser.view.email;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.Tag;
import nl.andrewl.email_indexer.data.TagRepository;
import nl.andrewl.emaildatasetbrowser.view.DatasetChangeListener;
import nl.andrewl.emaildatasetbrowser.view.tag.TagEditDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel that's used to manage the tags belonging to a single email entry. It
 * shows the list of tags, and provides facilities to modify that list.
 */
public class TagPanel extends JPanel implements EmailViewListener, DatasetChangeListener {
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
		parent.getBrowser().addListener(this);
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

		addDoubleClickToEditAction(tagList);
		addDoubleClickToEditAction(parentTagList);
		addDoubleClickToEditAction(childTagList);

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

	/**
	 * Adds a double-click mouse listener to a tag list, so that we can open
	 * an edit tag dialog for the selected tag. Note that because this tag
	 * panel is a DatasetChangeListener, the set of tags is refreshed
	 * automatically, so we do not call it after the dialog closes.
	 * @param list The list to add the listener to.
	 */
	private void addDoubleClickToEditAction(JList<Tag> list) {
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && list.getSelectedIndices().length == 1) {
					Tag selectedTag = list.getSelectedValue();
					var dialog = new TagEditDialog(parent.getBrowser(), selectedTag, parent.getCurrentDataset());
					dialog.setVisible(true);
				}
			}
		});
	}

	private void setEmail(EmailEntry email) {
		this.email = email;
		refreshTags();
	}

	public void refreshTags() {
		if (this.email == null) {
			return;
		}
		this.tagListModel.clear();
		this.tagComboBoxModel.removeAllElements();
		this.parentTagListModel.clear();
		this.childTagListModel.clear();
		if (parent.getCurrentDataset() != null) {
			var repo = new TagRepository(parent.getCurrentDataset());
			var addableTags = repo.findAll();
			var thisTags = repo.getTags(email.id());
			addableTags.removeAll(thisTags); // Remove any tags that this email already has.
			var parentTags = repo.getAllParentTags(email.id());
			var childTags = repo.getAllChildTags(email.id());
			SwingUtilities.invokeLater(() -> {
				this.tagComboBoxModel.addAll(addableTags);
				this.tagListModel.addAll(thisTags);
				this.parentTagListModel.addAll(parentTags);
				this.childTagListModel.addAll(childTags);
				this.repaint();
			});
		}
	}

	private void onTagSelected(JComboBox<Tag> tagComboBox) {
		Tag tag = (Tag) tagComboBox.getSelectedItem();
		if (tag == null) return;
		new TagRepository(parent.getCurrentDataset()).addTag(email.id(), tag.id());
		refreshTags();
	}

	@Override
	public void emailUpdated(EmailEntry email) {
		setEmail(email);
	}

	@Override
	public void datasetChanged(EmailDataset ds) {
		setEmail(null);
	}

	@Override
	public void tagsChanged(EmailDataset ds) {
		refreshTags();
	}
}
