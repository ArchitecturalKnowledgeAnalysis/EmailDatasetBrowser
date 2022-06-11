package nl.andrewl.emaildatasetbrowser.view.search.export;

import nl.andrewl.email_indexer.data.export.query.QueryExportParams;

public interface Exporter {
    public QueryExportParams specifyParameters(QueryExportParams params);
}
