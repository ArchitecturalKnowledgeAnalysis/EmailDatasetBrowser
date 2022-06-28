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
    protected final EmailViewPanel emailViewPanel;

    protected final EmailTreeView emailTreeView = new EmailTreeView();
    private final JButton searchButton = new JButton("Search");
    private final JButton exportButton = new JButton("Export");
    private final JButton clearButton = new JButton("Clear");

    private EmailDataset dataset;

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

        searchButton.addActionListener(e -> doSearch());
        clearButton.addActionListener(e -> onClearClicked());
        exportButton.addActionListener((e) -> {
            ExportPanel panel = new ExportPanel(
                    SwingUtilities.getWindowAncestor(this),
                    getDataset(),
                    buildExporter());
            panel.setVisible(true);
        });
        return inputPanel;
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
