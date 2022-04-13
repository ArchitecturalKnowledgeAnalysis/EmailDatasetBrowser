package nl.andrewl.emaildatasetbrowser.control.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import java.awt.event.ActionEvent;
import java.util.concurrent.ForkJoinPool;

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
		ProgressDialog progress = ProgressDialog.minimalText(emailViewPanel, "Hiding Emails by Author");
		progress.append("Hiding all emails sent by \"%s\".".formatted(emailAddress));
		ForkJoinPool.commonPool().submit(() -> {
			long count = new EmailRepository(emailViewPanel.getCurrentDataset())
					.hideAllEmailsBySentFrom('%' + emailAddress + '%');
			progress.append("Hid %d emails.".formatted(count));
			progress.done();
		});
	}

	@Override
	protected boolean shouldBeEnabled(EmailEntry email) {
		return true;
	}
}
