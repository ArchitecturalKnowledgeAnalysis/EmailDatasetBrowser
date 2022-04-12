package nl.andrewl.emaildatasetbrowser.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.emaildatasetbrowser.control.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

/**
 * A panel that provides navigation components so that users can traverse back
 * through the path of emails they've read.
 */
public class EmailNavigationPanel extends JPanel {
	private final EmailViewPanel emailViewPanel;
	private final Stack<EmailEntry> navigationStack = new Stack<>();
	private final JPanel breadcrumbPanel;
	private final JScrollPane breadcrumbScrollPane;
	private final JButton backButton = new JButton("Back");

	public EmailNavigationPanel(EmailViewPanel emailViewPanel) {
		super(new BorderLayout());
		this.emailViewPanel = emailViewPanel;
		this.breadcrumbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		breadcrumbScrollPane = new JScrollPane(breadcrumbPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(breadcrumbScrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		backButton.addActionListener(e -> {
			navigateBack();
			emailViewPanel.fetchAndSetEmail(navigationStack.peek().messageId());
		});
		buttonPanel.add(backButton);
		add(buttonPanel, BorderLayout.WEST);
	}

	private void update() {
		SwingUtilities.invokeLater(() -> {
			updateButtons();
			updateBreadcrumbPanel();
		});
	}

	public void navigateTo(EmailEntry email) {
		int idx = -1;
		for (int i = 0; i < navigationStack.size(); i++) {
			var e = navigationStack.get(i);
			if (e.equals(email)) {
				idx = i;
			}
		}
		if (idx != -1) {
			for (int i = navigationStack.size() - 1; i >= idx; i--) {
				navigationStack.pop();
			}
		}

		navigationStack.push(email);
		update();
	}

	public void navigateBack() {
		navigationStack.pop();
		update();
	}

	public void clear() {
		navigationStack.clear();
		update();
	}

	private void updateButtons() {
		backButton.setEnabled(navigationStack.size() > 1);
	}

	private void updateBreadcrumbPanel() {
		breadcrumbPanel.removeAll();
		for (int i = 0; i < navigationStack.size(); i++) {
			var email = navigationStack.get(i);
			JButton button = new JButton(email.subject().substring(0, Math.min(email.subject().length(), 24)));
			button.setToolTipText("Subject: %s\nFrom: %s\nId: %s".formatted(email.subject(), email.sentFrom(), email.messageId()));
			button.setForeground(SwingUtils.getColor(email.messageId()));
			final int idx = i;
			button.addActionListener(e -> {
				for (int k = navigationStack.size() - 1; k > idx; k--) navigationStack.pop();
				emailViewPanel.fetchAndSetEmail(email.messageId());
				update();
			});
			breadcrumbPanel.add(button);
			if (i < navigationStack.size() - 1) {
				breadcrumbPanel.add(new JLabel(" > "));
			}
		}
		breadcrumbPanel.repaint();
		breadcrumbScrollPane.revalidate();
	}
}
