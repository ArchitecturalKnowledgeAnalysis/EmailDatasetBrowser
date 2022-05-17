package nl.andrewl.emaildatasetbrowser.control;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.upgrade.Version1Upgrader;
import nl.andrewl.email_indexer.util.Async;
import nl.andrewl.email_indexer.util.Status;
import nl.andrewl.emaildatasetbrowser.EmailDatasetBrowser;
import nl.andrewl.emaildatasetbrowser.view.LabelledField;
import nl.andrewl.emaildatasetbrowser.view.PathSelectField;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * An action that shows a popup to allow users to upgrade a dataset from version 1 to the latest.
 */
public class UpgradeDatasetAction extends AbstractAction {
	private final EmailDatasetBrowser browser;

	public UpgradeDatasetAction(EmailDatasetBrowser browser) {
		super("Upgrade Dataset");
		this.browser = browser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog upgradeDialog = new JDialog(browser, "Upgrade Dataset", true);
		PathSelectField ds1Select = PathSelectField.directorySelectField();
		PathSelectField ds2Select = PathSelectField.directorySelectField();

		JPanel mainPanel = new JPanel(new BorderLayout());

		JPanel bodyPanel = new JPanel();
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.PAGE_AXIS));
		bodyPanel.add(new LabelledField("Old Dataset Directory", ds1Select));
		bodyPanel.add(new LabelledField("New Dataset Directory", ds2Select));
		mainPanel.add(bodyPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e1 -> upgradeDialog.dispose());
		buttonPanel.add(cancelButton);
		JButton okayButton = new JButton("Upgrade");
		okayButton.addActionListener(e1 -> {
			var ds1 = EmailDataset.open(ds1Select.getSelectedPath()).join();
			try {
				if (ds1.getVersion() != 1) {
					JOptionPane.showMessageDialog(upgradeDialog, "Can only upgrade from version 1 datasets.", "Invalid Version", JOptionPane.WARNING_MESSAGE);
					return;
				}
				ProgressDialog progress = ProgressDialog.minimalText(upgradeDialog, "Upgrading...");
				Async.run(() -> {
					new Version1Upgrader().upgrade(
							ds1Select.getSelectedPath(),
							ds2Select.getSelectedPath(),
							new Status().withMessageConsumer(progress)
					);
					progress.done();
					upgradeDialog.dispose();
				});
			} catch (Exception e2) {
				e2.printStackTrace();
				upgradeDialog.dispose();
			}
		});
		buttonPanel.add(okayButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		upgradeDialog.setContentPane(mainPanel);
		upgradeDialog.setPreferredSize(new Dimension(500, 300));
		upgradeDialog.pack();
		upgradeDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		upgradeDialog.setLocationRelativeTo(browser);
		upgradeDialog.setVisible(true);
	}
}
