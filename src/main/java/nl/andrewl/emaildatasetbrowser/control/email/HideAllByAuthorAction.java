package nl.andrewl.emaildatasetbrowser.control.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.email.EmailViewPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An action which hides the current author by their name.
 */
public class HideAllByAuthorAction extends EmailAction {
	public HideAllByAuthorAction(EmailViewPanel emailViewPanel) {
		super("Hide by Author", emailViewPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		var email = emailViewPanel.getEmail();
		String emailAddress = email.sentFrom().substring(email.sentFrom().lastIndexOf('<') + 1, email.sentFrom().length() - 1);
		long count = new EmailRepository(emailViewPanel.getCurrentDataset())
				.hideAllEmailsBySentFrom('%' + emailAddress + '%');
		JOptionPane.showMessageDialog(
				emailViewPanel,
				"Hid %d emails.".formatted(count),
				"Hid Emails",
				JOptionPane.INFORMATION_MESSAGE
		);
	}

	@Override
	protected boolean shouldBeEnabled(EmailEntry email) {
		return true;
	}
}
