package nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.query.PdfQueryExporter;
import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.export.query.QueryExporter;
import nl.andrewl.emaildatasetbrowser.view.search.export.ExportTarget;

/**
 * Concrete implementation of ExportTarget exporting to PDF files.
 */
public class PdfExportTarget implements ExportTarget {

    @Override
    public String getName() {
        return "PDF File";
    }

    @Override
    public FileNameExtensionFilter getFileNameExtentionFilter() {
        return new FileNameExtensionFilter("PDF Files", "pdf");
    }

    @Override
    public QueryExporter buildExporter(QueryExportParams params) {
        return new PdfQueryExporter(params);
    }
}
