package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * A listener that will lazily-load email replies and expand an email tree node
 * when it's selected.
 */
public class EmailTreeSelectionListener implements TreeSelectionListener {
	private final EmailViewPanel emailViewPanel;
	private final JTree tree;

	public EmailTreeSelectionListener(EmailViewPanel emailViewPanel, JTree tree) {
		this.emailViewPanel = emailViewPanel;
		this.tree = tree;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		EmailDataset dataset = emailViewPanel.getCurrentDataset();
		if (e.getPath().getLastPathComponent() instanceof EmailTreeNode node && dataset != null) {
			emailViewPanel.fetchAndSetEmail(node.getEmail().messageId());
			node.loadReplies(dataset);
			tree.expandPath(new TreePath(node.getPath()));
		}
	}
}
