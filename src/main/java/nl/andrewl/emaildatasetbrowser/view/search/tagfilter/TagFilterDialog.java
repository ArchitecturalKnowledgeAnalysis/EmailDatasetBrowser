package nl.andrewl.emaildatasetbrowser.view.search.tagfilter;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.Tag;
import nl.andrewl.email_indexer.data.TagRepository;
import nl.andrewl.email_indexer.data.search.filter.TagFilter;
import nl.andrewl.emaildatasetbrowser.view.LabelledField;
import nl.andrewl.emaildatasetbrowser.view.search.searchpanel.SimpleBrowsePanel;

import java.awt.*;
import javax.swing.*;
import java.util.function.BiConsumer;

/**
 * Swing dialog that allows users to update the tag filters.
 * Contains an inclusion filter and an exclusion filter.
 */
public class TagFilterDialog extends JDialog {
    private final SimpleBrowsePanel browsePanel;
    private final BiConsumer<TagFilter, TagFilter> onOkayClickedAction;

    private TagFilterPanel includeTagFilterPanel;
    private TagFilterPanel excludeTagFilterPanel;

    private TagFilter includeFilter = TagFilter.includeNone();
    private TagFilter excludeFilter = TagFilter.excludeNone();

    /**
     * 
     * @param owner               the parent window of this dialog.
     * @param browsePanel         the corresponding browsepanel.
     * @param includeFilter       the initial include filter.
     * @param excludeFilter       the initial exclude filter.
     * @param onOkayClickedAction action that is invoked when the okay button is
     *                            clicked. Its parameters are the include filter and
     *                            the exclude filter, respectively.
     */
    public TagFilterDialog(Window owner, SimpleBrowsePanel browsePanel, TagFilter includeFilter,
            TagFilter excludeFilter, BiConsumer<TagFilter, TagFilter> onOkayClickedAction) {
        super(owner, Dialog.ModalityType.APPLICATION_MODAL);
        this.includeFilter = includeFilter;
        this.excludeFilter = excludeFilter;
        this.browsePanel = browsePanel;
        this.onOkayClickedAction = onOkayClickedAction;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        buildFiltersPanels(panel);
        buildButtonPanel(panel);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(this);
    }

    private void buildFiltersPanels(JPanel parent) {
        EmailDataset currentDataset = this.browsePanel.getDataset();
        // include panel
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        includeTagFilterPanel = new TagFilterPanel(currentDataset, includeFilter, TagFilter.Type.INCLUDE_ANY);
        includeTagFilterPanel.setPreferredSize(new Dimension(400, 400));
        panel.add(new LabelledField("Included Tags", includeTagFilterPanel), BorderLayout.CENTER);
        // exclude panel
        excludeTagFilterPanel = new TagFilterPanel(currentDataset, excludeFilter, TagFilter.Type.EXCLUDE_ANY);
        excludeTagFilterPanel.setPreferredSize(new Dimension(400, 400));
        panel.add(new LabelledField("Excluded Tags", excludeTagFilterPanel), BorderLayout.CENTER);
        parent.add(panel);
    }

    private void buildButtonPanel(JPanel parent) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(ev -> dispose());
        JButton allTagsButton = new JButton("All Tags");
        allTagsButton.addActionListener(ev -> onAllTagsClicked());
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(ev -> onClearClicked());
        JButton okayButton = new JButton("Okay");
        okayButton.addActionListener(ev -> onOkayClicked());
        buttonPanel.add(cancelButton);
        buttonPanel.add(allTagsButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(okayButton);
        parent.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onAllTagsClicked() {
        var ids = new TagRepository(browsePanel.getDataset()).findAll().stream().map(Tag::id).toList();
        includeTagFilterPanel.setFilter(TagFilter.including(ids));
        excludeTagFilterPanel.setFilter(TagFilter.excludeNone());
    }

    private void onClearClicked() {
        includeTagFilterPanel.setFilter(TagFilter.includeNone());
        excludeTagFilterPanel.setFilter(TagFilter.excludeNone());
    }

    private void onOkayClicked() {
        dispose();
        this.onOkayClickedAction.accept(includeTagFilterPanel.getFilter(), excludeTagFilterPanel.getFilter());
    }
}
