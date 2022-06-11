package nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.query.PlainTextQueryExporter;
import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.export.query.QueryExporter;
import nl.andrewl.emaildatasetbrowser.view.search.export.ExportTarget;

public class TxtExportTarget implements ExportTarget {

    @Override
    public String getName() {
        return "Text File";
    }

    @Override
    public FileNameExtensionFilter getFileNameExtentionFilter() {
        return new FileNameExtensionFilter("Text Files", "txt");
    }

    @Override
    public QueryExporter buildExporter(QueryExportParams params) {
        return new PlainTextQueryExporter(params);
    }
}
