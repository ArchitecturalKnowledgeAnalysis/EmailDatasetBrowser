package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.email_indexer.data.TagRepository;
import nl.andrewl.email_indexer.data.search.EmailIndexSearcher;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.control.search.export.exporters.LuceneSearchExporter;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.ResponsiveJText;
import nl.andrewl.emaildatasetbrowser.view.email.EmailTreeView;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A panel for executing Lucene search queries and examining the results.
 */
public class LuceneSearchPanel extends JPanel {
    private final String PREFERENCES_SHOW_PROGRESS = "lucenesearch_show_progress";

    private EmailDataset dataset;

    private final EmailTreeView emailTreeView = new EmailTreeView();

    private final JTextArea queryField;
    private final JButton searchButton = new JButton("Search");
    private final JSpinner resultCountSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
    private final JCheckBox hideTaggedCheckbox = new JCheckBox("Hide Tagged");
    private final JButton exportButton = new JButton("Export");
    private final JCheckBox showProgressButton = new JCheckBox("Show Progress Dialog");

    public LuceneSearchPanel(EmailViewPanel emailViewPanel) {
        super(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());
        queryField = new JTextArea();
        queryField.setLineWrap(true);
        new ResponsiveJText(queryField).addKeyListener((e) -> doSearch());
        var queryScrollPane = new JScrollPane(queryField);
        queryScrollPane.setPreferredSize(new Dimension(-1, 100));
        inputPanel.add(queryScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(searchButton);
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(clearButton);
        hideTaggedCheckbox.setToolTipText("Removes tagged emails from search results.");
        buttonPanel.add(hideTaggedCheckbox);
        bottomPanel.add(buttonPanel);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exportPanel.add(resultCountSpinner);
        exportPanel.add(exportButton);
        bottomPanel.add(exportPanel);

        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        showProgressButton
                .setSelected(EmailDatasetBrowser.getPreferences().getBoolean(PREFERENCES_SHOW_PROGRESS, true));
        showProgressButton.addActionListener((e) -> EmailDatasetBrowser.getPreferences()
                .putBoolean(PREFERENCES_SHOW_PROGRESS, ((JToggleButton) e.getSource()).isSelected()));
        progressPanel.add(showProgressButton);
        bottomPanel.add(progressPanel);

        inputPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.NORTH);

        emailTreeView.addSelectionListener(new EmailTreeSelectionListener(emailViewPanel, emailTreeView.getTree()));
        add(emailTreeView, BorderLayout.CENTER);

        searchButton.addActionListener(e -> doSearch());
        clearButton.addActionListener(e -> {
            queryField.setText(null);
            emailTreeView.clear();
        });
        exportButton.addActionListener((e) -> {
            ExportPanel panel = new ExportPanel(
                    SwingUtilities.getWindowAncestor(this),
                    getDataset(),
                    new LuceneSearchExporter(this));
            panel.setVisible(true);
        });
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

    public String getQuery() {
        String query = queryField.getText();
        if (query == null || query.isBlank()) {
            return null;
        }
        return query.trim();
    }

    public int getResultCount() {
        return (int) resultCountSpinner.getValue();
    }

    private void doSearch() {
        emailTreeView.clear();
        String query = getQuery();
        if (query == null) {
            return;
        }

        ProgressDialog progress = new ProgressDialog(
                SwingUtilities.getWindowAncestor(this),
                "Searching",
                null,
                true,
                true,
                false,
                false
            );
        if (showProgressButton.isSelected()) {
            progress.start();
        }
        progress.append("Searching over all emails using query: \"%s\"\nPlease be patient. This may take a while."
                .formatted(query));
        final Instant start = Instant.now();
        var future = new EmailIndexSearcher().searchAsync(dataset, queryField.getText(), getResultCount())
                .handleAsync((emailIds, throwable) -> {
                    if (throwable != null) {
                        progress.append("An error occurred: " + throwable);
                    } else {
                        showResults(start, progress, emailIds);
                    }
                    progress.done();
                    return null;
                });
        progress.onCancel(() -> future.cancel(true));
    }

    private void showResults(final Instant start, ProgressDialog progress, List<Long> emailIds) {
        Duration dur = Duration.between(start, Instant.now());
        progress.appendF("Found %d email threads in %.3f seconds whose emails matched the query.", emailIds.size(),
                dur.toMillis() / 1000f);
        progress.append("Loading detailed email thread information from the database. This may take a while.");
        Instant start2 = Instant.now();
        var repo = new EmailRepository(dataset);
        var tagRepo = new TagRepository(dataset);
        int resultCount = getResultCount();
        progress.appendF("Showing the top %d results.", resultCount);
        List<EmailTreeNode> nodes = emailIds.stream()
                .map(id -> repo.findPreviewById(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(email -> !this.hideTaggedCheckbox.isSelected() || tagRepo.getTags(email.id()).isEmpty())
                .map(EmailTreeNode::new)
                .limit(resultCount)
                .toList();
        dur = Duration.between(start2, Instant.now());
        progress.appendF("Loaded email thread information from the database in %.3f seconds.", dur.toMillis() / 1000f);
        SwingUtilities.invokeLater(() -> {
            int i = 1;
            for (var node : nodes) {
                node.setRootResultIndex(i++);
                node.loadReplies(dataset);
            }
            emailTreeView.setEmailNodes(nodes);
        });
    }
}
