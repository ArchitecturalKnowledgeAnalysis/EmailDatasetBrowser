package nl.andrewl.emaildatasetbrowser.view.search.tagfilter;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.Tag;
import nl.andrewl.email_indexer.data.TagRepository;
import nl.andrewl.email_indexer.data.search.filter.TagFilter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel that can be used to show and select a specific tag filter for
 * searching over the dataset. You can think of it as a fancy multi-select.
 */
public class TagFilterPanel extends JPanel {
	private final DefaultListModel<Tag> tagListModel = new DefaultListModel<>();
	private final JList<Tag> tagList = new JList<>(tagListModel);
	private final TagFilter.Type filterType;

	public TagFilterPanel(EmailDataset dataset, TagFilter currentFilter, TagFilter.Type filterType) {
		super(new BorderLayout(5, 5));
		this.filterType = filterType;
		tagList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		add(new JScrollPane(tagList), BorderLayout.CENTER);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		var repo = new TagRepository(dataset);
		tagListModel.addAll(repo.findAll());

		setFilter(currentFilter);
	}

	public void setFilter(TagFilter filter) {
		int[] selectedIndices = new int[filter.tagIds().size()];
		int idx = 0;
		for (int tagId : filter.tagIds()) {
			for (int i = 0; i < tagListModel.size(); i++) {
				if (tagListModel.get(i).id() == tagId) {
					selectedIndices[idx++] = i;
					break;
				}
			}
		}
		tagList.setSelectedIndices(selectedIndices);
	}

	public TagFilter getFilter() {
		List<Integer> selectedIds = tagList.getSelectedValuesList().stream().map(Tag::id).toList();
		return new TagFilter(selectedIds, filterType);
	}
}
