package nl.andrewl.emaildatasetbrowser.view.email;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A panel that displays all information about an email. This is the main user
 * interface for interacting with a specific email.
 */
public class EmailViewPanel extends JPanel {
	private EmailDataset currentDataset = null;
	private EmailEntry email;
	private final Set<EmailViewListener> listeners = new HashSet<>();

	public EmailViewPanel() {
		this.setLayout(new BorderLayout());
		EmailBodyPanel bodyPanel = new EmailBodyPanel();
		this.add(bodyPanel, BorderLayout.CENTER);
		addListener(bodyPanel);

		EmailInfoPanel infoPanel = new EmailInfoPanel(this);
		infoPanel.setPreferredSize(new Dimension(400, -1));
		this.add(infoPanel, BorderLayout.EAST);
		addListener(infoPanel);

		setEmail(null);
	}

	public void addListener(EmailViewListener listener) {
		this.listeners.add(listener);
	}

	public void setDataset(EmailDataset dataset) {
		this.currentDataset = dataset;
		setEmail(null);
	}

	public EmailDataset getCurrentDataset() {
		return this.currentDataset;
	}

	public EmailEntry getEmail() {
		return email;
	}

	public void setEmail(EmailEntry email) {
		this.email = email;
		listeners.forEach(l -> {
			SwingUtilities.invokeLater(() -> l.emailUpdated(email));
		});
	}

	public void fetchAndSetEmail(String messageId) {
		if (this.currentDataset != null) {
			new EmailRepository(currentDataset).findEmailById(messageId)
					.ifPresentOrElse(this::setEmail, () -> setEmail(null));
		} else {
			setEmail(null);
		}
	}

	public void refresh() {
		if (this.email != null) fetchAndSetEmail(email.messageId());
	}
}