package nl.andrewl.emaildatasetbrowser.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * A panel that shows some auxiliary details about an email, aside from the main
 * body of the email.
 */
public class EmailInfoPanel extends JPanel implements EmailViewListener {
	private final EmailViewPanel parent;

	private final JLabel messageIdLabel = new JLabel();
	private final JLabel subjectLabel = new JLabel();
	private final JButton inReplyToButton = new JButton("None");
	private ActionListener inReplyToActionListener;
	private final JLabel dateLabel = new JLabel();
	private final JLabel sentFromLabel = new JLabel();
	private final TagPanel tagPanel;
	private final RepliesPanel repliesPanel;

	public EmailInfoPanel(EmailViewPanel parent) {
		super(new GridBagLayout());
		this.parent = parent;
		this.tagPanel = new TagPanel(parent);
		parent.addListener(tagPanel);
		this.repliesPanel = new RepliesPanel(parent);
		parent.addListener(repliesPanel);
		buildUI();
	}

	private void buildUI() {
		GridBagConstraints labelConstraint = new GridBagConstraints();
		labelConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		labelConstraint.weightx = 0.01;
		labelConstraint.weighty = 0.01;
		labelConstraint.insets = new Insets(3, 3, 3, 3);
		labelConstraint.gridx = 0;
		labelConstraint.gridy = 0;
		String[] labels = new String[]{"Message Id", "Subject", "In Reply To", "Sent From", "Date"};
		for (var l : labels) {
			var label = new JLabel(l);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			this.add(label, labelConstraint);
			labelConstraint.gridy++;
		}

		GridBagConstraints fieldConstraint = new GridBagConstraints();
		fieldConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		fieldConstraint.weightx = 0.99;
		fieldConstraint.weighty = 0.01;
		fieldConstraint.insets = new Insets(3, 3, 3, 3);
		fieldConstraint.fill = GridBagConstraints.HORIZONTAL;
		fieldConstraint.gridx = 1;
		fieldConstraint.gridy = 0;
		Component[] values = new Component[]{messageIdLabel, subjectLabel, inReplyToButton, sentFromLabel, dateLabel};
		for (var v : values) {
			this.add(v, fieldConstraint);
			fieldConstraint.gridy++;
		}

		// Add complex sections.
		GridBagConstraints subsectionConstraint = new GridBagConstraints();
		subsectionConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		subsectionConstraint.weightx = 1;
		subsectionConstraint.weighty = 0.99;
		subsectionConstraint.fill = GridBagConstraints.BOTH;
		subsectionConstraint.gridwidth = 2;
		subsectionConstraint.gridy = values.length;
		subsectionConstraint.insets = new Insets(3, 3, 3, 3);
		JPanel subPanel = new JPanel(new GridLayout(2, 1));
		subPanel.add(tagPanel);
		subPanel.add(repliesPanel);
		this.add(subPanel, subsectionConstraint);
	}

	public void setEmail(EmailEntry email) {
		if (email != null) {
			this.messageIdLabel.setText(email.messageId());
			this.subjectLabel.setText(email.subject());
			this.dateLabel.setText(email.date().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm:ss Z")));
			this.sentFromLabel.setText(email.sentFrom());
			String inReplyToButtonText;
			Optional<EmailEntry> inReplyTo = Optional.empty();
			if (parent.getCurrentDataset() != null) {
				inReplyTo = new EmailRepository(parent.getCurrentDataset()).findEmailById(email.inReplyTo());
			}
			if (email.inReplyTo() == null || email.inReplyTo().isBlank() || inReplyTo.isEmpty()) {
				inReplyToButtonText = "None";
				inReplyToButton.setEnabled(false);
			} else {
				var parentEmail = inReplyTo.get();
				inReplyToButtonText = "<html><strong>%s</strong><br>by <em>%s</em></html>".formatted(parentEmail.subject(), parentEmail.sentFrom());
				inReplyToButton.setEnabled(true);
			}
			this.inReplyToButton.setText(inReplyToButtonText);
			if (inReplyToActionListener != null) inReplyToButton.removeActionListener(inReplyToActionListener);
			inReplyToActionListener = e -> {
				SwingUtilities.invokeLater(() -> parent.navigateTo(email.inReplyTo()));
			};
			inReplyToButton.addActionListener(inReplyToActionListener);
		} else {
			this.inReplyToButton.setEnabled(false);
		}
	}

	@Override
	public void emailUpdated(EmailEntry email) {
		setEmail(email);
		setVisible(email != null);
	}
}
