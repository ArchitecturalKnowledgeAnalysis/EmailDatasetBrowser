package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailIndexSearcher;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

/**
 * A panel for executing Lucene search queries and examining the results.
 */
public class LuceneSearchPanel extends JPanel {
    private EmailDataset dataset;
    private final DefaultMutableTreeNode resultsRoot = new DefaultMutableTreeNode();

    public LuceneSearchPanel(EmailViewPanel emailViewPanel) {
        super(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextArea queryField = new JTextArea();
        queryField.setPreferredSize(new Dimension(-1, 100));
        JButton searchButton = new JButton("Search");
        inputPanel.add(new JScrollPane(queryField), BorderLayout.CENTER);
        inputPanel.add(searchButton, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.NORTH);

        DefaultTreeModel resultsModel = new DefaultTreeModel(resultsRoot);
        JTree resultsTree = new JTree(resultsModel);
        resultsTree.setRootVisible(false);
        resultsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        resultsTree.getSelectionModel().addTreeSelectionListener(e -> {
            if (e.getPath().getLastPathComponent() instanceof EmailTreeNode etn) {
                emailViewPanel.fetchAndSetEmail(etn.getEmail().messageId());
            }
        });
        JScrollPane resultsScrollPane = new JScrollPane(resultsTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultsScrollPane.setPreferredSize(new Dimension(200, -1));
        add(resultsScrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            new EmailIndexSearcher().searchAsync(dataset, queryField.getText()).thenAccept(emails -> {
                System.out.println("Got " + emails.size() + " results");
                SwingUtilities.invokeLater(() -> {
                    resultsRoot.removeAllChildren();
                    for (var email : emails) {
                        resultsRoot.add(new EmailTreeNode(email));
                    }
                    resultsTree.expandPath(new TreePath(resultsRoot.getPath()));
                });
            });
        });
    }

    public void setDataset(EmailDataset dataset) {
        this.dataset = dataset;
        resultsRoot.removeAllChildren();
    }
}
