package nl.andrewl.emaildatasetbrowser.view.search.searchpanel;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.email_indexer.data.search.EmailIndexSearcher;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportSample;
import nl.andrewl.emaildatasetbrowser.control.search.export.exporters.LuceneSearchExporter;
import nl.andrewl.emaildatasetbrowser.view.common.LabelledSearchField;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.EmailTreeNode;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

/**
 * Search panel using Lucene Search Queries.
 */
public final class LuceneSearchPanel extends SearchPanel {
    private LabelledSearchField queryField;

    public LuceneSearchPanel(EmailViewPanel emailViewPanel) {
        super(emailViewPanel);
    }

    @Override
    protected JPanel buildParameterPanel() {
        queryField = new LabelledSearchField(
                "Comma-Separated List of email IDs:",
                (e) -> doSearch());
        return queryField;
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
        new EmailIndexSearcher().searchAsync(getDataset(), queryField.getQueryField().getText(), getPageSize())
                .handleAsync((emailIds, throwable) -> {
                    if (throwable != null) {
                        String errorMessage = "Search terminated with error:\n%s"
                                .formatted(throwable.getMessage());
                        JOptionPane errorPane = new JOptionPane(errorMessage, JOptionPane.ERROR_MESSAGE);
                        JDialog dialog = errorPane.createDialog("Search Error");
                        dialog.setAlwaysOnTop(true);
                        dialog.setVisible(true);
                    } else {
                        showResults(emailIds);
                    }
                    return null;
                });

    }

    @Override
    protected void onClearClicked() {
        queryField.getQueryField().setText(null);
        emailTreeView.clear();
    }

    public String getQuery() {
        String query = queryField.getQueryField().getText();
        if (query == null || query.isBlank()) {
            return null;
        }
        return query.trim();
    }

    private void showResults(List<Long> emailIds) {
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
            setTotalEmails(nodes.size());
        });
    }
}
