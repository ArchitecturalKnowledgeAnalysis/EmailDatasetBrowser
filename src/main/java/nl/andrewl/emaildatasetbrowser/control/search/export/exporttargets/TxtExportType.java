package nl.andrewl.emaildatasetbrowser.control.search.export.exporttargets;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.datasample.datatype.TxtExporter;
import nl.andrewl.email_indexer.data.export.datasample.datatype.TypeExporter;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportType;

/**
 * Factory object for text file exporters.
 */
public class TxtExportType implements ExportType {

    @Override
    public String getName() {
        return "Text File";
    }

    @Override
    public FileNameExtensionFilter getFileNameExtentionFilter() {
        return new FileNameExtensionFilter("Text Files", "txt");
    }

    @Override
    public TypeExporter buildTypeExporter() {
        return new TxtExporter();
    }
}
