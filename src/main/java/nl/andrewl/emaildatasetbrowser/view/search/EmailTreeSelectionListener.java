package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * A listener that will lazily-load email replies and expand an email tree node
 * when it's selected.
 */
public class EmailTreeSelectionListener implements TreeSelectionListener {
	public static final String PREF_AUTO_OPEN = "pref_auto_open_tree_node";

	private final EmailViewPanel emailViewPanel;
	private final JTree tree;

	private EmailTreeNode lastNode = null;

	public EmailTreeSelectionListener(EmailViewPanel emailViewPanel, JTree tree) {
		this.emailViewPanel = emailViewPanel;
		this.tree = tree;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		EmailDataset dataset = emailViewPanel.getCurrentDataset();
		if (e.getPath().getLastPathComponent() instanceof EmailTreeNode node && dataset != null) {
			emailViewPanel.fetchAndSetEmail(node.getEmail().id());
			node.loadReplies(dataset);
			node.children().asIterator().forEachRemaining(childNode -> {
				if (childNode instanceof EmailTreeNode cn)
					cn.loadReplies(dataset);
			});
			Preferences prefs = EmailDatasetBrowser.getPreferences();
			// Expands path if the setting allows it to, or the node is double clicked.
			if (prefs.getBoolean(PREF_AUTO_OPEN, true) || (lastNode == node)) {
				tree.expandPath(new TreePath(node.getPath()));
			}
			lastNode = node;
		}
	}
}
