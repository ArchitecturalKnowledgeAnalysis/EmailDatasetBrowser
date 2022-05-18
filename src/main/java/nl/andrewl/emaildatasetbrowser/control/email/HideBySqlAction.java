package nl.andrewl.emaildatasetbrowser.control.email;

import nl.andrewl.email_indexer.util.DbUtils;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

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
			String query = "UPDATE EMAIL SET HIDDEN = TRUE WHERE " + clause;
			if (SwingUtils.confirm(browser, "Are you sure you want to execute the following query:\n" + query + "\n" + count + " emails will be hidden.")) {
				DbUtils.doTransaction(browser.getCurrentDataset().getConnection(), c -> {
					try (
						var stmt = c.prepareStatement(query);
						var mutStmt = c.prepareStatement(
							"INSERT INTO MUTATION (DESCRIPTION, AFFECTED_EMAIL_COUNT) VALUES (?, ?)")
					) {
						int hiddenCount = stmt.executeUpdate();
						mutStmt.setString(1, "Hiding by SQL clause.");
						mutStmt.setLong(2, hiddenCount);
						mutStmt.executeUpdate();
						JOptionPane.showMessageDialog(
								browser,
								hiddenCount + " emails were hidden by this update.",
								"Emails Hidden",
								JOptionPane.INFORMATION_MESSAGE
						);
						browser.getEmailViewPanel().refresh();
					} catch (SQLException e1) {
						e1.printStackTrace();
						c.rollback();
						JOptionPane.showMessageDialog(
								browser,
								"An SQL error occurred: " + e1.getMessage(),
								"SQL Error",
								JOptionPane.ERROR_MESSAGE
						);
					}
				});
			}
		}
	}
}
