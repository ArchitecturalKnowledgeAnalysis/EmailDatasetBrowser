package nl.andrewl.emaildatasetbrowser.view;

import nl.andrewl.email_indexer.data.EmailDataset;

public interface DatasetChangeListener {
	void datasetChanged(EmailDataset ds);
}
