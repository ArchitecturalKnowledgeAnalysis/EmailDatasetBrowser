package nl.andrewl.emaildatasetbrowser.control.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import java.awt.event.ActionEvent;
import java.util.concurrent.ForkJoinPool;

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
		ProgressDialog progress = ProgressDialog.minimalText(emailViewPanel, "Hide Emails by Body");
		progress.append("Hiding all emails whose body matches the currently selected email.");
		ForkJoinPool.commonPool().submit(() -> {
			long count = new EmailRepository(emailViewPanel.getCurrentDataset())
					.hideAllEmailsByBody(emailViewPanel.getEmail().body());
			progress.append("Hid %d emails.".formatted(count));
			progress.done();
		});
	}
}
