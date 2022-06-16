package nl.andrewl.emaildatasetbrowser.view.tag;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.Tag;
import nl.andrewl.email_indexer.data.TagRepository;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

	public TagManagerDialog(EmailDatasetBrowser browser, EmailDataset ds) {
		super(browser, "Tag Manager", ModalityType.APPLICATION_MODAL);
		this.ds = ds;

		setContentPane(buildUI(browser));
		setPreferredSize(new Dimension(500, 500));
		pack();
		setLocationRelativeTo(browser);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private Container buildUI(EmailDatasetBrowser browser) {
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
		tagTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tagTable.rowAtPoint(e.getPoint());
				if (row != -1 && e.getClickCount() == 2) {
					tagTable.setRowSelectionInterval(row, row);
					Tag tag = tagTableModel.getTagAt(tagTable.getSelectedRow());
					if (tag == null) return;
					var dialog = new TagEditDialog(browser, tag, ds);
					dialog.setVisible(true);
					tagTableModel.refreshTags(ds);
				}
			}
		});
		tagTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		tagTable.getColumnModel().getColumn(1).setPreferredWidth(120);
		tagTable.getColumnModel().getColumn(2).setPreferredWidth(20);
		tagTable.getColumnModel().getColumn(3).setMinWidth(200);
		tagTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		tagTableModel.refreshTags(ds);
		JScrollPane scrollPane = new JScrollPane(tagTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(removeButton);
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		addButton.addActionListener(e -> {
			var dialog = new TagEditDialog(browser, null, ds);
			dialog.setVisible(true);
			tagTableModel.refreshTags(ds);
		});
		editButton.addActionListener(e -> {
			Tag tag = tagTableModel.getTagAt(tagTable.getSelectedRow());
			if (tag == null) return;
			var dialog = new TagEditDialog(browser, tag, ds);
			dialog.setVisible(true);
			tagTableModel.refreshTags(ds);
		});
		removeButton.addActionListener(e -> {
			Tag tag = tagTableModel.getTagAt(tagTable.getSelectedRow());
			if (tag == null) return;
			if (SwingUtils.confirm(this, "Are you sure you want to remove this tag?")) {
				new TagRepository(ds).deleteTag(tag.id());
				tagTableModel.refreshTags(ds);
			}
		});

		return mainPanel;
	}
}
