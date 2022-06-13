package nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.datasample.datatype.TxtExporter;
import nl.andrewl.email_indexer.data.export.datasample.datatype.TypeExporter;
import nl.andrewl.emaildatasetbrowser.view.search.export.ExportType;

/**
 * Concrete implementation of ExportTarget exporting to plain text files.
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
