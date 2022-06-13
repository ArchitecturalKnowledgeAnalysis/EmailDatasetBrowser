package nl.andrewl.emaildatasetbrowser.control.search.export;

import nl.andrewl.email_indexer.data.export.ExporterParameters;
import nl.andrewl.email_indexer.data.export.datasample.datatype.TypeExporter;
import nl.andrewl.email_indexer.data.export.datasample.sampletype.SampleExporter;

/**
 * Generic exporter interface for factory objects of different types of
 * exporters.
 */
public interface ExportSample {
    /**
     * Called before exporting, allowing export parameters to be updated according
     * to the concrete exporter implementation.
     * 
     * @param params parameter object with common parameters already set.
     * @return updated parameters
     */
    public ExporterParameters specifyParameters(ExporterParameters params);

    /**
     * Factory method for creating the concrete SampleExporter corresponding with
     * this class.
     * 
     * @param typeExporter The used type exporter.
     * @param params       The used export parameters.
     * @return A newly built concrete sample exporter corresponding with this class.
     */
    public SampleExporter buildSampleExporter(TypeExporter typeExporter, ExporterParameters params);
}
