package nl.andrewl.emaildatasetbrowser.view.email;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailEntry;
import nl.andrewl.email_indexer.data.EmailRepository;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.DatasetChangeListener;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A panel that displays all information about an email. This is the main user
 * interface for interacting with a specific email.
 */
public class EmailViewPanel extends JPanel implements DatasetChangeListener {
	private EmailDataset currentDataset = null;
	private EmailEntry email;
	private final EmailDatasetBrowser browser;
	private final EmailInfoPanel infoPanel;
	private final Set<EmailViewListener> listeners = new HashSet<>();

	public EmailViewPanel(EmailDatasetBrowser browser) {
		super(new BorderLayout());
		this.browser = browser;
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		EmailBodyPanel bodyPanel = new EmailBodyPanel();
		splitPane.add(bodyPanel);
		addListener(bodyPanel);

		infoPanel = new EmailInfoPanel(this);
		infoPanel.setPreferredSize(new Dimension(400, -1));
		splitPane.add(infoPanel);
		addListener(infoPanel);
		splitPane.setOneTouchExpandable(true);
		this.add(splitPane, BorderLayout.CENTER);

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

	public EmailDatasetBrowser getBrowser() {
		return browser;
	}

	public EmailEntry getEmail() {
		return email;
	}

	public void setEmail(EmailEntry email) {
		this.email = email;
		listeners.forEach(l -> SwingUtilities.invokeLater(() -> l.emailUpdated(email)));
	}

	public void fetchAndSetEmail(long id) {
		if (currentDataset != null) {
			new EmailRepository(currentDataset).findEmailById(id)
					.ifPresentOrElse(this::setEmail, () -> setEmail(null));
		} else {
			setEmail(null);
		}
	}

	public void refresh() {
		if (this.email != null) fetchAndSetEmail(email.id());
	}

	public EmailInfoPanel getInfoPanel() {
		return infoPanel;
	}

	@Override
	public void datasetChanged(EmailDataset ds) {
		setDataset(ds);
	}
}
