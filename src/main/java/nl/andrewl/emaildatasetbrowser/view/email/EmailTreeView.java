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
		setEmails(emails, ds, false);
	}

	public void setEmails(List<EmailEntryPreview> emails, EmailDataset ds, boolean selectFirst) {
		rootNode.removeAllChildren();
		emails.stream().map(email -> {
			var node = new EmailTreeNode(email);
			node.loadReplies(ds);
			return node;
		}).forEachOrdered(rootNode::add);
		treeModel.nodeStructureChanged(rootNode);
		tree.expandPath(new TreePath(rootNode.getPath()));
		if (selectFirst && emails.size() > 0) {
			EmailTreeNode firstNode = (EmailTreeNode) rootNode.getChildAt(0);
			tree.setSelectionPath(new TreePath(firstNode.getPath()));
		}
	}

	public void setEmailNodes(List<EmailTreeNode> nodes) {
		setEmailNodes(nodes, false);
	}

	public void setEmailNodes(List<EmailTreeNode> nodes, boolean selectFirst) {
		rootNode.removeAllChildren();
		nodes.forEach(rootNode::add);
		treeModel.nodeStructureChanged(rootNode);
		tree.expandPath(new TreePath(rootNode.getPath()));
		if (selectFirst && nodes.size() > 0) {
			tree.setSelectionPath(new TreePath(nodes.get(0).getPath()));
		}
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
