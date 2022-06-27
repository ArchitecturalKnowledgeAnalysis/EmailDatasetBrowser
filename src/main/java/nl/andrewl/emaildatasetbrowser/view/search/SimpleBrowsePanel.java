package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.search.EmailSearchResult;
import nl.andrewl.email_indexer.data.search.EmailSearcher;
import nl.andrewl.email_indexer.data.search.SearchFilter;
import nl.andrewl.email_indexer.data.search.filter.HiddenFilter;
import nl.andrewl.email_indexer.data.search.filter.RootFilter;
import nl.andrewl.email_indexer.data.search.filter.TagFilter;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.control.search.export.exporters.SimpleExporter;
import nl.andrewl.emaildatasetbrowser.view.BooleanSelect;
import nl.andrewl.emaildatasetbrowser.view.DatasetChangeListener;
import nl.andrewl.emaildatasetbrowser.view.SwingUtils;
import nl.andrewl.emaildatasetbrowser.view.email.EmailTreeView;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.tagfilter.TagFilterDialog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for browsing the dataset using some filters and a paginated results
 * list.
 */
public class SimpleBrowsePanel extends JPanel implements DatasetChangeListener {
	public final static String PREF_BROWSE_PAGE_SIZE = "pref_browse_page_size";

	private EmailDataset currentDataset;
	private int currentPage = 1;

	private TagFilter includeFilter = TagFilter.excludeNone();
	private TagFilter excludeFilter = TagFilter.excludeNone();

	private final EmailTreeView emailTreeView = new EmailTreeView();

	private final BooleanSelect showHiddenSelect = new BooleanSelect("All", "Only Hidden", "Only Shown");
	private final BooleanSelect showRootSelect = new BooleanSelect("All", "Only Roots", "Only Children");

	private final JButton editTagFilterButton = new JButton("Edit");
	private final JButton nextPageButton = new JButton("Next");
	private final JButton previousPageButton = new JButton("Prev");
	private final JButton exportButton = new JButton("Export Selection");
	private final JLabel currentPageLabel = new JLabel("Page 1 of 1");
	private final JLabel sizeLabel = new JLabel("Showing 0 of 0 results");

	public SimpleBrowsePanel(EmailViewPanel emailViewPanel) {
		super(new BorderLayout());
		this.add(buildFilterPanel(), BorderLayout.NORTH);
		this.add(emailTreeView, BorderLayout.CENTER);

		emailTreeView.addSelectionListener(new EmailTreeSelectionListener(emailViewPanel, emailTreeView.getTree()));
	}

	public void setDataset(EmailDataset ds) {
		emailTreeView.clear();
		this.currentDataset = ds;

		// Set all elements
		boolean enabled = ds != null;

		editTagFilterButton.setEnabled(enabled);
		includeFilter = TagFilter.excludeNone();
		excludeFilter = TagFilter.excludeNone();

		showHiddenSelect.setSelectedValue(false);
		showHiddenSelect.setEnabled(enabled);
		showRootSelect.setSelectedValue(null);
		showRootSelect.setEnabled(enabled);

		nextPageButton.setEnabled(enabled);
		previousPageButton.setEnabled(enabled);
		currentPageLabel.setText("Page 1 of 1");
		sizeLabel.setText("Showing 0 of 0 results");
		exportButton.setEnabled(enabled);

		// Set the dataset after updating all controls.
		if (ds != null) {
			this.currentPage = 1;
			doSearch();
		}
	}

	public EmailDataset getDataset() {
		return this.currentDataset;
	}

	private void doExport() {
		if (currentDataset == null) {
			return;
		}
		ExportPanel panel = new ExportPanel(
				SwingUtilities.getWindowAncestor(this),
				this.getDataset(),
				new SimpleExporter(this));
		panel.setVisible(true);
	}

	private void doSearch() {
		if (currentDataset == null) {
			emailTreeView.clear();
			return;
		}
		SwingUtils.setAllButtonsEnabled(this, false);
		int pagesize = EmailDatasetBrowser.getPreferences().getInt(PREF_BROWSE_PAGE_SIZE, 20);
		new EmailSearcher(currentDataset).findAll(this.currentPage, pagesize, getCurrentSearchFilters())
				.handle((results, throwable) -> {
					SwingUtilities.invokeLater(() -> {
						SwingUtils.setAllButtonsEnabled(this, true);
						showResults(results);
						this.currentPageLabel.setText("Page %d of %d".formatted(results.page(), results.pageCount()));
						this.sizeLabel.setText("Showing %d of %d results".formatted(results.emails().size(),
								results.totalResultCount()));
					});
					return null;
				});
	}

	public List<SearchFilter> getCurrentSearchFilters() {
		List<SearchFilter> filters = new ArrayList<>(2);
		Boolean hidden = showHiddenSelect.getSelectedValue();
		if (hidden != null)
			filters.add(new HiddenFilter(hidden));
		Boolean showRoot = showRootSelect.getSelectedValue();
		if (showRoot != null)
			filters.add(new RootFilter(showRoot));
		if (!includeFilter.getWhereClause().isBlank())
			filters.add(includeFilter);
		if (!excludeFilter.getWhereClause().isBlank())
			filters.add(excludeFilter);
		return filters;
	}

	private void searchFromBeginning() {
		this.currentPage = 1;
		doSearch();
	}

	private void showResults(EmailSearchResult result) {
		emailTreeView.setEmails(result.emails(), currentDataset);
		nextPageButton.setEnabled(result.hasNextPage());
		previousPageButton.setEnabled(result.hasPreviousPage());
	}

	private JPanel buildFilterPanel() {
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
		filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		filterPanel.add(buildControlPanel("Show Hidden", showHiddenSelect));
		showHiddenSelect.setSelectedValue(false);
		showHiddenSelect.addActionListener(e -> searchFromBeginning());
		filterPanel.add(buildControlPanel("Show Root", showRootSelect));
		showRootSelect.setSelectedValue(null);
		showRootSelect.addActionListener(e -> searchFromBeginning());
		filterPanel.add(buildControlPanel("Tag Filter", editTagFilterButton));
		editTagFilterButton.addActionListener(e -> {
			if (currentDataset != null) {
				TagFilterDialog dialog = new TagFilterDialog(SwingUtilities.getWindowAncestor(this), this,
						includeFilter, excludeFilter, (ti, te) -> {
							this.includeFilter = ti;
							this.excludeFilter = te;
							doSearch();
						});
				dialog.setVisible(true);
			}
		});
		searchPanel.add(filterPanel);

		// Page control panel settings.
		nextPageButton.addActionListener(e -> {
			this.currentPage++;
			doSearch();
		});
		previousPageButton.addActionListener(e -> {
			this.currentPage--;
			doSearch();
		});

		JPanel pageControlPanel = new JPanel(new GridLayout(1, 2));
		pageControlPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		previousPageButton.setMargin(new Insets(0, 0, 0, 0));
		nextPageButton.setMargin(new Insets(0, 0, 0, 0));
		pageControlPanel.add(previousPageButton);
		pageControlPanel.add(nextPageButton);
		searchPanel.add(pageControlPanel);

		JPanel footerPanel = new JPanel(new GridLayout(3, 1));
		footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		footerPanel.add(currentPageLabel);
		footerPanel.add(sizeLabel);
		exportButton.addActionListener(e -> doExport());
		footerPanel.add(exportButton);
		searchPanel.add(footerPanel);

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
		// Add a lower margin to each control panel.
		p.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
		return p;
	}

	@Override
	public void datasetChanged(EmailDataset ds) {
		setDataset(ds);
	}
}
