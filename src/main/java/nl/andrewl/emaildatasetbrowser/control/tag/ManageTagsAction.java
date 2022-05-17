package nl.andrewl.emaildatasetbrowser.control.tag;

import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.tag.TagManagerDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An action that opens the tag manager dialog so the user can edit and view
 * the set of tags in the current dataset.
 */
public class ManageTagsAction extends AbstractAction {
	private final EmailDatasetBrowser browser;

	public ManageTagsAction(EmailDatasetBrowser browser) {
		super("Manage Tags");
		this.browser = browser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (browser.getCurrentDataset() != null) {
			var dialog = new TagManagerDialog(browser, browser.getCurrentDataset());
			dialog.setVisible(true);
			// After tags might have been changed, refresh the browser's email tag panel.
			if (browser.getEmailViewPanel().getEmail() != null) {
				browser.getEmailViewPanel().getInfoPanel().getTagPanel().refreshTags();
			}
		}
	}
}
