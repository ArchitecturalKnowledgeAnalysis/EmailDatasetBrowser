package nl.andrewl.emaildatasetbrowser.control.search.export.exporttargets;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.datasample.datatype.PdfExporter;
import nl.andrewl.email_indexer.data.export.datasample.datatype.TypeExporter;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportType;

/**
 * Factory object for PDF file exporters.
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
