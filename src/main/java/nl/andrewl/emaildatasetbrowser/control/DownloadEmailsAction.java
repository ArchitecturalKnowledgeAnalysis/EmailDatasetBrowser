package nl.andrewl.emaildatasetbrowser.control;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import nl.andrewl.emaildatasetbrowser.view.LabelledField;
import nl.andrewl.emaildatasetbrowser.view.PathSelectField;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildownloader.ApacheMailingListFetcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DownloadEmailsAction extends AbstractAction {
	private final Window owner;

	public DownloadEmailsAction(Window owner) {
		super("Download Emails");
		this.owner = owner;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = new JDialog(owner, "Download ", Dialog.ModalityType.APPLICATION_MODAL);
		JPanel p = new JPanel(new BorderLayout());

		JTextField domainField = new JTextField(0);
		JTextField listField = new JTextField(0);
		var timeSettings = new TimePickerSettings();
		timeSettings.setAllowEmptyTimes(false);
		timeSettings.setInitialTimeToNow();
		var dateSettings = new DatePickerSettings();
		dateSettings.setAllowEmptyDates(false);
		DateTimePicker startPicker = new DateTimePicker(dateSettings.copySettings(), timeSettings);
		startPicker.setDateTimeStrict(LocalDateTime.now().minusYears(10));
		DateTimePicker endPicker = new DateTimePicker(dateSettings.copySettings(), timeSettings);
		endPicker.setDateTimeStrict(LocalDateTime.now());
		PathSelectField dirField = PathSelectField.directorySelectField();

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
		inputPanel.add(new LabelledField("Domain", domainField));
		inputPanel.add(new LabelledField("List", listField));
		inputPanel.add(new LabelledField("Start", startPicker));
		inputPanel.add(new LabelledField("End", endPicker));
		inputPanel.add(dirField);

		p.add(inputPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(event -> dialog.dispose());
		JButton downloadButton = new JButton("Download");
		downloadButton.addActionListener(event -> {
			ApacheMailingListFetcher fetcher = new ApacheMailingListFetcher(
					12,
					1000,
					"https://lists.apache.org/api/mbox.lua"
			);
			Path outputDir = dirField.getSelectedPath();
			dialog.dispose();
			ProgressDialog progressDialog = new ProgressDialog(owner, "Downloading...", "Downloading emails");
			progressDialog.start();
			fetcher.download(
					outputDir,
					domainField.getText(),
					listField.getText(),
					startPicker.getDateTimeStrict().atZone(ZoneId.systemDefault()),
					endPicker.getDateTimeStrict().atZone(ZoneId.systemDefault()),
					progressDialog
			).handle((paths, throwable) -> {
				progressDialog.done();
				if (throwable == null) {
					JOptionPane.showMessageDialog(
							progressDialog.getDialog(),
							"MBox files downloaded successfully.",
							"Done",
							JOptionPane.INFORMATION_MESSAGE
					);
				} else {
					throwable.printStackTrace();
				}
				return null;
			});
		});
		buttonPanel.add(downloadButton);
		buttonPanel.add(cancelButton);
		p.add(buttonPanel, BorderLayout.SOUTH);

		dialog.setContentPane(p);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}
}
