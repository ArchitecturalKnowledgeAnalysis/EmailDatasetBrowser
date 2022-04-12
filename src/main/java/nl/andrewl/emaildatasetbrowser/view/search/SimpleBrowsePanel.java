package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailEntryPreview;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.email_indexer.data.EmailSearchResult;
import nl.andrewl.emaildatasetbrowser.EmailListItemRenderer;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for browsing the dataset using some filters and a paginated results
 * list.
 */
public class SimpleBrowsePanel extends JPanel {
	private final DefaultListModel<EmailEntryPreview> emailListModel;
	private EmailDataset currentDataset;
	private int currentPage = 1;

	private final JComboBox<Boolean> showHiddenComboBox = new JComboBox<>(new Boolean[]{null, true, false});
	private final JComboBox<Boolean> showTaggedComboBox = new JComboBox<>(new Boolean[]{null, true, false});
	private final JButton nextPageButton = new JButton("Next Page");
	private final JButton previousPageButton = new JButton("Previous Page");
	private final JLabel currentPageLabel = new JLabel("Page 1 of 1");
	private final JLabel sizeLabel = new JLabel("Showing 0 of 0 results");

	public SimpleBrowsePanel(EmailViewPanel emailViewPanel) {
		super(new BorderLayout());
		this.setPreferredSize(new Dimension(400, -1));

		this.add(buildFilterPanel(), BorderLayout.NORTH);

		this.emailListModel = new DefaultListModel<>();
		JList<EmailEntryPreview> emailList = new JList<>(this.emailListModel);
		emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		emailList.setCellRenderer(new EmailListItemRenderer());
		emailList.addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) return;
			var selected = emailList.getSelectedValue();
			if (selected != null) {
				new EmailRepository(currentDataset).findEmailById(selected.messageId()).ifPresent(emailViewPanel::setEmail);
			}
		});
		JScrollPane listScroller = new JScrollPane(emailList);
		this.add(listScroller, BorderLayout.CENTER);
	}

	public void setDataset(EmailDataset ds) {
		this.emailListModel.clear();

		// Set all elements
		boolean enabled = ds != null;

		showHiddenComboBox.setSelectedItem(false);
		showHiddenComboBox.setEnabled(enabled);

		showTaggedComboBox.setSelectedItem(null);
		showTaggedComboBox.setEnabled(enabled);

		nextPageButton.setEnabled(enabled);
		previousPageButton.setEnabled(enabled);

		// Set the dataset after updating all controls.
		this.currentDataset = ds;
		if (ds != null) {
			this.currentPage = 1;
			doSearch();
		}
	}

	private void doSearch() {
		if (currentDataset == null) {
			this.emailListModel.clear();
			return;
		}
		var results = new EmailRepository(currentDataset).findAll(
				this.currentPage,
				20,
				(Boolean) this.showHiddenComboBox.getSelectedItem(),
				(Boolean) this.showTaggedComboBox.getSelectedItem()
		);
		showResults(results);
		this.currentPageLabel.setText("Page %d of %d".formatted(results.page(), results.pageCount()));
		this.sizeLabel.setText("Showing %d of %d results".formatted(results.emails().size(), results.totalResultCount()));
	}

	private void showResults(EmailSearchResult result) {
		this.emailListModel.clear();
		this.emailListModel.addAll(result.emails());
		nextPageButton.setEnabled(result.hasNextPage());
		previousPageButton.setEnabled(result.hasPreviousPage());
	}

	private JPanel buildFilterPanel() {
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));

//		JPanel textSearchPanel = new JPanel(new GridBagLayout());
//		GridBagConstraints c = new GridBagConstraints();
//		c.gridx = 0; c.gridy = 0;
//		c.weightx = 0.99; c.weighty = 0.5;
//		c.anchor = GridBagConstraints.LINE_START;
//		c.fill = GridBagConstraints.BOTH;
//		c.insets = new Insets(2, 2, 2, 2);
//		textSearchPanel.add(searchField, c);
//		c.gridx = 1;
//		c.weightx = 0.01;
//		c.fill = GridBagConstraints.BOTH;
//		textSearchPanel.add(searchButton, c);
//		searchButton.addActionListener(e -> {
//			if (this.currentDataset != null) {
//				var results = new EmailRepository(currentDataset).search(searchField.getText());
//				showResults(results);
//			}
//		});
//		searchButton.setToolTipText("Search email threads using Lucene indexes.");
//		searchPanel.add(textSearchPanel);

		JPanel filterPanel = new JPanel(new GridLayout(0, 2, 5, 5));
		filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		filterPanel.add(buildControlPanel("Show Hidden", showHiddenComboBox));
		showHiddenComboBox.setSelectedItem(false);
		showHiddenComboBox.addActionListener(e -> {
			if (currentDataset == null) return;
			this.currentPage = 1;
			doSearch();
		});
		filterPanel.add(buildControlPanel("Show Tagged", showTaggedComboBox));
		showTaggedComboBox.addActionListener(e -> {
			if (currentDataset == null) return;
			this.currentPage = 1;
			doSearch();
		});
		nextPageButton.addActionListener(e -> {
			this.currentPage++;
			doSearch();
		});
		previousPageButton.addActionListener(e -> {
			this.currentPage--;
			doSearch();
		});
		filterPanel.add(previousPageButton);
		filterPanel.add(nextPageButton);
		filterPanel.add(currentPageLabel);
		filterPanel.add(sizeLabel);
		searchPanel.add(filterPanel);

		return searchPanel;
	}

	private JPanel buildControlPanel(String label, Component component) {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.01;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(2, 2, 2, 2);
		p.add(new JLabel(label), c);
		c.weightx = 0.99;
		c.fill = GridBagConstraints.BOTH;
		p.add(component);
		return p;
	}
}
