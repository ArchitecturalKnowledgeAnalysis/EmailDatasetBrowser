package nl.andrewl.emaildatasetbrowser.view.tag;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.Tag;
import nl.andrewl.email_indexer.data.TagRepository;
import nl.andrewl.emaildatasetbrowser.view.SwingUtils;

import javax.swing.*;
import java.awt.*;

/**
 * This dialog can be used to manage the list of tags in a dataset.
 */
public class TagManagerDialog extends JDialog {
	private final TagTableModel tagTableModel = new TagTableModel();
	private final JTable tagTable = new JTable(tagTableModel);
	private final JButton editButton = new JButton("Edit");
	private final JButton addButton = new JButton("Add");
	private final JButton removeButton = new JButton("Remove");

	private final EmailDataset ds;

	public TagManagerDialog(Window owner, EmailDataset ds) {
		super(owner, "Tag Manager", ModalityType.APPLICATION_MODAL);
		this.ds = ds;

		setContentPane(buildUI());
		setPreferredSize(new Dimension(500, 500));
		pack();
		setLocationRelativeTo(owner);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private Container buildUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		tagTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tagTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int row = tagTable.getSelectedRow();
				boolean itemSelected = row != -1;
				editButton.setEnabled(itemSelected);
				removeButton.setEnabled(itemSelected);
			}
		});
		tagTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		tagTable.getColumnModel().getColumn(1).setPreferredWidth(120);
		tagTable.getColumnModel().getColumn(2).setMinWidth(200);
		tagTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		refreshTags();
		JScrollPane scrollPane = new JScrollPane(tagTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(removeButton);
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		addButton.addActionListener(e -> {
			var dialog = new TagEditDialog(this, null, ds);
			dialog.setVisible(true);
			refreshTags();
		});
		editButton.addActionListener(e -> {
			Tag tag = tagTableModel.getTagAt(tagTable.getSelectedRow());
			if (tag == null) return;
			var dialog = new TagEditDialog(this, tag, ds);
			dialog.setVisible(true);
			refreshTags();
		});
		removeButton.addActionListener(e -> {
			Tag tag = tagTableModel.getTagAt(tagTable.getSelectedRow());
			if (tag == null) return;
			if (SwingUtils.confirm(this, "Are you sure you want to remove this tag?")) {
				new TagRepository(ds).deleteTag(tag.id());
				refreshTags();
			}
		});

		return mainPanel;
	}

	private void refreshTags() {
		tagTableModel.setTags(new TagRepository(ds).findAll());
	}
}
