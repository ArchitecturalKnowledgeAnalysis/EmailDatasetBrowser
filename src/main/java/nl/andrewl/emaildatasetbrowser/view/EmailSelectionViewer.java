package nl.andrewl.emaildatasetbrowser.view;

import nl.andrewl.email_indexer.data.EmailEntryPreview;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.email.EmailTreeView;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.EmailTreeSelectionListener;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

/**
 * A separate view for browsing a pre-defined list of emails.
 */
public class EmailSelectionViewer extends JFrame {
	public EmailSelectionViewer(List<Long> emailIds, EmailDatasetBrowser browser) {
		super("Email Selection Viewer");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		EmailViewPanel emailViewPanel = new EmailViewPanel(browser);
		browser.addListener(emailViewPanel);
		emailViewPanel.setDataset(browser.getCurrentDataset());

		var treeView = new EmailTreeView();
		treeView.addSelectionListener(new EmailTreeSelectionListener(emailViewPanel, treeView.getTree()));

		var repo = new EmailRepository(browser.getCurrentDataset());
		List<EmailEntryPreview> emails = emailIds.stream()
				.map(id -> repo.findPreviewById(id).orElse(null))
				.filter(Objects::nonNull).toList();
		treeView.setEmails(emails, browser.getCurrentDataset());

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(treeView);
		splitPane.add(emailViewPanel);

		setContentPane(splitPane);
		pack();
	}
}
