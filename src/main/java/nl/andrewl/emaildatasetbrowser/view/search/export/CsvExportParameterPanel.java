package nl.andrewl.emaildatasetbrowser.view.search.export;

import nl.andrewl.email_indexer.data.export.query.CsvQueryExporter;
import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.export.query.QueryExporter;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

import javax.swing.filechooser.FileNameExtensionFilter;

public final class CsvExportParameterPanel extends QueryExportParameterPanel {
	public CsvExportParameterPanel(LuceneSearchPanel searchPanel) {
		super(searchPanel);
	}

	@Override
	public String getName() {
		return "CSV File";
	}

	@Override
	protected FileNameExtensionFilter buildFileNameFilter() {
		return new FileNameExtensionFilter("CSV Files", "csv");
	}

	@Override
	protected QueryExporter buildExporter(QueryExportParams params) {
		return new CsvQueryExporter(params);
	}
}
