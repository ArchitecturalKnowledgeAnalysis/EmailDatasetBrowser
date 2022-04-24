package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailEntryPreview;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.email_indexer.data.search.EmailSearchResult;
import nl.andrewl.email_indexer.data.search.EmailSearcher;
import nl.andrewl.email_indexer.data.search.SearchFilter;
import nl.andrewl.email_indexer.data.search.filter.HiddenFilter;
import nl.andrewl.emaildatasetbrowser.EmailListItemRenderer;
import nl.andrewl.emaildatasetbrowser.view.SwingUtils;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
	private final JButton nextPageButton = new JButton("Next");
	private final JButton previousPageButton = new JButton("Prev");
	private final JLabel currentPageLabel = new JLabel("Page 1 of 1");
	private final JLabel sizeLabel = new JLabel("Showing 0 of 0 results");

	public SimpleBrowsePanel(EmailViewPanel emailViewPanel) {
		super(new BorderLayout());
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
		this.currentDataset = ds;

		// Set all elements
		boolean enabled = ds != null;

		showHiddenComboBox.setSelectedItem(false);
		showHiddenComboBox.setEnabled(enabled);

		showTaggedComboBox.setSelectedItem(null);
		showTaggedComboBox.setEnabled(enabled);

		nextPageButton.setEnabled(enabled);
		previousPageButton.setEnabled(enabled);

		// Set the dataset after updating all controls.
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
		List<SearchFilter> filters = new ArrayList<>(2);
		Boolean hidden = (Boolean) this.showHiddenComboBox.getSelectedItem();
		if (hidden != null) filters.add(new HiddenFilter(hidden));
		SwingUtils.setAllButtonsEnabled(this, false);
		new EmailSearcher(currentDataset).findAll(this.currentPage, 20, filters)
			.handle((results, throwable) -> {
				SwingUtilities.invokeLater(() -> {
					SwingUtils.setAllButtonsEnabled(this, true);
					showResults(results);
					this.currentPageLabel.setText("Page %d of %d".formatted(results.page(), results.pageCount()));
					this.sizeLabel.setText("Showing %d of %d results".formatted(results.emails().size(), results.totalResultCount()));
				});
				return null;
			});
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

		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
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
		searchPanel.add(filterPanel);

		nextPageButton.addActionListener(e -> {
			this.currentPage++;
			doSearch();
		});
		previousPageButton.addActionListener(e -> {
			this.currentPage--;
			doSearch();
		});

		JPanel pageControlPanel = new JPanel(new GridLayout(1, 2));
		previousPageButton.setMargin(new Insets(0, 0, 0, 0));
		nextPageButton.setMargin(new Insets(0, 0, 0, 0));
		pageControlPanel.add(previousPageButton);
		pageControlPanel.add(nextPageButton);
		searchPanel.add(pageControlPanel);

		searchPanel.add(currentPageLabel);
		searchPanel.add(sizeLabel);

		return searchPanel;
	}

	private JPanel buildControlPanel(String label, Component component) {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.01;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);
		p.add(new JLabel(label), c);
		c.weightx = 0.99;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.BOTH;
		p.add(component);
		return p;
	}
}
