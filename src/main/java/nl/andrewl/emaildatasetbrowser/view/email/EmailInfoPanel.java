package nl.andrewl.emaildatasetbrowser.view.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.view.common.SelectableLabel;

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

	private final SelectableLabel idLabel = new SelectableLabel();
	private final SelectableLabel subjectLabel = new SelectableLabel();
	private final JButton inReplyToButton = new JButton("None");
	private ActionListener inReplyToActionListener;
	private final SelectableLabel dateLabel = new SelectableLabel();
	private final SelectableLabel sentFromLabel = new SelectableLabel();
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
		String[] labels = new String[]{"Id", "Subject", "In Reply To", "Sent From", "Date"};
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
		Component[] values = new Component[]{idLabel, subjectLabel, inReplyToButton, sentFromLabel, dateLabel};
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
			this.idLabel.setText(Long.toString(email.id()));
			this.subjectLabel.setText("<html>%s</html>".formatted(email.subject()));
			this.dateLabel.setText(email.date().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm:ss Z")));
			this.sentFromLabel.setText("<html>%s</html>".formatted(email.sentFrom()));
			String inReplyToButtonText;
			Optional<EmailEntry> optionalParent = Optional.empty();
			if (this.parent.getCurrentDataset() != null && email.parentId() != null) {
				optionalParent = new EmailRepository(this.parent.getCurrentDataset()).findEmailById(email.parentId());
			}
			if (inReplyToActionListener != null) inReplyToButton.removeActionListener(inReplyToActionListener);
			if (optionalParent.isEmpty()) {
				inReplyToButtonText = "None";
				inReplyToButton.setEnabled(false);
			} else {
				var parentEmail = optionalParent.get();
				inReplyToButtonText = "<html><strong>%s</strong><br>by <em>%s</em></html>".formatted(parentEmail.subject(), parentEmail.sentFrom());
				inReplyToButton.setEnabled(true);
				inReplyToActionListener = e -> {
					SwingUtilities.invokeLater(() -> this.parent.fetchAndSetEmail(email.parentId()));
				};
				inReplyToButton.addActionListener(inReplyToActionListener);
			}
			this.inReplyToButton.setText(inReplyToButtonText);
		} else {
			this.inReplyToButton.setEnabled(false);
		}
	}

	@Override
	public void emailUpdated(EmailEntry email) {
		setEmail(email);
		setVisible(email != null);
	}

	public TagPanel getTagPanel() {
		return tagPanel;
	}
}
