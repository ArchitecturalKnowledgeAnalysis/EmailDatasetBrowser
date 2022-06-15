package nl.andrewl.emaildatasetbrowser;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.emaildatasetbrowser.control.*;
import nl.andrewl.emaildatasetbrowser.control.email.*;
import nl.andrewl.emaildatasetbrowser.control.tag.ManageTagsAction;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;
import nl.andrewl.emaildatasetbrowser.view.search.SimpleBrowsePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.Preferences;

/**
 * The main JFrame for the dataset browser application.
 */
public class EmailDatasetBrowser extends JFrame {
	public static final String PREFERENCES_NODE_NAME = "email_dataset_browser_prefs";

	private final EmailViewPanel emailViewPanel;
	private final SimpleBrowsePanel browsePanel;
	private final LuceneSearchPanel searchPanel;
	private EmailDataset currentDataset = null;

	public EmailDatasetBrowser () {
		super("Email Dataset Browser");
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.emailViewPanel = new EmailViewPanel();
		this.browsePanel = new SimpleBrowsePanel(emailViewPanel);
		this.searchPanel = new LuceneSearchPanel(emailViewPanel);

		JTabbedPane searchPane = new JTabbedPane();
		searchPane.add("Browse", browsePanel);
		searchPane.add("Lucene Search", searchPanel);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(searchPane);
		splitPane.add(emailViewPanel);
		setContentPane(splitPane);

		this.setJMenuBar(buildMenu());
		this.setPreferredSize(new Dimension(1000, 600));
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDataset(null).join();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeDataset().thenRun(() -> dispose());
			}
		});
		SwingUtilities.invokeLater(() -> new DatasetOpenAction(this).tryOpenLastDataset());
	}

	public EmailDataset getCurrentDataset() {
		return this.currentDataset;
	}

	/**
	 * Sets the dataset that this browser will render. If the browser already
	 * has a dataset open, it will close that one first.
	 * @param ds The dataset to use.
	 */
	public CompletableFuture<Void> setDataset(EmailDataset ds) {
		try {
			if (ds != null && ds.getVersion() < 2) {
				return CompletableFuture.failedFuture(new IllegalArgumentException("Cannot load old dataset versions."));
			}
		} catch (IOException e) {
			return CompletableFuture.failedFuture(e);
		}
		CompletableFuture<Void> cf = CompletableFuture.completedFuture(null);
		if (currentDataset != null) {
			cf = cf.thenCompose(unused -> closeDataset());
		}
		return cf.thenAccept(unused -> {
			this.currentDataset = ds;
			browsePanel.setDataset(ds);
			searchPanel.setDataset(ds);
			emailViewPanel.setDataset(ds);
		});
	}

	public EmailViewPanel getEmailViewPanel() {
		return emailViewPanel;
	}

	public SimpleBrowsePanel getBrowsePanel() {
		return browsePanel;
	}

	public LuceneSearchPanel getSearchPanel() {
		return searchPanel;
	}

	private JMenuBar buildMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(new DatasetOpenAction(this)));
		fileMenu.add(new JMenuItem(new UpgradeDatasetAction(this)));
		fileMenu.add(new JMenuItem(new GenerateDatasetAction(this)));
		fileMenu.add(new JMenuItem(new RegenerateIndexesAction(this)));
		fileMenu.add(new JMenuItem(new ExportDatasetAction(this)));
		fileMenu.add(new JMenuItem(new CloseDatasetAction(this)));
		menuBar.add(fileMenu);

		JMenu filterMenu = new JMenu("Filter");
		filterMenu.add(new JMenuItem(new HideAction(emailViewPanel)));
		filterMenu.add(new JMenuItem(new ShowAction(emailViewPanel)));
		filterMenu.add(new JMenuItem(new HideAllByAuthorAction(emailViewPanel)));
		filterMenu.add(new JMenuItem(new HideAllByBodyAction(emailViewPanel)));
		filterMenu.add(new JMenuItem(new HideBySqlAction(this)));
		filterMenu.add(new JMenuItem(new DeleteHiddenAction(emailViewPanel)));
		menuBar.add(filterMenu);

		JMenu viewMenu = new JMenu("View");
		viewMenu.add(new JMenuItem(new ViewSelectionAction(this)));
		menuBar.add(viewMenu);

		JMenu tagMenu = new JMenu("Tag");
		tagMenu.add(new JMenuItem(new ManageTagsAction(this)));
		menuBar.add(tagMenu);

		return menuBar;
	}

	private CompletableFuture<Void> closeDataset() {
		if (currentDataset == null) return CompletableFuture.completedFuture(null);
		ProgressDialog dialog = new ProgressDialog(
				this,
				"Closing Dataset",
				"Closing the current dataset.",
				true,
				false,
				false,
				true
		);
		dialog.start();
		dialog.appendF("Closing the currently open dataset at %s", currentDataset.getOpenDir());
		return currentDataset.close().handle((unused, throwable) -> {
			if (throwable != null) {
				throwable.printStackTrace();
				JOptionPane.showMessageDialog(
						emailViewPanel,
						"An error occurred while closing the database:\n" + throwable.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE
				);
			} else {
				dialog.append("Dataset closed successfully.");
			}
			dialog.done();
			currentDataset = null;
			return null;
		});
	}

	public static Preferences getPreferences() {
		return Preferences.userRoot().node(PREFERENCES_NODE_NAME);
	}
}
