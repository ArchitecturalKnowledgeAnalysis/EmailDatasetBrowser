package nl.andrewl.emaildatasetbrowser.view;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.*;
import java.util.prefs.Preferences;

import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;

public class SettingsDialog extends JDialog {
    private final Preferences prefs;

    public SettingsDialog(EmailDatasetBrowser browser) {
        super(browser, "Settings");
        this.prefs = EmailDatasetBrowser.getPreferences();

        JPanel p = new JPanel(new BorderLayout());

        p.add(buildOpenLastDatasetButton());

        setContentPane(p);

        setPreferredSize(new Dimension(400, 300));
        pack();
        setLocationRelativeTo(browser);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JCheckBox buildOpenLastDatasetButton() {
        JCheckBox openLastDatasetCheckBox = new JCheckBox("Open last dataset on start-up");
        openLastDatasetCheckBox.setSelected(prefs.getBoolean(EmailDatasetBrowser.PREF_LOAD_LAST_DS, false));
        openLastDatasetCheckBox.addActionListener((e) -> prefs.putBoolean(EmailDatasetBrowser.PREF_LOAD_LAST_DS,
                ((JCheckBox) e.getSource()).isSelected()));
        return openLastDatasetCheckBox;
    }
}
