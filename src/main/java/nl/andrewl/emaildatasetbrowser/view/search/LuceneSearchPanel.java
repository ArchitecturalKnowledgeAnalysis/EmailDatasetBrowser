package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.email_indexer.data.search.EmailIndexSearcher;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.export.LuceneSearchExportPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A panel for executing Lucene search queries and examining the results.
 */
public class LuceneSearchPanel extends JPanel {
    private EmailDataset dataset;

    private final JTextArea queryField;
    private final JButton searchButton = new JButton("Search");
    private final JSpinner resultCountSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
    private final JButton exportButton = new JButton("Export");

    private final DefaultMutableTreeNode resultsRoot = new DefaultMutableTreeNode();
    private final DefaultTreeModel resultsModel = new DefaultTreeModel(resultsRoot);
    private final JTree resultsTree = new JTree(resultsModel);

    public LuceneSearchPanel(EmailViewPanel emailViewPanel) {
        super(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());
        queryField = new JTextArea();
        queryField.setLineWrap(true);
        var queryScrollPane = new JScrollPane(queryField);
        queryScrollPane.setPreferredSize(new Dimension(-1, 100));
        inputPanel.add(queryScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(searchButton);
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(clearButton);
        bottomPanel.add(buttonPanel);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exportPanel.add(resultCountSpinner);
        exportPanel.add(exportButton);
        bottomPanel.add(exportPanel);

        inputPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.NORTH);

        resultsTree.setRootVisible(false);
        resultsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        resultsTree.getSelectionModel()
                .addTreeSelectionListener(new EmailTreeSelectionListener(emailViewPanel, resultsTree));
        JScrollPane resultsScrollPane = new JScrollPane(resultsTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(resultsScrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> doSearch(resultsTree));
        clearButton.addActionListener(e -> {
            queryField.setText(null);
            resultsRoot.removeAllChildren();
            resultsModel.nodeStructureChanged(resultsRoot);
        });
        exportButton.addActionListener((e) -> {
            LuceneSearchExportPanel panel = new LuceneSearchExportPanel(
                    SwingUtilities.getWindowAncestor(this),
                    this);
            panel.activate();
        });
    }

    public void setDataset(EmailDataset dataset) {
        this.dataset = dataset;
        resultsRoot.removeAllChildren();
        resultsModel.nodeStructureChanged(resultsRoot);
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

    private void doSearch(JTree resultsTree) {
        resultsRoot.removeAllChildren();
        resultsModel.nodeStructureChanged(resultsRoot);
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
                true);
        progress.activate();
        progress.append("Searching over all emails using query: \"%s\"\nPlease be patient. This may take a while."
                .formatted(query));
        final Instant start = Instant.now();
        var future = new EmailIndexSearcher().searchAsync(dataset, queryField.getText(), getResultCount())
                .handleAsync((emailIds, throwable) -> {
                    if (throwable != null) {
                        progress.append("An error occurred: " + throwable);
                    } else {
                        showResults(start, progress, emailIds, resultsTree);
                    }
                    progress.done();
                    return null;
                });
        progress.onCancel(() -> future.cancel(true));
    }

    private void showResults(final Instant start, ProgressDialog progress, List<Long> emailIds, JTree resultsTree) {
        Duration dur = Duration.between(start, Instant.now());
        progress.appendF("Found %d email threads in %.3f seconds whose emails matched the query.", emailIds.size(),
                dur.toMillis() / 1000f);
        progress.append("Loading detailed email thread information from the database. This may take a while.");
        Instant start2 = Instant.now();
        var repo = new EmailRepository(dataset);
        int resultCount = getResultCount();
        progress.appendF("Showing the top %d results.", resultCount);
        List<EmailTreeNode> nodes = emailIds.stream()
                .map(id -> repo.findPreviewById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(EmailTreeNode::new)
                .limit(resultCount)
                .toList();
        dur = Duration.between(start2, Instant.now());
        progress.appendF("Loaded email thread information from the database in %.3f seconds.", dur.toMillis() / 1000f);
        SwingUtilities.invokeLater(() -> {
            int i = 1;
            for (var node : nodes) {
                node.setRootResultIndex(i++);
                resultsRoot.add(node);
            }
            resultsModel.nodeStructureChanged(resultsRoot);
            resultsTree.expandPath(new TreePath(resultsRoot.getPath()));
        });
    }
}
