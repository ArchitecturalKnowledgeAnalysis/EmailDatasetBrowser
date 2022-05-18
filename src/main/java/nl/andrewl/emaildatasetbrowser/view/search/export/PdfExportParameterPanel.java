package nl.andrewl.emaildatasetbrowser.view.search.export;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.query.PdfQueryExporter;
import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

public final class PdfExportParameterPanel extends ExportParameterPanel {

    private final LuceneSearchPanel searchPanel;
    private final JSpinner maxResultsSpinner = new JSpinner(
            new SpinnerNumberModel(100, 1, 10000, 1));
    private final JCheckBox toggleButton = new JCheckBox("Separate mailing threads");
    private final JTextField pathField = new JTextField();

    private Path outputPath = null;

    public PdfExportParameterPanel(LuceneSearchPanel searchPanel) {
        super(searchPanel);
        this.searchPanel = searchPanel;

        add(new JLabel("Max. result count:"));
        add(maxResultsSpinner);

        JButton searchDirectoryButton = new JButton("Select Directory");
        JButton searchFileButton = new JButton("Select File");
        toggleButton.addActionListener(e -> {
            searchDirectoryButton.setVisible(toggleButton.isSelected());
            searchFileButton.setVisible(!toggleButton.isSelected());
            outputPath = null;
            updatePathField();
        });
        add(toggleButton);

        searchDirectoryButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            this.outputPath = fc.getSelectedFile().toPath();
            updatePathField();
        });
        searchDirectoryButton.setVisible(false);
        add(searchDirectoryButton);

        searchFileButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            fc.setAcceptAllFileFilterUsed(true);
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            this.outputPath = fc.getSelectedFile().toPath();
            updatePathField();
        });
        add(searchFileButton);

        pathField.setEditable(false);
        add(pathField);
    }

    @Override
    public String getKey() {
        return "PDF File";
    }

    @Override
    public CompletableFuture<Void> export() {
        QueryExportParams params = new QueryExportParams()
                .withQuery(this.searchPanel.getQuery())
                .withMaxResultCount((int) this.maxResultsSpinner.getValue())
                .withSeparateEmailThreads(this.toggleButton.isSelected());
        PdfQueryExporter queryExporter = new PdfQueryExporter(params);
        return queryExporter.export(this.searchPanel.getDataset(), this.outputPath);
    }

    private void updatePathField() {
        if (this.outputPath != null) {
            this.pathField.setText(this.outputPath.toString());
        } else {
            this.pathField.setText("");
        }
    }
}
