package nl.andrewl.emaildatasetbrowser.email;

import nl.andrewl.email_indexer.data.EmailEntry;

/**
 * Listener for when the email a user is viewing is updated.
 */
public interface EmailViewListener {
	void emailUpdated(EmailEntry email);
}
