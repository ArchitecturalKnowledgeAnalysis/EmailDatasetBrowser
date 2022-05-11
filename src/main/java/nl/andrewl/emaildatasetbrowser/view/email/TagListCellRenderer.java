package nl.andrewl.emaildatasetbrowser.view.email;

import nl.andrewl.email_indexer.data.Tag;
import nl.andrewl.emaildatasetbrowser.util.ColorHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Renderer for the list of tags that belong to an email. Renders each tag with
 * a random color determined by the hashcode of its name.
 */
public class TagListCellRenderer implements ListCellRenderer<Tag> {
	private final JLabel label = new JLabel();

	public TagListCellRenderer() {
		this.label.setOpaque(true);
		this.label.setFont(this.label.getFont().deriveFont(Font.BOLD).deriveFont(16.0f));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Tag> list, Tag value, int index, boolean isSelected, boolean cellHasFocus) {
		Color foregroundColor = ColorHelper.getColor(value.name());

		label.setText(value.name());
		label.setForeground(foregroundColor);
		if (isSelected) {
			label.setBackground(list.getSelectionBackground());
		} else {
			label.setBackground(null);
		}
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return label;
	}
}
