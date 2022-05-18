package nl.andrewl.emaildatasetbrowser.view.search.export;

import java.util.concurrent.CompletableFuture;

import javax.swing.JButton;

import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

public final class PlainTextExportPanel extends ExportParameterPanel {

	public PlainTextExportPanel(LuceneSearchPanel searchPanel) {
		super(searchPanel);
		add(new JButton("Some button for plain text"));
	}

	@Override
	public String getKey() {
		return "Plain Text";
	}

	@Override
	public CompletableFuture<Void> export() {
		// TODO Auto-generated method stub
		return null;

	}
}
