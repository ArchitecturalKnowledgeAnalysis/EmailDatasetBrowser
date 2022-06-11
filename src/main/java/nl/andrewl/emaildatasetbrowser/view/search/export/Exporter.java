package nl.andrewl.emaildatasetbrowser.view.search.export;

import nl.andrewl.email_indexer.data.export.query.QueryExportParams;

/**
 * Generic exporter interface for different types of exporters.
 */
public interface Exporter {
    /**
     * Called before exporting, allowing export parameters to be updated according
     * to the concrete exporter implementation.
     * 
     * @param params parameter object with common parameters already set.
     * @return updated parameters
     */
    public QueryExportParams specifyParameters(QueryExportParams params);
}
