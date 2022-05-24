package nl.andrewl.emaildatasetbrowser.view.tag;

import nl.andrewl.email_indexer.data.Tag;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * A table model for the set of tags in a dataset.
 */
public class TagTableModel extends AbstractTableModel {
	private final List<Tag> tags = new ArrayList<>();

	public void setTags(List<Tag> tags) {
		this.tags.clear();
		this.tags.addAll(tags);
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
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Tag tag = getTagAt(rowIndex);
		if (tag == null) return null;
		return switch (columnIndex) {
			case 0 -> tag.id();
			case 1 -> tag.name();
			case 2 -> tag.description();
			default -> null;
		};
	}

	@Override
	public String getColumnName(int column) {
		return switch (column) {
			case 0 -> "Id";
			case 1 -> "Name";
			case 2 -> "Description";
			default -> null;
		};
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}
