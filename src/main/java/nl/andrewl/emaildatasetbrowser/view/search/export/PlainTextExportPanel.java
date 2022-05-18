package nl.andrewl.emaildatasetbrowser.view.search.export;

import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.export.query.PlainTextQueryExporter;
import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.export.query.QueryExporter;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

public final class PlainTextExportPanel extends QueryExportParameterPanel {

	public PlainTextExportPanel(LuceneSearchPanel searchPanel) {
		super(searchPanel);
	}

	@Override
	public String getName() {
		return "Plain Text";
	}

	@Override
	protected FileNameExtensionFilter buildFileNameFilter() {
		return new FileNameExtensionFilter(
				"Text Files",
				"txt");
	}

	@Override
	protected QueryExporter buildExporter(QueryExportParams params) {
		return new PlainTextQueryExporter(params);
	}
}
