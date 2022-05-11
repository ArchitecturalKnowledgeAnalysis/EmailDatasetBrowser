package nl.andrewl.emaildatasetbrowser.view.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * A panel that shows a list of all replies to an email.
 */
public class RepliesPanel extends JPanel implements EmailViewListener {
	private final EmailViewPanel parent;

	private final JPanel buttonPanel;

	public RepliesPanel(EmailViewPanel parent) {
		super(new BorderLayout());
		this.parent = parent;
		this.setBorder(BorderFactory.createTitledBorder("Replies"));
		this.buttonPanel = new JPanel();
		this.buttonPanel.setLayout(new BoxLayout(this.buttonPanel, BoxLayout.PAGE_AXIS));
		JScrollPane scrollPane = new JScrollPane(buttonPanel);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void setEmail(EmailEntry email) {
		buttonPanel.removeAll();
		if (email != null) {
			ForkJoinPool.commonPool().execute(() -> {
				var repo = new EmailRepository(parent.getCurrentDataset());
				var replies = repo.findAllReplies(email.id());
				List<JButton> buttonsToAdd = new ArrayList<>();
				for (var reply : replies) {
					JButton button = new JButton("<html><strong>%s</strong><br>by <em>%s</em></html>".formatted(reply.subject(), reply.sentFrom()));
					button.addActionListener(e -> SwingUtilities.invokeLater(() -> parent.fetchAndSetEmail(reply.id())));
					buttonsToAdd.add(button);
				}
				SwingUtilities.invokeLater(() -> {
					for (var button : buttonsToAdd) buttonPanel.add(button);
					this.revalidate();
					this.repaint();
				});
			});
		}
	}

	@Override
	public void emailUpdated(EmailEntry email) {
		setEmail(email);
	}
}
