package nl.andrewl.emaildatasetbrowser.view.search.export.exporters;

import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;
import nl.andrewl.emaildatasetbrowser.view.search.export.Exporter;

/**
 * Concrete implementation of Exporter exporting data acquired using the
 * LuceneSearch query.
 */
public class LuceneSearchExporter implements Exporter {
    private final LuceneSearchPanel searchPanel;

    /**
     * @param searchPanel panel which's data is exported.
     */
    public LuceneSearchExporter(LuceneSearchPanel searchPanel) {
        this.searchPanel = searchPanel;
    }

    @Override
    public QueryExportParams specifyParameters(QueryExportParams params) {
        return params.withQuery(this.searchPanel.getQuery());
    }
}
