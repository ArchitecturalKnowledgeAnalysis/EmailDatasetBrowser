package nl.andrewl.emaildatasetbrowser.view.email;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailEntryPreview;
import nl.andrewl.emaildatasetbrowser.view.search.EmailTreeNode;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.List;

/**
 * A tree view that shows a set of emails as tree nodes. This can be used as a
 * general-purpose result set display.
 */
public class EmailTreeView extends JPanel {
	private final JTree tree;
	private final DefaultTreeModel treeModel;
	private final DefaultMutableTreeNode rootNode;

	public EmailTreeView() {
		super(new BorderLayout());
		rootNode = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(rootNode);
		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	public void setEmails(List<EmailEntryPreview> emails, EmailDataset ds) {
		rootNode.removeAllChildren();
		emails.stream().map(email -> {
			var node = new EmailTreeNode(email);
			node.loadReplies(ds);
			return node;
		}).forEachOrdered(rootNode::add);
		treeModel.nodeStructureChanged(rootNode);
		tree.expandPath(new TreePath(rootNode.getPath()));
	}

	public void setEmailNodes(List<EmailTreeNode> nodes) {
		rootNode.removeAllChildren();
		nodes.forEach(rootNode::add);
		treeModel.nodeStructureChanged(rootNode);
		tree.expandPath(new TreePath(rootNode.getPath()));
	}

	public void clear() {
		rootNode.removeAllChildren();
		treeModel.nodeStructureChanged(rootNode);
	}

	public void addSelectionListener(TreeSelectionListener listener) {
		tree.addTreeSelectionListener(listener);
	}

	public JTree getTree() {
		return tree;
	}
}
