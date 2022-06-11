package nl.andrewl.emaildatasetbrowser.view.search.export;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.export.query.QueryExporter;

public interface ExportTarget {
    public String getName();

    public FileNameExtensionFilter getFileNameExtentionFilter();

    public QueryExporter buildExporter(QueryExportParams params);
}
