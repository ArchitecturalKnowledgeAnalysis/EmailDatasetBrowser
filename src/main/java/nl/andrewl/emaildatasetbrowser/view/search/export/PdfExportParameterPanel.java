package nl.andrewl.emaildatasetbrowser.view.search.export;

import java.io.FileFilter;
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
import nl.andrewl.email_indexer.data.export.query.QueryExporter;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

public final class PdfExportParameterPanel extends ExportParameterPanel {

    public PdfExportParameterPanel(LuceneSearchPanel searchPanel) {
        super(searchPanel);
    }

    @Override
    public String getKey() {
        return "PDF File";
    }

    @Override
    protected FileNameExtensionFilter buildFileNameFilter() {
        return new FileNameExtensionFilter(
                "PDF Files",
                "pdf");
    }

    @Override
    protected QueryExporter buildExporter(QueryExportParams params) {
        return new PdfQueryExporter(params);
    }
}
