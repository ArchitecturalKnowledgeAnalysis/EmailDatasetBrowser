package nl.andrewl.emaildatasetbrowser.view.search.export;

import java.awt.*;
import java.util.HashMap;

import javax.swing.*;

import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

public class LuceneSearchExportPanel extends JDialog {
    private static final String PANEL_TITLE = "Lucene Search Export";

    private final JPanel contentPanel = new JPanel();

    private final HashMap<String, QueryExportParameterPanel> parameterPanels = new HashMap<>();
    private String currentExportPanel;

    public LuceneSearchExportPanel(Window owner, LuceneSearchPanel searchPanel) {
        super(owner, PANEL_TITLE, ModalityType.TOOLKIT_MODAL);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        JComboBox<String> exportTypes = new JComboBox<>();
        exportTypes.addActionListener(e -> {
            setExportPanel((String) exportTypes.getSelectedItem());
        });
        p.add(exportTypes);

        QueryExportParameterPanel pdfPanel = new PdfExportParameterPanel(searchPanel);
        parameterPanels.put(pdfPanel.getName(), pdfPanel);
        contentPanel.add(pdfPanel);

        QueryExportParameterPanel txtPanel = new PlainTextExportParameterPanel(searchPanel);
        parameterPanels.put(txtPanel.getName(), txtPanel);
        contentPanel.add(txtPanel);

        p.add(contentPanel);
        parameterPanels.values().forEach(panel -> exportTypes.addItem(panel.getName()));
        setExportPanel((String) exportTypes.getSelectedItem());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> {
            ProgressDialog progress = ProgressDialog.minimalText(searchPanel, "Exporting Query Results");
            progress.append("Generating export for query: \"%s\"".formatted(searchPanel.getQuery()));
            this.parameterPanels.get(currentExportPanel).export()
                    .whenComplete((v, throwable) -> {
                        if (throwable != null) {
                            progress.append("Export completed exceptionally...");
                        } else {
                            progress.append("Export completed!");
                        }
                        progress.done();
                    });
            dispose();
        });
        buttonPanel.add(exportButton);

        p.add(buttonPanel);

        setContentPane(p);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBounds(0, 0, 300, 225);
        setLocationRelativeTo(owner);
    }

    private void setExportPanel(String newValue) {
        if (this.currentExportPanel != null) {
            parameterPanels.get(this.currentExportPanel).setVisible(false);
        }
        this.currentExportPanel = newValue;
        parameterPanels.get(newValue).setVisible(true);
        contentPanel.revalidate();
    }

    /**
     * Begins showing the dialog. Use this instead of calling setVisible(true).
     */
    public void activate() {
        new Thread(() -> {
            setVisible(true);
        }).start();
    }
}
