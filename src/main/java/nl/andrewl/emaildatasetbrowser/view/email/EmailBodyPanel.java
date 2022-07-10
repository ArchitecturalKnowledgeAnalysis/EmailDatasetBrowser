package nl.andrewl.emaildatasetbrowser.view.email;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.util.HTMLHelper;

/**
 * A panel containing some basic components for viewing the body of an email.
 */
public class EmailBodyPanel extends JPanel implements EmailViewListener {
	public static final String PREF_SCROLL_SPEED = "pref_email_body_scroll_speed";
	private final JTextPane textPane = new JTextPane();
	private final JScrollPane scrollPane = new JScrollPane(textPane);

	public EmailBodyPanel() {
		super(new BorderLayout());
		textPane.setEditable(false);
		textPane.setFont(new Font("monospaced", textPane.getFont().getStyle(), 16));
		textPane.setBackground(textPane.getBackground().darker());
		updateScrollSpeed();
		add(scrollPane, BorderLayout.CENTER);
	}

	private void setEmail(EmailEntry email) {
		if (email != null) {
			if (HTMLHelper.isHtml(email.body())) {
				textPane.setContentType("text/html");
			} else {
				textPane.setContentType("text/plain");
			}
			textPane.setText(email.body());
			textPane.setCaretPosition(0);
		} else {
			textPane.setText(null);
		}
		updateScrollSpeed();
	}

	private void updateScrollSpeed() {
		int scrollSpeed = EmailDatasetBrowser.getPreferences().getInt(PREF_SCROLL_SPEED, 100);
		scrollPane.getVerticalScrollBar().setUnitIncrement(scrollSpeed);
	}

	@Override
	public void emailUpdated(EmailEntry email) {
		setEmail(email);
	}
}
