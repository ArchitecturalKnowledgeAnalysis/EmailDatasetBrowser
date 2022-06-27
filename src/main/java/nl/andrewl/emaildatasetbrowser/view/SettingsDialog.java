package nl.andrewl.emaildatasetbrowser.view;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.*;
import java.util.prefs.Preferences;

import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.search.EmailTreeSelectionListener;
import nl.andrewl.emaildatasetbrowser.view.search.SimpleBrowsePanel;

public class SettingsDialog extends JDialog {
    private final Preferences prefs;

    public SettingsDialog(EmailDatasetBrowser browser) {
        super(browser, "Settings");
        this.prefs = EmailDatasetBrowser.getPreferences();

        JPanel p = new JPanel(new GridLayout(3, 1));
        p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        p.add(buildSettingsCheckbox(EmailDatasetBrowser.PREF_LOAD_LAST_DS, false,
                "Open last dataset on start-up"));

        p.add(buildSettingsCheckbox(EmailTreeSelectionListener.PREF_AUTO_OPEN, true,
                "Automatically expand email in tree-view"));

        p.add(buildSettingsSpinner(SimpleBrowsePanel.PREF_BROWSE_PAGE_SIZE, 20, 1, 100, 1, "Simple browse page size"));

        setContentPane(p);

        setPreferredSize(new Dimension(400, 200));
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

    private LabelledField buildSettingsSpinner(String key, int defaultValue, int min, int max, int stepsize,
            String text) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(prefs.getInt(key, defaultValue), min, max, stepsize));
        spinner.addChangeListener((e) -> prefs.putInt(key, (int) spinner.getValue()));
        return new LabelledField(text, spinner);
    }
}
