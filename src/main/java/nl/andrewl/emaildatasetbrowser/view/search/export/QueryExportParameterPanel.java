package nl.andrewl.emaildatasetbrowser.view.search.export;

import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.export.query.QueryExporter;
import nl.andrewl.emaildatasetbrowser.control.DirectoryFileFilter;
import nl.andrewl.emaildatasetbrowser.view.PathSelectField;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

/**
 * Simple JPanel base class for Exporting queries.
 */
public abstract class QueryExportParameterPanel extends JPanel {
    private LuceneSearchPanel searchPanel;

    private final JSpinner maxResultsSpinner = new JSpinner(
            new SpinnerNumberModel(100, 1, 10000, 1));
    private final JCheckBox separateThreadsToggle = new JCheckBox("Separate mailing threads");
    private final PathSelectField dirSelectField = new PathSelectField(JFileChooser.DIRECTORIES_ONLY, false, false,
            new DirectoryFileFilter(), "Select Directory", "Select Export Directory", "Select");
    private final PathSelectField fileSelectField = new PathSelectField(JFileChooser.FILES_ONLY, true, false,
            buildFileNameFilter(), "Select File", "Select Export File", "Select");

    public QueryExportParameterPanel(LuceneSearchPanel searchPanel) {
        super(new FlowLayout(FlowLayout.LEFT));
        this.searchPanel = searchPanel;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setVisible(false);

        add(new JLabel("Max. result count:"));
        add(maxResultsSpinner);

        separateThreadsToggle.addActionListener(e -> {
            dirSelectField.setVisible(separateThreadsToggle.isSelected());
            fileSelectField.setVisible(!separateThreadsToggle.isSelected());
        });
        add(separateThreadsToggle);

        dirSelectField.setVisible(false);
        add(dirSelectField);
        add(fileSelectField);
    }

    /**
     * Exports the query results.
     */
    public CompletableFuture<Void> export() {
        String query = this.searchPanel.getQuery();
        Path outputPath = this.separateThreadsToggle.isSelected()
                ? this.dirSelectField.getSelectedPath()
                : this.fileSelectField.getSelectedPath();
        if (!hasValidParameters(query, outputPath)) {
            return CompletableFuture.failedFuture(new IllegalArgumentException(
                    "Cannot export with variables query: \"%s\" and output path: \"%s\""
                            .formatted(this.searchPanel.getQuery(), outputPath)));
        }
        QueryExportParams params = new QueryExportParams()
                .withQuery(this.searchPanel.getQuery())
                .withMaxResultCount((int) this.maxResultsSpinner.getValue())
                .withSeparateEmailThreads(this.separateThreadsToggle.isSelected());
        QueryExporter exporter = buildExporter(params);
        return exporter.export(this.searchPanel.getDataset(), outputPath);
    }

    private boolean hasValidParameters(String query, Path outputPath) {
        // Query validation.
        if (query == null || query.isBlank()) {
            return false;
        }
        // Path validation.
        if (outputPath == null) {
            return false;
        }
        try {
            outputPath.toFile().getCanonicalPath();
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }

    public abstract String getName();

    /**
     * Builder function for the to-be-used FileNameExtentionFilter.
     */
    protected abstract FileNameExtensionFilter buildFileNameFilter();

    /**
     * Builder function for the to-be-used QueryExporter.
     */
    protected abstract QueryExporter buildExporter(QueryExportParams params);
}
