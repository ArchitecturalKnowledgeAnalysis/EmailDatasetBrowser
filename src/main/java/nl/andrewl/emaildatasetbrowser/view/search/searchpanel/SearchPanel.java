package nl.andrewl.emaildatasetbrowser.view.search.searchpanel;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportSample;
import nl.andrewl.emaildatasetbrowser.view.DatasetChangeListener;
import nl.andrewl.emaildatasetbrowser.view.email.EmailTreeView;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.EmailTreeSelectionListener;
import nl.andrewl.emaildatasetbrowser.view.search.ExportPanel;

import java.awt.*;
import javax.swing.*;

public abstract class SearchPanel extends JPanel implements DatasetChangeListener {
    public final static String PREF_BROWSE_PAGE_SIZE = "pref_browse_page_size";

    protected final EmailViewPanel emailViewPanel;

    protected final EmailTreeView emailTreeView = new EmailTreeView();
    private final JButton nextPageButton = new JButton("Next");
    private final JButton previousPageButton = new JButton("Prev");
    private final JButton searchButton = new JButton("Search");
    private final JButton exportButton = new JButton("Export");
    private final JButton clearButton = new JButton("Clear");
    private final JLabel currentPageLabel = new JLabel("Page 1 of 1");

    private EmailDataset dataset;

    private int currentPage = 1;

    public SearchPanel(EmailViewPanel emailViewPanel) {
        super(new BorderLayout());
        this.emailViewPanel = emailViewPanel;
        this.add(this.buildSearchPanel(), BorderLayout.NORTH);
        emailTreeView.addSelectionListener(new EmailTreeSelectionListener(emailViewPanel, emailTreeView.getTree()));
        add(emailTreeView, BorderLayout.CENTER);
    }

    public EmailDataset getDataset() {
        return dataset;
    }

    protected int getCurrentPage() {
        return this.currentPage;
    }

    protected int getPageSize() {
        return EmailDatasetBrowser.getPreferences().getInt(PREF_BROWSE_PAGE_SIZE, 25);
    }

    public void setDataset(EmailDataset dataset) {
        this.dataset = dataset;
        emailTreeView.clear();
        searchButton.setEnabled(dataset != null);
        clearButton.setEnabled(dataset != null);
        exportButton.setEnabled(dataset != null);
    }

    @Override
    public void datasetChanged(EmailDataset ds) {
        setDataset(ds);
    }

    private JPanel buildSearchPanel() {
        // Sets up the concrete implementation's parameter panel.
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(buildParameterPanel(), BorderLayout.NORTH);
        inputPanel.add(buildControlButtonPanel(), BorderLayout.CENTER);
        inputPanel.add(buildPageNavigationPanel(), BorderLayout.SOUTH);
        return inputPanel;
    }

    private JPanel buildControlButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        searchButton.setMargin(new Insets(0, 0, 0, 2));
        clearButton.setMargin(new Insets(0, 2, 0, 2));
        clearButton.setMargin(new Insets(0, 2, 0, 0));
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exportButton);
        searchButton.addActionListener(e -> doSearch());
        clearButton.addActionListener(e -> onClearClicked());
        exportButton.addActionListener(e -> doExport());
        return buttonPanel;
    }

    private JPanel buildPageNavigationPanel() {
        JPanel navigationParent = new JPanel(new GridLayout(2, 1));
        navigationParent.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        navigationParent.add(currentPageLabel);
        JPanel pageControlPanel = new JPanel(new GridLayout(1, 2));
        previousPageButton.setMargin(new Insets(0, 0, 0, 2));
        nextPageButton.setMargin(new Insets(0, 2, 0, 0));
        pageControlPanel.add(previousPageButton);
        pageControlPanel.add(nextPageButton);
        previousPageButton.setEnabled(false);
        nextPageButton.setEnabled(false);
        nextPageButton.addActionListener(e -> {
            this.currentPage++;
            doSearch();
        });
        previousPageButton.addActionListener(e -> {
            this.currentPage--;
            doSearch();
        });
        navigationParent.add(pageControlPanel);
        return navigationParent;
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
    protected void doSearch() {
        emailTreeView.clear();
        int pageSize = getPageSize();
        currentPageLabel.setText(
                "Page %s (%s - %s)".formatted(currentPage, (currentPage - 1) * pageSize + 1, currentPage * pageSize));
    }

    /**
     * Called when the clear button is clicked.
     */
    protected abstract void onClearClicked();
}
