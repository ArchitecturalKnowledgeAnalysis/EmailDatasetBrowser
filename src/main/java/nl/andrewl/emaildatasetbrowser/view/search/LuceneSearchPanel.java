package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailIndexSearcher;
import nl.andrewl.emaildatasetbrowser.control.search.ExportLuceneSearchAction;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.List;

/**
 * A panel for executing Lucene search queries and examining the results.
 */
public class LuceneSearchPanel extends JPanel {
    private EmailDataset dataset;
    private final JButton searchButton;
    private final JButton exportButton;
    private final DefaultMutableTreeNode resultsRoot = new DefaultMutableTreeNode();
    private final DefaultTreeModel resultsModel = new DefaultTreeModel(resultsRoot);
    private final JTextArea queryField;

    public LuceneSearchPanel(EmailViewPanel emailViewPanel) {
        super(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());
        queryField = new JTextArea();
        queryField.setPreferredSize(new Dimension(-1, 100));
        searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        exportButton = new JButton("Export");
        inputPanel.add(new JScrollPane(queryField), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exportButton);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.NORTH);

        JTree resultsTree = new JTree(resultsModel);
        resultsTree.setRootVisible(false);
        resultsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        resultsTree.getSelectionModel().addTreeSelectionListener(e -> {
            if (e.getPath().getLastPathComponent() instanceof EmailTreeNode etn) {
                emailViewPanel.fetchAndSetEmail(etn.getEmail().messageId());
                resultsTree.expandPath(new TreePath(etn.getPath()));
            }
        });
        JScrollPane resultsScrollPane = new JScrollPane(resultsTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(resultsScrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> doSearch(resultsTree));
        clearButton.addActionListener(e -> {
            queryField.setText(null);
            resultsRoot.removeAllChildren();
            resultsModel.nodeStructureChanged(resultsRoot);
        });
        exportButton.addActionListener(new ExportLuceneSearchAction(this));
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

    private void doSearch(JTree resultsTree) {
        resultsRoot.removeAllChildren();
        resultsModel.nodeStructureChanged(resultsRoot);
        String query = getQuery();
        if (query == null) return;

        ProgressDialog progress = ProgressDialog.minimalText(this, "Searching");
        progress.append("Searching over all emails using query: \"%s\"".formatted(query));
        var future = new EmailIndexSearcher().searchAsync(dataset, queryField.getText()).thenAcceptAsync(emails -> {
            progress.append("Found %d email threads whose contents match the query.".formatted(emails.size()));
            List<EmailTreeNode> nodes = emails.stream().map(EmailTreeNode::new).toList();
            SwingUtilities.invokeLater(() -> {
                nodes.forEach(resultsRoot::add);
                resultsModel.nodeStructureChanged(resultsRoot);
                resultsTree.expandPath(new TreePath(resultsRoot.getPath()));
            });
        });
        progress.bind(future);
    }
}
