package nl.andrewl.emaildatasetbrowser.view;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.*;
import java.util.prefs.Preferences;

import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.search.EmailTreeSelectionListener;

public class SettingsDialog extends JDialog {
    private final Preferences prefs;

    public SettingsDialog(EmailDatasetBrowser browser) {
        super(browser, "Settings");
        this.prefs = EmailDatasetBrowser.getPreferences();

        JPanel p = new JPanel(new GridLayout(2, 1));

        p.add(buildSettingsCheckbox(EmailDatasetBrowser.PREF_LOAD_LAST_DS, false,
                "Open last dataset on start-up"));

        p.add(buildSettingsCheckbox(EmailTreeSelectionListener.PREF_AUTO_OPEN, true,
                "Automatically expand email in tree-view"));

        setContentPane(p);

        setPreferredSize(new Dimension(400, 100));
        pack();
        setLocationRelativeTo(browser);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JCheckBox buildSettingsCheckbox(String key, boolean defaultValue, String text) {
        JCheckBox openLastDatasetCheckBox = new JCheckBox(text);
        openLastDatasetCheckBox.setSelected(prefs.getBoolean(key, defaultValue));
        openLastDatasetCheckBox
                .addActionListener((e) -> prefs.putBoolean(key, ((JCheckBox) e.getSource()).isSelected()));
        return openLastDatasetCheckBox;
    }
}
