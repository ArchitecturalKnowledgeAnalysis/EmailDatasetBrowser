package nl.andrewl.emaildatasetbrowser.view.search.export.exporters;

import java.util.List;

import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.search.SearchFilter;
import nl.andrewl.emaildatasetbrowser.view.search.SimpleBrowsePanel;
import nl.andrewl.emaildatasetbrowser.view.search.export.Exporter;

/**
 * Concrete implementation of Exporter exporting data acquired in the
 * SimpleBrowsePanel.
 */
public class SimpleExporter implements Exporter {
    private final SimpleBrowsePanel browsePanel;

    /**
     * @param browsePanel The browsepanel which's data is exported.
     */
    public SimpleExporter(SimpleBrowsePanel browsePanel) {
        this.browsePanel = browsePanel;
    }

    @Override
    public QueryExportParams specifyParameters(QueryExportParams params) {
        List<SearchFilter> filters = this.browsePanel.getCurrentSearchFilters();
        // TODO add filters to params;
        return params;
    }
}
