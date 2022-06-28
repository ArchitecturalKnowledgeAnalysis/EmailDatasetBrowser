package nl.andrewl.emaildatasetbrowser.view.search.searchpanel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import nl.andrewl.email_indexer.data.EmailEntryPreview;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.control.search.export.ExportSample;
import nl.andrewl.emaildatasetbrowser.view.ConcreteKeyEventListener;
import nl.andrewl.emaildatasetbrowser.view.ConcreteKeyEventListener.KeyEventType;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;

/**
 * Search panel using email ids.
 */
public final class EmailSelectionPanel extends SearchPanel {
    private JTextArea queryField;

    public EmailSelectionPanel(EmailViewPanel emailViewPanel) {
        super(emailViewPanel);
        disableExport();
    }

    @Override
    protected JPanel buildParameterPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(new JLabel("Comma-Separated List of IDs:"));
        queryField = new JTextArea();
        queryField.setLineWrap(true);
        inputPanel.add(queryField);
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
        // This method isn't called as the export
        // button is disabled in the constructor.
        // TODO: Implement this, if ever needed.
        throw new RuntimeException("Method not implemented yet...");
    }

    @Override
    protected void doSearch() {
        super.doSearch();
        emailTreeView.clear();
        String ids = queryField.getText();
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
                emailTreeView.setEmails(emails, getDataset());
                setTotalEmails(emails.size());
            }
        }
    }

    @Override
    protected void onClearClicked() {
        emailTreeView.clear();
        queryField.setText(null);
    }
}
