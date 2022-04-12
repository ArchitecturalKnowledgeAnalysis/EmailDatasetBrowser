package nl.andrewl.emaildatasetbrowser;

import com.formdev.flatlaf.FlatDarkLaf;
import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.emaildatasetbrowser.control.DatasetOpenAction;
import nl.andrewl.emaildatasetbrowser.control.ExportDatasetAction;
import nl.andrewl.emaildatasetbrowser.control.GenerateDatasetAction;
import nl.andrewl.emaildatasetbrowser.control.email.*;
import nl.andrewl.emaildatasetbrowser.view.email.EmailViewPanel;
import nl.andrewl.emaildatasetbrowser.view.search.LuceneSearchPanel;
import nl.andrewl.emaildatasetbrowser.view.search.SimpleBrowsePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The main JFrame for the dataset browser application.
 */
public class EmailDatasetBrowser extends JFrame {
	private final EmailViewPanel emailViewPanel;
	private final SimpleBrowsePanel browsePanel;
	private final LuceneSearchPanel searchPanel;
	private EmailDataset currentDataset = null;

	public static void main(String[] args) {
		FlatDarkLaf.setup();
		var browser = new EmailDatasetBrowser();
		browser.setVisible(true);
	}

	public EmailDatasetBrowser () {
		super("Email Dataset Browser");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.emailViewPanel = new EmailViewPanel();
		this.browsePanel = new SimpleBrowsePanel(emailViewPanel);
		this.searchPanel = new LuceneSearchPanel(emailViewPanel);

		JPanel container = new JPanel(new BorderLayout());
		container.add(this.emailViewPanel, BorderLayout.CENTER);
		JTabbedPane searchPane = new JTabbedPane();
		searchPane.add("Browse", browsePanel);
		searchPane.add("Lucene Search", searchPanel);
		container.add(searchPane, BorderLayout.WEST);
		this.setContentPane(container);

		this.setJMenuBar(buildMenu());
		this.setPreferredSize(new Dimension(1000, 600));
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDataset(null);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (currentDataset != null) {
					try {
						currentDataset.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}

	public EmailDataset getCurrentDataset() {
		return this.currentDataset;
	}

	public void setDataset(EmailDataset ds) {
		if (currentDataset != null) {
			try {
				currentDataset.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		fileMenu.add(new JMenuItem(new ExportDatasetAction(this)));
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
}
