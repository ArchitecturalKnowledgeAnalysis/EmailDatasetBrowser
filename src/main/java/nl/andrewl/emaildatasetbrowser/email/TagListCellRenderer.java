package nl.andrewl.emaildatasetbrowser.email;

import nl.andrewl.emaildatasetbrowser.control.SwingUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Renderer for the list of tags that belong to an email. Renders each tag with
 * a random color determined by the hashcode of its name.
 */
public class TagListCellRenderer implements ListCellRenderer<String> {
	private final JLabel label = new JLabel();

	public TagListCellRenderer() {
		this.label.setOpaque(true);
		this.label.setFont(this.label.getFont().deriveFont(Font.BOLD).deriveFont(16.0f));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
		Color foregroundColor = SwingUtils.getColor(value);

		label.setText(value);
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
