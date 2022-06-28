package nl.andrewl.emaildatasetbrowser.control.search.export.exporters;

import nl.andrewl.email_indexer.data.export.ExporterParameters;
import nl.andrewl.email_indexer.data.export.datasample.datatype.TypeExporter;
import nl.andrewl.email_indexer.data.export.datasample.sampletype.FilterExporter;
import nl.andrewl.email_indexer.data.export.datasample.sampletype.SampleExporter;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportSample;
import nl.andrewl.emaildatasetbrowser.view.search.searchpanel.SimpleBrowsePanel;

/**
 * Factory object for sample exporters of the SimpleBrowsePanel.
 */
public class SimpleExporter implements ExportSample {
    private final SimpleBrowsePanel browsePanel;

    /**
     * @param browsePanel The browsepanel which's data is exported.
     */
    public SimpleExporter(SimpleBrowsePanel browsePanel) {
        this.browsePanel = browsePanel;
    }

    @Override
    public ExporterParameters specifyParameters(ExporterParameters params) {
        params.withSearchFilters(this.browsePanel.getCurrentSearchFilters());
        return params;
    }

    @Override
    public SampleExporter buildSampleExporter(TypeExporter typeExporter, ExporterParameters params) {
        return new FilterExporter(typeExporter, params);
    }
}
