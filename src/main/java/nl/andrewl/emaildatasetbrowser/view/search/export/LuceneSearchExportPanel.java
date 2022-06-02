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
        super(owner, PANEL_TITLE, ModalityType.APPLICATION_MODAL);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        JComboBox<String> exportTypes = new JComboBox<>();
        exportTypes.addActionListener(e -> {
            setExportPanel((String) exportTypes.getSelectedItem());
        });
        p.add(exportTypes);

        QueryExportParameterPanel csvPanel = new CsvExportParameterPanel(searchPanel);
        parameterPanels.put(csvPanel.getName(), csvPanel);
        contentPanel.add(csvPanel);

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
            var progress = new ProgressDialog(
                    SwingUtilities.getWindowAncestor(searchPanel),
                    "Exporting Query Results",
                    null,
                    true,
                    false,
                    true);
            progress.activate();
            progress.append("Generating export for query: \"%s\"".formatted(searchPanel.getQuery()));
            this.parameterPanels.get(currentExportPanel).export()
                    .whenComplete((v, throwable) -> {
                        if (throwable != null) {
                            progress.append("Export completed exceptionally with message:");
                            progress.append(throwable.getMessage());
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
        setBounds(0, 0, 400, 225);
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
}
