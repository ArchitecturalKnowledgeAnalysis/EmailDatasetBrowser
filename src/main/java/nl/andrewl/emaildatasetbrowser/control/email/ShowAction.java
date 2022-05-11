package nl.andrewl.emaildatasetbrowser.control.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import java.awt.event.ActionEvent;

public class ShowAction extends EmailAction {
	public ShowAction(EmailViewPanel emailViewPanel) {
		super("Show", emailViewPanel);
	}

	@Override
	protected boolean shouldBeEnabled(EmailEntry email) {
		return email.hidden();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new EmailRepository(emailViewPanel.getCurrentDataset()).showEmail(emailViewPanel.getEmail().id());
		emailViewPanel.refresh();
	}
}
