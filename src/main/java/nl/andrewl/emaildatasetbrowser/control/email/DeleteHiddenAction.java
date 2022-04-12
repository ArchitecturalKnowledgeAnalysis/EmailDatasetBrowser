package nl.andrewl.emaildatasetbrowser.control.email;

import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class DeleteHiddenAction extends AbstractAction {
	private final EmailViewPanel emailViewPanel;

	public DeleteHiddenAction(EmailViewPanel emailViewPanel) {
		super("Delete Hidden");
		this.emailViewPanel = emailViewPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (emailViewPanel.getCurrentDataset() == null) return;
		int result = JOptionPane.showConfirmDialog(
				emailViewPanel,
				"Are you sure you want to delete all hidden emails? This cannot be undone.",
				"Confirm Deletion",
				JOptionPane.YES_NO_OPTION
		);
		if (result == JOptionPane.YES_OPTION) {
			new EmailRepository(emailViewPanel.getCurrentDataset()).deleteAllHidden();
		}
	}
}
