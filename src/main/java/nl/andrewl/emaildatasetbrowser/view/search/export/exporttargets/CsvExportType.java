package nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.datasample.datatype.CsvExporter;
import nl.andrewl.email_indexer.data.export.datasample.datatype.TypeExporter;
import nl.andrewl.emaildatasetbrowser.view.search.export.ExportType;

/**
 * Factory object for CSV file exporters.
 */
public class CsvExportType implements ExportType {

    @Override
    public String getName() {
        return "CSV File";
    }

    @Override
    public FileNameExtensionFilter getFileNameExtentionFilter() {
        return new FileNameExtensionFilter("CSV Files", "csv");
    }

    @Override
    public TypeExporter buildTypeExporter() {
        return new CsvExporter();
    }
}
