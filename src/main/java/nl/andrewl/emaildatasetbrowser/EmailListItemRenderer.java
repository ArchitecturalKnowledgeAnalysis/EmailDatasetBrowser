package nl.andrewl.emaildatasetbrowser;

import nl.andrewl.email_indexer.data.EmailEntryPreview;

import javax.swing.*;
import java.awt.*;

public class EmailListItemRenderer extends JLabel implements ListCellRenderer<EmailEntryPreview> {
	public EmailListItemRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends EmailEntryPreview> list, EmailEntryPreview value, int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setText(value.subject());
		return this;
	}
}
