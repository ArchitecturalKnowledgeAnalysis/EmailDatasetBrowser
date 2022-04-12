package nl.andrewl.emaildatasetbrowser.view.email;

import nl.andrewl.email_indexer.data.EmailEntry;

import javax.swing.*;
import java.awt.*;

/**
 * A panel containing some basic components for viewing the body of an email.
 */
public class EmailBodyPanel extends JPanel implements EmailViewListener {
	private final JTextPane textPane = new JTextPane();

	public EmailBodyPanel() {
		super(new BorderLayout());
		textPane.setEditable(false);
		textPane.setFont(new Font("monospaced", textPane.getFont().getStyle(), 16));
		textPane.setBackground(textPane.getBackground().darker());
		JScrollPane scrollPane = new JScrollPane(textPane);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void setEmail(EmailEntry email) {
		if (email != null) {
			textPane.setText(email.body());
			textPane.setCaretPosition(0);
		} else {
			textPane.setText(null);
		}
	}

	@Override
	public void emailUpdated(EmailEntry email) {
		setEmail(email);
	}
}
