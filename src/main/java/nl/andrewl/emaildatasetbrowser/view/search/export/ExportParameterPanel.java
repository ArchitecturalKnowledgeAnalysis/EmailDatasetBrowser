package nl.andrewl.emaildatasetbrowser.view.search.export;

import java.awt.FlowLayout;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.export.query.QueryExporter;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

public abstract class ExportParameterPanel extends JPanel {
    private LuceneSearchPanel searchPanel;

    private final JSpinner maxResultsSpinner = new JSpinner(
            new SpinnerNumberModel(100, 1, 10000, 1));
    private final JCheckBox toggleButton = new JCheckBox("Separate mailing threads");
    private final JTextField pathField = new JTextField();

    private Path outputPath = null;

    public ExportParameterPanel(LuceneSearchPanel searchPanel) {
        super(new FlowLayout(FlowLayout.LEFT));
        this.searchPanel = searchPanel;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setVisible(false);

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
            fc.setFileFilter(buildFileNameFilter());
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

    private void updatePathField() {
        if (this.outputPath != null) {
            this.pathField.setText(this.outputPath.toString());
        } else {
            this.pathField.setText("");
        }
    }

    public CompletableFuture<Void> export() {
        QueryExportParams params = new QueryExportParams()
                .withQuery(this.searchPanel.getQuery())
                .withMaxResultCount((int) this.maxResultsSpinner.getValue())
                .withSeparateEmailThreads(this.toggleButton.isSelected());
        QueryExporter exporter = buildExporter(params);
        return exporter.export(this.searchPanel.getDataset(), this.outputPath);
    }

    public abstract String getKey();

    protected abstract FileNameExtensionFilter buildFileNameFilter();

    protected abstract QueryExporter buildExporter(QueryExportParams params);
}
