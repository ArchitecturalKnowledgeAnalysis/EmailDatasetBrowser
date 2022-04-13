package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailIndexSearcher;
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
    private final DefaultMutableTreeNode resultsRoot = new DefaultMutableTreeNode();
    private final DefaultTreeModel resultsModel = new DefaultTreeModel(resultsRoot);

    public LuceneSearchPanel(EmailViewPanel emailViewPanel) {
        super(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextArea queryField = new JTextArea();
        queryField.setPreferredSize(new Dimension(-1, 100));
        searchButton = new JButton("Search");
        inputPanel.add(new JScrollPane(queryField), BorderLayout.CENTER);
        inputPanel.add(searchButton, BorderLayout.SOUTH);
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

        searchButton.addActionListener(e -> doSearch(queryField, emailViewPanel, resultsTree));
    }

    public void setDataset(EmailDataset dataset) {
        this.dataset = dataset;
        resultsRoot.removeAllChildren();
        resultsModel.nodeStructureChanged(resultsRoot);
        searchButton.setEnabled(dataset != null);
    }

    private void doSearch(JTextArea queryField, EmailViewPanel emailViewPanel, JTree resultsTree) {
        resultsRoot.removeAllChildren();
        resultsModel.nodeStructureChanged(resultsRoot);
        String query = queryField.getText();
        if (query == null || query.isBlank()) {
            emailViewPanel.setEmail(null);
            return;
        }
        query = query.trim();

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
