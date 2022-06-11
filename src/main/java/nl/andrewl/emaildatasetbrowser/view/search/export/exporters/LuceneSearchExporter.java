package nl.andrewl.emaildatasetbrowser.view.search.export.exporters;

import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;
import nl.andrewl.emaildatasetbrowser.view.search.export.Exporter;

public class LuceneSearchExporter implements Exporter {
    private final LuceneSearchPanel searchPanel;

    public LuceneSearchExporter(LuceneSearchPanel searchPanel) {
        this.searchPanel = searchPanel;
    }

    @Override
    public QueryExportParams specifyParameters(QueryExportParams params) {
        return params.withQuery(this.searchPanel.getQuery());
    }
}
