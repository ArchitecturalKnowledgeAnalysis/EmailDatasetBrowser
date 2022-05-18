package nl.andrewl.emaildatasetbrowser.view.search.export;

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
