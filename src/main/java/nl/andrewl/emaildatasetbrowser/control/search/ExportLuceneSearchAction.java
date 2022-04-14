package nl.andrewl.emaildatasetbrowser.control.search;

import nl.andrewl.email_indexer.data.EmailEntryPreview;
import nl.andrewl.email_indexer.data.EmailIndexSearcher;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ExportLuceneSearchAction implements ActionListener {
    private final LuceneSearchPanel searchPanel;

    public ExportLuceneSearchAction(LuceneSearchPanel searchPanel) {
        this.searchPanel = searchPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String query = searchPanel.getQuery();
        if (query == null || searchPanel.getDataset() == null) return;

        JFileChooser fc = new JFileChooser(".");
        fc.setFileFilter(new FileNameExtensionFilter("Text files", ".txt"));
        fc.setAcceptAllFileFilterUsed(false);
        int result = fc.showSaveDialog(searchPanel);
        if (result != JFileChooser.APPROVE_OPTION) return;
        Path file = fc.getSelectedFile().toPath();

        ProgressDialog progress = ProgressDialog.minimalText(searchPanel, "Exporting Query Results");
        progress.append("Generating export for query: \"%s\"".formatted(query));
        var repo = new EmailRepository(searchPanel.getDataset());
        new EmailIndexSearcher().searchAsync(searchPanel.getDataset(), query)
                .handleAsync((emailIds, throwable) -> {
                    if (throwable != null) {
                        progress.append("An error occurred while searching: " + throwable.getMessage());
                    } else {
                        progress.append("Found %d emails.".formatted(emailIds.size()));
                        try {
                            List<EmailEntryPreview> emails = emailIds.parallelStream()
                                    .map(id -> repo.findPreviewById(id).orElse(null))
                                    .filter(Objects::nonNull)
                                    .peek(repo::loadRepliesRecursive)
                                    .toList();
                            writeExport(emails, repo, query, file, progress);
                        } catch (IOException ex) {
                            progress.append("An error occurred while exporting: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                    progress.done();
                    return null;
                });
    }

    private void writeExport(List<EmailEntryPreview> emails, EmailRepository repo, String query, Path file, Consumer<String> messageConsumer) throws IOException {
        try (PrintWriter p = new PrintWriter(new FileWriter(file.toFile()), false)) {
            p.println("Query: " + query);
            p.println("Exported at: " + ZonedDateTime.now());
            p.println("Tags: " + String.join(", ", repo.getAllTags()));
            p.println("Total emails: " + emails.size());
            p.println("\n");
            for (int i = 0; i < emails.size(); i++) {
                messageConsumer.accept("Exporting email #" + (i + 1));
                var email = emails.get(i);
                repo.loadRepliesRecursive(email);
                p.println("Email #" + (i + 1));
                exportEmail(email, repo, p, 0);
            }
        }
    }

    private void exportEmail(EmailEntryPreview email, EmailRepository repo, PrintWriter p, int indentLevel) {
        String indent = "\t".repeat(indentLevel);
        p.println(indent + "Message id: " + email.messageId());
        p.println(indent + "Subject: " + email.subject());
        p.println(indent + "Sent from: " + email.sentFrom());
        p.println(indent + "Date: " + email.date());
        p.println(indent + "Tags: " + String.join(", ", email.tags()));
        p.println(indent + "Hidden: " + email.hidden());
        repo.getBody(email.messageId()).ifPresent(body -> {
            p.println(indent + "Body---->>>");
            body.trim().lines().forEachOrdered(line -> p.println(indent + line));
            p.println(indent + "-------->>>");
        });
        if (!email.replies().isEmpty()) {
            p.println("Replies:");
            for (int i = 0; i < email.replies().size(); i++) {
                var reply = email.replies().get(i);
                p.println("\t" + indent + "Reply #" + (i + 1));
                exportEmail(reply, repo, p, indentLevel + 1);
                p.println();
            }
        }
        p.println();
    }
}
