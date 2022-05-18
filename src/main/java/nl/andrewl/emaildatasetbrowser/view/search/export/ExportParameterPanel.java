package nl.andrewl.emaildatasetbrowser.view.search.export;

import java.awt.FlowLayout;
import java.util.concurrent.CompletableFuture;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

public abstract class ExportParameterPanel extends JPanel {
    private LuceneSearchPanel searchPanel;

    public ExportParameterPanel(LuceneSearchPanel searchPanel) {
        super(new FlowLayout(FlowLayout.LEFT));
        this.searchPanel = searchPanel;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setVisible(false);
    }

    protected final LuceneSearchPanel getSearchPanel() {
        return searchPanel;
    }

    public abstract String getKey();

    public abstract CompletableFuture<Void> export();
}
