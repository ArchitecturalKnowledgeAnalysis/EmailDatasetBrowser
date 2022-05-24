package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.Tag;
import nl.andrewl.email_indexer.data.TagRepository;
import nl.andrewl.email_indexer.data.search.filter.TagFilter;
import nl.andrewl.emaildatasetbrowser.view.LabelledField;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel that can be used to show and select a specific tag filter for
 * searching over the dataset. You can think of it as a fancy multi-select.
 */
public class TagFilterPanel extends JPanel {
	private static final String INCLUDE = "Include Selected";
	private static final String EXCLUDE = "Exclude Selected";

	private final JComboBox<String> typeSelect = new JComboBox<>(new String[]{INCLUDE, EXCLUDE});
	private final DefaultListModel<Tag> tagListModel = new DefaultListModel<>();
	private final JList<Tag> tagList = new JList<>(tagListModel);

	public TagFilterPanel(EmailDataset dataset, TagFilter currentFilter) {
		super(new BorderLayout(5, 5));
		add(new LabelledField("Filter Type", typeSelect), BorderLayout.NORTH);
		tagList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		add(new LabelledField("Tags", new JScrollPane(tagList)), BorderLayout.CENTER);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		var repo = new TagRepository(dataset);
		tagListModel.addAll(repo.findAll());

		setFilter(currentFilter);
	}

	public void setFilter(TagFilter filter) {
		typeSelect.setSelectedItem(switch (filter.type()) {
			case INCLUDE_ANY -> INCLUDE;
			case EXCLUDE_ANY -> EXCLUDE;
		});
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
		String selected = (String) typeSelect.getSelectedItem();
		if (selected == null) selected = EXCLUDE;
		TagFilter.Type type = switch (selected) {
			case INCLUDE -> TagFilter.Type.INCLUDE_ANY;
			case EXCLUDE -> TagFilter.Type.EXCLUDE_ANY;
			default -> TagFilter.Type.EXCLUDE_ANY;
		};
		List<Integer> selectedIds = tagList.getSelectedValuesList().stream().map(Tag::id).toList();
		return new TagFilter(selectedIds, type);
	}
}
