package nl.andrewl.emaildatasetbrowser.control.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.email.EmailViewPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HideAllByBodyAction extends EmailAction {
	public HideAllByBodyAction(EmailViewPanel emailViewPanel) {
		super("Hide all by body", emailViewPanel);
	}

	@Override
	protected boolean shouldBeEnabled(EmailEntry email) {
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		long count = new EmailRepository(emailViewPanel.getCurrentDataset())
				.hideAllEmailsByBody(emailViewPanel.getEmail().body());
		JOptionPane.showMessageDialog(
				emailViewPanel,
				"Hid %d emails.".formatted(count),
				"Hid Emails",
				JOptionPane.INFORMATION_MESSAGE
		);
	}
}
