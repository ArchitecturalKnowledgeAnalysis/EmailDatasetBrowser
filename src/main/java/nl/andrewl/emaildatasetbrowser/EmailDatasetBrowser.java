package nl.andrewl.emaildatasetbrowser;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.emaildatasetbrowser.control.*;
import nl.andrewl.emaildatasetbrowser.control.email.*;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;
import nl.andrewl.emaildatasetbrowser.view.search.SimpleBrowsePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
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
		this.setDataset(null);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setDataset(null);
			}
		});
	}

	public EmailDataset getCurrentDataset() {
		return this.currentDataset;
	}

	/**
	 * Sets the dataset that this browser will render. If the browser already
	 * has a dataset open, it will close that one first.
	 * @param ds The dataset to use.
	 */
	public void setDataset(EmailDataset ds) {
		if (currentDataset != null) {
			closeDataset();
		}
		this.currentDataset = ds;
		browsePanel.setDataset(ds);
		searchPanel.setDataset(ds);
		emailViewPanel.setDataset(ds);
	}

	private JMenuBar buildMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(new DatasetOpenAction(this)));
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
		filterMenu.add(new JMenuItem(new DeleteHiddenAction(emailViewPanel)));

		menuBar.add(filterMenu);

		return menuBar;
	}

	private void closeDataset() {
		if (currentDataset == null) return;
		ProgressDialog dialog = new ProgressDialog(
				this,
				"Closing Dataset",
				"Closing the current dataset.",
				true,
				false,
				false
		);
		dialog.appendF("Closing the currently open dataset at %s", currentDataset.getOpenDir());
		dialog.activate();
		try {
			currentDataset.close();
			dialog.append("Dataset closed successfully.");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(
					emailViewPanel,
					"An error occurred while closing the database:\n" + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
		} finally {
			dialog.done();
			currentDataset = null;
		}
	}

	public static Preferences getPreferences() {
		return Preferences.userRoot().node(PREFERENCES_NODE_NAME);
	}
}
