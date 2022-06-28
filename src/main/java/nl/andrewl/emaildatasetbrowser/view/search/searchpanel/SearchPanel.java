package nl.andrewl.emaildatasetbrowser.view.search.searchpanel;

import java.awt.*;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportSample;
import nl.andrewl.emaildatasetbrowser.view.DatasetChangeListener;
import nl.andrewl.emaildatasetbrowser.view.email.EmailTreeView;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.EmailTreeSelectionListener;
import nl.andrewl.emaildatasetbrowser.view.search.ExportPanel;

public abstract class SearchPanel extends JPanel implements DatasetChangeListener {
    public final static String PREF_BROWSE_PAGE_SIZE = "pref_browse_page_size";

    protected final EmailViewPanel emailViewPanel;

    protected final EmailTreeView emailTreeView = new EmailTreeView();
    private final JButton nextPageButton = new JButton("Next");
    private final JButton previousPageButton = new JButton("Prev");
    private final JButton searchButton = new JButton("Search");
    private final JButton exportButton = new JButton("Export");
    private final JButton clearButton = new JButton("Clear");

    private EmailDataset dataset;

    private int currentPage = 1;

    public SearchPanel(EmailViewPanel emailViewPanel) {
        super(new BorderLayout());
        this.emailViewPanel = emailViewPanel;
        this.add(this.buildSearchPanel(), BorderLayout.NORTH);
        emailTreeView.addSelectionListener(new EmailTreeSelectionListener(emailViewPanel, emailTreeView.getTree()));
        add(emailTreeView, BorderLayout.CENTER);
    }

    public void setDataset(EmailDataset dataset) {
        this.dataset = dataset;
        emailTreeView.clear();
        searchButton.setEnabled(dataset != null);
        exportButton.setEnabled(dataset != null);
    }

    public EmailDataset getDataset() {
        return dataset;
    }

    protected int getCurrentPage() {
        return this.currentPage;
    }

    @Override
    public void datasetChanged(EmailDataset ds) {
        setDataset(ds);
    }

    private JPanel buildSearchPanel() {
        // Sets up the concrete implementation's parameter panel.
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(buildParameterPanel(), BorderLayout.NORTH);

        // Sets up the shared panel.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exportButton);
        inputPanel.add(buttonPanel);

        // TODO: add paging controls add data.

        searchButton.addActionListener(e -> {
            if (this.dataset != null) {
                doSearch();
            }
        });
        clearButton.addActionListener(e -> {
            if (this.dataset != null) {
                onClearClicked();
            }
        });
        exportButton.addActionListener(e -> {
            if (this.dataset != null) {
                doExport();
            }
        });
        return inputPanel;
    }

    private void doExport() {
        ExportPanel panel = new ExportPanel(
                SwingUtilities.getWindowAncestor(this),
                getDataset(),
                buildExporter());
        panel.setVisible(true);
    }

    protected void searchFromBeginning() {
        this.currentPage = 1;
        doSearch();
    }

    protected void toggleChangePageButton(boolean nextButton, boolean enabled) {
        if (nextButton) {
            this.nextPageButton.setEnabled(enabled);
        } else {
            previousPageButton.setEnabled(enabled);
        }
    }

    /**
     * Factory method allowing the implementing class to add custom search
     * parameters to the searchpanel UI.
     * 
     * @param parent the container object for all added Swing objects.
     */
    protected abstract JPanel buildParameterPanel();

    /**
     * Factory method for the concrete ExporterSample object.
     */
    protected abstract ExportSample buildExporter();

    /**
     * Called when the search button is clicked.
     */
    protected abstract void doSearch();

    /**
     * Called when the clear button is clicked.
     */
    protected abstract void onClearClicked();
}
