package nl.andrewl.emaildatasetbrowser.view.search.searchpanel;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.search.EmailSearchResult;
import nl.andrewl.email_indexer.data.search.EmailSearcher;
import nl.andrewl.email_indexer.data.search.SearchFilter;
import nl.andrewl.email_indexer.data.search.filter.HiddenFilter;
import nl.andrewl.email_indexer.data.search.filter.RootFilter;
import nl.andrewl.email_indexer.data.search.filter.TagFilter;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportSample;
import nl.andrewl.emaildatasetbrowser.control.search.export.exporters.SimpleExporter;
import nl.andrewl.emaildatasetbrowser.view.BooleanSelect;
import nl.andrewl.emaildatasetbrowser.view.SwingUtils;
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
public class SimpleBrowsePanel extends SearchPanel {
	private TagFilter includeFilter = TagFilter.excludeNone();
	private TagFilter excludeFilter = TagFilter.excludeNone();

	private BooleanSelect showHiddenSelect;
	private BooleanSelect showRootSelect;

	public SimpleBrowsePanel(EmailViewPanel emailViewPanel) {
		super(emailViewPanel);
	}

	@Override
	public void setDataset(EmailDataset dataset) {
		try {// Wrap this in a try-catch to catch any weird NPEs that occur due to event propagation order.
			super.setDataset(dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (dataset != null) {
			SwingUtilities.invokeLater(this::doSearch);
		}
	}

	@Override
	protected JPanel buildParameterPanel() {
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
		filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));

		showHiddenSelect = new BooleanSelect("All", "Only Hidden", "Only Shown");
		filterPanel.add(buildControlPanel("Show Hidden", showHiddenSelect));
		showHiddenSelect.setSelectedValue(false);
		showHiddenSelect.addActionListener(e -> searchFromBeginning());

		showRootSelect = new BooleanSelect("All", "Only Roots", "Only Children");
		filterPanel.add(buildControlPanel("Show Root", showRootSelect));
		showRootSelect.setSelectedValue(null);
		showRootSelect.addActionListener(e -> searchFromBeginning());

		JButton editTagFilterButton = new JButton("Edit");
		filterPanel.add(buildControlPanel("Tag Filter", editTagFilterButton));
		editTagFilterButton.addActionListener(e -> onEditTagFilterClicked());
		searchPanel.add(filterPanel);
		return searchPanel;
	}

	@Override
	protected ExportSample buildExporter() {
		return new SimpleExporter(this);
	}

	@Override
	protected void doSearch() {
		super.doSearch();
		SwingUtils.setAllButtonsEnabled(this, false);
		new EmailSearcher(getDataset()).findAll(getCurrentPage(), getPageSize(), getCurrentSearchFilters())
				.handle((results, throwable) -> {
					SwingUtilities.invokeLater(() -> {
						SwingUtils.setAllButtonsEnabled(this, true);
						showResults(results);
					});
					return null;
				});

	}

	@Override
	protected void onClearClicked() {
		this.emailTreeView.clear();
		includeFilter = TagFilter.excludeNone();
		excludeFilter = TagFilter.excludeNone();
		showRootSelect.setSelectedValue(null);
		showHiddenSelect.setSelectedValue(false);
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

	private void showResults(EmailSearchResult result) {
		emailTreeView.setEmails(result.emails(), getDataset(), true);
		toggleChangePageButton(true, result.hasNextPage());
		toggleChangePageButton(false, result.hasPreviousPage());
		setTotalEmails((int) result.totalResultCount());
	}

	private void onEditTagFilterClicked() {
		if (getDataset() == null) {
			return;
		}
		TagFilterDialog dialog = new TagFilterDialog(SwingUtilities.getWindowAncestor(this), this,
				includeFilter, excludeFilter, (ti, te) -> {
					this.includeFilter = ti;
					this.excludeFilter = te;
					doSearch();
				});
		dialog.setVisible(true);
	}
}
