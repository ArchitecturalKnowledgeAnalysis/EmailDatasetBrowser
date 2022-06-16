package nl.andrewl.emaildatasetbrowser.control.email;

import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.EmailSelectionViewer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An action that shows a viewer for a pre-configured selection of emails.
 */
public class ViewSelectionAction extends AbstractAction {
	private final EmailDatasetBrowser browser;

	public ViewSelectionAction(EmailDatasetBrowser browser) {
		super("View Selection");
		this.browser = browser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (browser.getCurrentDataset() == null) return;
		String ids = JOptionPane.showInputDialog(
				browser,
				"Enter a comma-separated list of email ids to view.",
				"View Selection",
				JOptionPane.PLAIN_MESSAGE
		);
		if (ids != null && !ids.isBlank()) {
			Pattern pattern = Pattern.compile("(\\d+)(?>\\s*,\\s*)?");
			Matcher matcher = pattern.matcher(ids);
			List<Long> emailIds = new ArrayList<>();
			while (matcher.find()) {
				emailIds.add(Long.parseLong(matcher.group(1)));
			}
			if (emailIds.isEmpty()) {
				JOptionPane.showMessageDialog(
						browser,
						"Invalid list of ids. Should contain at least one id."
				);
			} else {
				var viewer = new EmailSelectionViewer(emailIds, browser);
				viewer.setVisible(true);
			}
		}
	}
}
