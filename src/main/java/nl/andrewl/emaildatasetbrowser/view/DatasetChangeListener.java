package nl.andrewl.emaildatasetbrowser.view;

import nl.andrewl.email_indexer.data.EmailDataset;

public interface DatasetChangeListener {
	/**
	 * Called when the browser's dataset changes, either completely, or so
	 * significantly that listeners are encouraged to completely reload their
	 * content.
	 * @param ds The dataset.
	 */
	void datasetChanged(EmailDataset ds);

	/**
	 * Called when the current dataset's tags have been updated.
	 * @param ds The dataset.
	 */
	default void tagsChanged(EmailDataset ds) {}
}
