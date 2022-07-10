package nl.andrewl.emaildatasetbrowser.view.tag;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.Tag;
import nl.andrewl.email_indexer.data.TagRepository;
import nl.andrewl.emaildatasetbrowser.view.DatasetChangeListener;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A table model for the set of tags in a dataset.
 */
public class TagTableModel extends AbstractTableModel implements DatasetChangeListener {
	private final List<Tag> tags = new ArrayList<>();
	private final Map<Tag, Long> taggedEmailCounts = new HashMap<>();

	/**
	 * Refreshes this model's set of tags using the given dataset.
	 * @param ds The dataset to fetch tags from.
	 */
	public void refreshTags(EmailDataset ds) {
		tags.clear();
		taggedEmailCounts.clear();
		if (ds != null) {
			var repo = new TagRepository(ds);
			tags.addAll(repo.findAll());
			for (var tag : tags) {
				taggedEmailCounts.put(tag, repo.countTaggedEmails(tag.id()));
			}
		}
		fireTableDataChanged();
	}

	public Tag getTagAt(int row) {
		if (row < 0 || row > tags.size() - 1) {
			return null;
		}
		return tags.get(row);
	}

	@Override
	public int getRowCount() {
		return tags.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Tag tag = getTagAt(rowIndex);
		if (tag == null) return null;
		return switch (columnIndex) {
			case 0 -> tag.id();
			case 1 -> tag.name();
			case 2 -> taggedEmailCounts.get(tag);
			case 3 -> tag.description();
			default -> null;
		};
	}

	@Override
	public String getColumnName(int column) {
		return switch (column) {
			case 0 -> "Id";
			case 1 -> "Name";
			case 2 -> "Emails";
			case 3 -> "Description";
			default -> null;
		};
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void datasetChanged(EmailDataset ds) {
		refreshTags(ds);
	}

	@Override
	public void tagsChanged(EmailDataset ds) {
		refreshTags(ds);
	}
}
