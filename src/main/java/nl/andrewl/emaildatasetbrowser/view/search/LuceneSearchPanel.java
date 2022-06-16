package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.email_indexer.data.TagRepository;
import nl.andrewl.email_indexer.data.search.EmailIndexSearcher;
import nl.andrewl.emaildatasetbrowser.control.search.export.exporters.LuceneSearchExporter;
import nl.andrewl.emaildatasetbrowser.view.ConcreteKeyEventListener;
import nl.andrewl.emaildatasetbrowser.view.ConcreteKeyEventListener.KeyEventType;
import nl.andrewl.emaildatasetbrowser.view.email.EmailTreeView;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A panel for executing Lucene search queries and examining the results.
 */
public class LuceneSearchPanel extends JPanel {
    private EmailDataset dataset;

    private final EmailTreeView emailTreeView = new EmailTreeView();

    private final JTextArea queryField;
    private final JButton searchButton = new JButton("Search");
    private final JSpinner resultCountSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
    private final JCheckBox hideTaggedCheckbox = new JCheckBox("Hide Tagged");
    private final JButton exportButton = new JButton("Export");

    public LuceneSearchPanel(EmailViewPanel emailViewPanel) {
        super(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());
        queryField = new JTextArea();
        queryField.setLineWrap(true);
        ConcreteKeyEventListener rText = new ConcreteKeyEventListener()
                .addKeyListener(KeyEventType.KEY_RELEASED, KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK, (e) -> doSearch());
        queryField.addKeyListener(rText);
        var queryScrollPane = new JScrollPane(queryField);
        queryScrollPane.setPreferredSize(new Dimension(-1, 100));
        inputPanel.add(queryScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(searchButton);
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(clearButton);
        hideTaggedCheckbox.setToolTipText("Removes tagged emails from search results.");
        buttonPanel.add(hideTaggedCheckbox);
        bottomPanel.add(buttonPanel);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exportPanel.add(resultCountSpinner);
        exportPanel.add(exportButton);
        bottomPanel.add(exportPanel);

        inputPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.NORTH);

        emailTreeView.addSelectionListener(new EmailTreeSelectionListener(emailViewPanel, emailTreeView.getTree()));
        add(emailTreeView, BorderLayout.CENTER);

        searchButton.addActionListener(e -> doSearch());
        clearButton.addActionListener(e -> {
            queryField.setText(null);
            emailTreeView.clear();
        });
        exportButton.addActionListener((e) -> {
            ExportPanel panel = new ExportPanel(
                    SwingUtilities.getWindowAncestor(this),
                    getDataset(),
                    new LuceneSearchExporter(this));
            panel.setVisible(true);
        });
    }

    public void setDataset(EmailDataset dataset) {
        this.dataset = dataset;
        emailTreeView.clear();
        searchButton.setEnabled(dataset != null);
        exportButton.setEnabled(dataset != null);
    }

    public EmailDataset getDataset() {
        return dataset;
    }

    public String getQuery() {
        String query = queryField.getText();
        if (query == null || query.isBlank()) {
            return null;
        }
        return query.trim();
    }

    public int getResultCount() {
        return (int) resultCountSpinner.getValue();
    }

    private void doSearch() {
        emailTreeView.clear();
        String query = getQuery();
        if (query == null) {
            return;
        }

        final Instant start = Instant.now();
        new EmailIndexSearcher().searchAsync(dataset, queryField.getText(), getResultCount())
                .handleAsync((emailIds, throwable) -> {
                    if (throwable != null) {
                        String errorMessage = "Search terminated with error:\n%s"
                                .formatted(throwable.getMessage());
                        JOptionPane errorPane = new JOptionPane(errorMessage, JOptionPane.ERROR_MESSAGE);
                        JDialog dialog = errorPane.createDialog("Search Error");
                        dialog.setAlwaysOnTop(true);
                        dialog.setVisible(true);
                    } else {
                        showResults(start, emailIds);
                    }
                    return null;
                });
    }

    private void showResults(final Instant start, List<Long> emailIds) {
        var repo = new EmailRepository(dataset);
        var tagRepo = new TagRepository(dataset);
        int resultCount = getResultCount();
        List<EmailTreeNode> nodes = emailIds.stream()
                .map(id -> repo.findPreviewById(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(email -> !this.hideTaggedCheckbox.isSelected() || tagRepo.getTags(email.id()).isEmpty())
                .map(EmailTreeNode::new)
                .limit(resultCount)
                .toList();
        SwingUtilities.invokeLater(() -> {
            int i = 1;
            for (var node : nodes) {
                node.setRootResultIndex(i++);
                node.loadReplies(dataset);
            }
            emailTreeView.setEmailNodes(nodes);
        });
    }
}
