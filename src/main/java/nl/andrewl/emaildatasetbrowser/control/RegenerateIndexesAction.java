package nl.andrewl.emaildatasetbrowser.control;

import nl.andrewl.email_indexer.gen.EmailIndexGenerator;
import nl.andrewl.email_indexer.util.Status;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class RegenerateIndexesAction extends AbstractAction {
    private final EmailDatasetBrowser browser;

    public RegenerateIndexesAction(EmailDatasetBrowser browser) {
        super("Regenerate Indexes");
        this.browser = browser;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (browser.getCurrentDataset() != null) {
            var ds = browser.getCurrentDataset();
            ProgressDialog progress = ProgressDialog.minimalText(browser, "Regenerating Indexes");
            progress.append("Starting the process of regenerating indexes.");
            ForkJoinPool.commonPool().submit(() -> {
                try {
                    new EmailIndexGenerator(new Status().withMessageConsumer(progress)).generateIndex(ds);
                    progress.append("Indexes have been regenerated successfully.");
                } catch (IOException ex) {
                    progress.append("An error occurred: " + ex.getMessage());
                }
                progress.done();
            });
        }
    }
}
