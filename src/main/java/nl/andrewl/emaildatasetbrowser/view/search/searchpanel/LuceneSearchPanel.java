package nl.andrewl.emaildatasetbrowser.view.search.searchpanel;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.email_indexer.data.search.EmailIndexSearcher;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportSample;
import nl.andrewl.emaildatasetbrowser.control.search.export.exporters.LuceneSearchExporter;
import nl.andrewl.emaildatasetbrowser.view.ConcreteKeyEventListener;
import nl.andrewl.emaildatasetbrowser.view.ConcreteKeyEventListener.KeyEventType;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.EmailTreeNode;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Search panel using Lucene Search Queries.
 */
public final class LuceneSearchPanel extends SearchPanel {

    private JTextArea queryField;

    public LuceneSearchPanel(EmailViewPanel emailViewPanel) {
        super(emailViewPanel);
    }

    @Override
    protected JPanel buildParameterPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
        inputPanel.add(new JLabel("Lucene Query:"));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        queryField = new JTextArea();
        queryField.setMargin(new Insets(5,5,5,5));
        queryField.setLineWrap(true);
        ConcreteKeyEventListener rText = new ConcreteKeyEventListener()
                .addKeyListener(KeyEventType.KEY_RELEASED, KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK,
                        (e) -> doSearch());
        queryField.addKeyListener(rText);
        var queryScrollPane = new JScrollPane(queryField);
        queryScrollPane.setPreferredSize(new Dimension(-1, 100));
        inputPanel.add(queryScrollPane, BorderLayout.CENTER);
        return inputPanel;
    }

    @Override
    protected ExportSample buildExporter() {
        return new LuceneSearchExporter(this);
    }

    @Override
    protected void doSearch() {
        super.doSearch();
        String query = getQuery();
        if (query == null) {
            return;
        }
        final Instant start = Instant.now();
        new EmailIndexSearcher().searchAsync(getDataset(), queryField.getText(), getPageSize())
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

    @Override
    protected void onClearClicked() {
        queryField.setText(null);
        emailTreeView.clear();
    }

    public String getQuery() {
        String query = queryField.getText();
        if (query == null || query.isBlank()) {
            return null;
        }
        return query.trim();
    }

    private void showResults(final Instant start, List<Long> emailIds) {
        EmailDataset dataset = getDataset();
        var repo = new EmailRepository(dataset);
        int resultCount = EmailDatasetBrowser.getPreferences().getInt(SimpleBrowsePanel.PREF_BROWSE_PAGE_SIZE, 100);
        List<EmailTreeNode> nodes = emailIds.stream()
                .map(id -> repo.findPreviewById(id).orElse(null))
                .filter(Objects::nonNull)
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
