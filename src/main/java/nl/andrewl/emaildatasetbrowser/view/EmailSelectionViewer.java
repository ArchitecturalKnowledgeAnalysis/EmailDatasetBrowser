package nl.andrewl.emaildatasetbrowser.view;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.EmailTreeNode;
import nl.andrewl.emaildatasetbrowser.view.search.EmailTreeSelectionListener;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.List;
import java.util.Objects;

/**
 * A separate view for browsing a pre-defined list of emails.
 */
public class EmailSelectionViewer extends JFrame {
	public EmailSelectionViewer(List<Long> emailIds, EmailDataset dataset) {
		super("Email Selection Viewer");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		EmailViewPanel emailViewPanel = new EmailViewPanel();
		emailViewPanel.setDataset(dataset);

		DefaultMutableTreeNode emailsRoot = new DefaultMutableTreeNode();
		DefaultTreeModel emailsModel = new DefaultTreeModel(emailsRoot);
		JTree emailsTree = new JTree(emailsModel);
		emailsTree.setRootVisible(false);
		emailsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		emailsTree.getSelectionModel().addTreeSelectionListener(new EmailTreeSelectionListener(emailViewPanel, emailsTree));
		var repo = new EmailRepository(dataset);
		emailIds.stream()
				.map(id -> repo.findPreviewById(id).orElse(null))
				.filter(Objects::nonNull)
				.map(EmailTreeNode::new)
				.forEachOrdered(emailsRoot::add);
		emailsModel.nodeStructureChanged(emailsRoot);
		emailsTree.expandPath(new TreePath(emailsRoot.getPath()));
		JScrollPane emailsScrollPane = new JScrollPane(emailsTree);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(emailsScrollPane);
		splitPane.add(emailViewPanel);

		setContentPane(splitPane);
		pack();
	}
}
