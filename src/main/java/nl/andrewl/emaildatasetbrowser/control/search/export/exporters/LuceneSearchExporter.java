package nl.andrewl.emaildatasetbrowser.control.search.export.exporters;

import nl.andrewl.email_indexer.data.export.ExporterParameters;
import nl.andrewl.email_indexer.data.export.datasample.datatype.TypeExporter;
import nl.andrewl.email_indexer.data.export.datasample.sampletype.QueryExporter;
import nl.andrewl.email_indexer.data.export.datasample.sampletype.SampleExporter;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportSample;
import nl.andrewl.emaildatasetbrowser.view.search.searchpanel.LuceneSearchPanel;

/**
 * Factory object for sample exporters of the LuceneSearchPanel.
 */
public class LuceneSearchExporter implements ExportSample {
    private final LuceneSearchPanel searchPanel;

    /**
     * @param searchPanel panel which's data is exported.
     */
    public LuceneSearchExporter(LuceneSearchPanel searchPanel) {
        this.searchPanel = searchPanel;
    }

    @Override
    public ExporterParameters specifyParameters(ExporterParameters params) {
        return params.withQuery(this.searchPanel.getQuery());
    }

    @Override
    public SampleExporter buildSampleExporter(TypeExporter typeExporter, ExporterParameters params) {
        return new QueryExporter(typeExporter, params);
    }
}
