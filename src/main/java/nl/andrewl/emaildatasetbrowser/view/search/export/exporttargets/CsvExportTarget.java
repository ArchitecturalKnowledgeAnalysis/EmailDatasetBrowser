package nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.query.CsvQueryExporter;
import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.export.query.QueryExporter;
import nl.andrewl.emaildatasetbrowser.view.search.export.ExportTarget;

/**
 * Concrete implementation of ExportTarget exporting to CSV files.
 */
public class CsvExportTarget implements ExportTarget {

    @Override
    public String getName() {
        return "CSV File";
    }

    @Override
    public FileNameExtensionFilter getFileNameExtentionFilter() {
        return new FileNameExtensionFilter("CSV Files", "csv");
    }

    @Override
    public QueryExporter buildExporter(QueryExportParams params) {
        return new CsvQueryExporter(params);
    }
}
