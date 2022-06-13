package nl.andrewl.emaildatasetbrowser.view.search.export;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.datasample.datatype.TypeExporter;

/**
 * Common interface for different export formats; i.e. file types.
 */
public interface ExportType {
    /**
     * The name of this target in natural language.
     * 
     * @return the name of this target.
     */
    public String getName();

    /**
     * The file extention filter that is used for this export target in case the
     * tool targets a single output file.
     * 
     * @return to-be-used file extention filter.
     */
    public FileNameExtensionFilter getFileNameExtentionFilter();

    /**
     * Builds the concrete exporter corresponding with this export target.
     * 
     * @param params to-be-used export parameters.
     * @return built query exporter.
     */
    public TypeExporter buildTypeExporter();
}
