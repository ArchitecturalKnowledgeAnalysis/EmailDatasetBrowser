package nl.andrewl.emaildatasetbrowser.view.search.export;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.query.PdfQueryExporter;
import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.export.query.QueryExporter;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

public final class PdfExportParameterPanel extends QueryExportParameterPanel {

    public PdfExportParameterPanel(LuceneSearchPanel searchPanel) {
        super(searchPanel);
    }

    @Override
    public String getName() {
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
