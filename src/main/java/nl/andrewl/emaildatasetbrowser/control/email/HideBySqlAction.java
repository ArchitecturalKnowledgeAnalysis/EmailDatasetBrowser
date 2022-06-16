package nl.andrewl.emaildatasetbrowser.control.email;

import nl.andrewl.email_indexer.util.DbUtils;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HideBySqlAction extends AbstractAction {
	private final EmailDatasetBrowser browser;

	public HideBySqlAction(EmailDatasetBrowser browser) {
		super("Hide by SQL");
		this.browser = browser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (browser.getCurrentDataset() == null) return;
		String clause = JOptionPane.showInputDialog(
				browser,
				"Please specify an SQL WHERE clause to select emails to hide.\n" +
						"Will be injected into the ... of \"UPDATE EMAIL SET HIDDEN = TRUE WHERE ...\"",
				"Hide by SQL",
				JOptionPane.PLAIN_MESSAGE
		);
		if (clause == null || clause.isBlank()) {
			JOptionPane.showMessageDialog(browser, "SQL clause cannot be empty.");
		} else {
			long count = DbUtils.count(browser.getCurrentDataset().getConnection(), "SELECT COUNT(ID) FROM EMAIL WHERE HIDDEN = FALSE AND " + clause);
			String query = "UPDATE EMAIL SET HIDDEN = TRUE WHERE HIDDEN = FALSE AND " + clause;
			if (count > 0 && SwingUtils.confirm(browser, "Are you sure you want to execute the following query:\n" + query + "\n" + count + " emails will be hidden.")) {
				DbUtils.doTransaction(browser.getCurrentDataset().getConnection(), c -> {
					long mutId = DbUtils.insertWithId(c, "INSERT INTO MUTATION (DESCRIPTION) VALUES (?)", "Hiding all by SQL clause: " + clause);
					int hiddenCount = DbUtils.update(c, query);
					DbUtils.update(c, "UPDATE MUTATION SET AFFECTED_EMAIL_COUNT = ? WHERE ID = ?", hiddenCount, mutId);
					JOptionPane.showMessageDialog(
							browser,
							hiddenCount + " emails were hidden by this update.",
							"Emails Hidden",
							JOptionPane.INFORMATION_MESSAGE
					);
					browser.notifyListeners();
				});
			} else if (count < 1) {
				JOptionPane.showMessageDialog(
						browser,
						"No emails were found using that SQL clause.",
						"No Emails Found",
						JOptionPane.WARNING_MESSAGE
				);
			}
		}
	}
}
