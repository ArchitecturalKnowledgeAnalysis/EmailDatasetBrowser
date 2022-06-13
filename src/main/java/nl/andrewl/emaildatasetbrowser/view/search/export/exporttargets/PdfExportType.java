package nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.datasample.datatype.PdfExporter;
import nl.andrewl.email_indexer.data.export.datasample.datatype.TypeExporter;
import nl.andrewl.emaildatasetbrowser.view.search.export.ExportType;

/**
 * Concrete implementation of ExportTarget exporting to PDF files.
 */
public class PdfExportType implements ExportType {

    @Override
    public String getName() {
        return "PDF File";
    }

    @Override
    public FileNameExtensionFilter getFileNameExtentionFilter() {
        return new FileNameExtensionFilter("PDF Files", "pdf");
    }

    @Override
    public TypeExporter buildTypeExporter() {
        return new PdfExporter();
    }
}
