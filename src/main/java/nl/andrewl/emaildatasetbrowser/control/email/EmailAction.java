package nl.andrewl.emaildatasetbrowser.control.email;

import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewListener;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import javax.swing.*;

public abstract class EmailAction extends AbstractAction implements EmailViewListener {
	protected final EmailViewPanel emailViewPanel;

	protected EmailAction(String name, EmailViewPanel emailViewPanel) {
		super(name);
		this.emailViewPanel = emailViewPanel;
		emailViewPanel.addListener(this);
	}

	@Override
	public void emailUpdated(EmailEntry email) {
		setEnabled(email != null && shouldBeEnabled(email));
	}

	protected abstract boolean shouldBeEnabled(EmailEntry email);
}
