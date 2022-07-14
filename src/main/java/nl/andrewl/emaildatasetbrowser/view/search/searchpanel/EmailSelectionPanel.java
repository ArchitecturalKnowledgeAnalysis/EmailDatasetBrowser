package nl.andrewl.emaildatasetbrowser.view.search.searchpanel;

import nl.andrewl.email_indexer.data.EmailEntryPreview;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportSample;
import nl.andrewl.emaildatasetbrowser.view.common.LabelledSearchField;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Search panel using email ids.
 */
public final class EmailSelectionPanel extends SearchPanel {
    private LabelledSearchField queryField;

    public EmailSelectionPanel(EmailViewPanel emailViewPanel) {
        super(emailViewPanel);
        disableExport();
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
        // This method isn't called as the export
        // button is disabled in the constructor.
        // TODO: Implement this, if ever needed.
        throw new RuntimeException("Method not implemented yet...");
    }

    @Override
    protected void doSearch() {
        super.doSearch();
        emailTreeView.clear();
        String ids = queryField.getQueryField().getText();
        if (ids != null && !ids.isBlank()) {
            Pattern pattern = Pattern.compile("(\\d+)(?>\\s*,\\s*)?");
            Matcher matcher = pattern.matcher(ids);
            List<Long> emailIds = new ArrayList<>();
            while (matcher.find()) {
                emailIds.add(Long.parseLong(matcher.group(1)));
            }
            if (emailIds.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid list of ids. Should contain at least one id.");
            } else {
                var repo = new EmailRepository(getDataset());
                List<EmailEntryPreview> emails = emailIds.stream()
                        .map(id -> repo.findPreviewById(id).orElse(null))
                        .filter(Objects::nonNull).toList();
                SwingUtilities.invokeLater(() -> {
                    emailTreeView.setEmails(emails, getDataset(), true);
                    setTotalEmails(emails.size());
                });
            }
        }
    }

    @Override
    protected void onClearClicked() {
        emailTreeView.clear();
        queryField.getQueryField().setText(null);
    }
}
