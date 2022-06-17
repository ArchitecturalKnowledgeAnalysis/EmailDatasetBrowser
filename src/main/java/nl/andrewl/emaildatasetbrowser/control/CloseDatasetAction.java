package nl.andrewl.emaildatasetbrowser.control;

import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CloseDatasetAction extends AbstractAction {
	private final EmailDatasetBrowser browser;

	public CloseDatasetAction(EmailDatasetBrowser browser) {
		super("Close Dataset");
		this.browser = browser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		browser.closeDataset(null, true);
	}
}
