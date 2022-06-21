package nl.andrewl.emaildatasetbrowser.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.SettingsDialog;

public class OpenSettingsAction extends AbstractAction {
    private final EmailDatasetBrowser browser;

    public OpenSettingsAction(EmailDatasetBrowser browser) {
		super("Settings");
		this.browser = browser;
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        SettingsDialog settings = new SettingsDialog(browser);
        settings.setVisible(true);
    }
}
