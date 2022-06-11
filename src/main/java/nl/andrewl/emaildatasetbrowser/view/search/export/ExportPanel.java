package nl.andrewl.emaildatasetbrowser.view.search.export;

import java.util.HashMap;
import java.awt.*;
import java.nio.file.Path;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.export.query.QueryExportParams;
import nl.andrewl.email_indexer.data.export.query.QueryExporter;
import nl.andrewl.emaildatasetbrowser.control.DirectoryFileFilter;
import nl.andrewl.emaildatasetbrowser.view.PathSelectField;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets.CsvExportTarget;
import nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets.PdfExportTarget;
import nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets.TxtExportTarget;

public class ExportPanel extends JDialog {

    private final Exporter exportAction;
    private final EmailDataset dataset;
    private final HashMap<String, ExportTarget> exportTargets = new HashMap<>();
    private ExportTarget currentTarget;

    private final JPanel optionsPanel = new JPanel();
    private final JComboBox<String> exportTypes = new JComboBox<>();;
    private final JSpinner maxResultsSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
    private final PathSelectField dirSelectField = new PathSelectField(JFileChooser.DIRECTORIES_ONLY, false, false,
            new DirectoryFileFilter(), "Select Directory", "Select Export Directory", "Select");;
    private final PathSelectField fileSelectField = new PathSelectField(JFileChooser.FILES_ONLY, true, false,
            new FileNameExtensionFilter("No Filter", ".*"), "Select File", "Select Export File", "Select");
    private final JCheckBox separateThreadsToggle = new JCheckBox("Separate mailing threads");

    public ExportPanel(Window owner, EmailDataset dataset, Exporter exporter) {
        super(owner, "Export Emails", ModalityType.APPLICATION_MODAL);
        this.exportAction = exporter;
        this.dataset = dataset;

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));

        // Adds all export types.
        addExportTarget(new PdfExportTarget());
        addExportTarget(new TxtExportTarget());
        addExportTarget(new CsvExportTarget());
        exportTypes.addActionListener(e -> updateCurrentTarget());
        contentPanel.add(exportTypes);
        updateCurrentTarget();

        // Maximum results counter
        optionsPanel.add(new JLabel("Max. result count:"));
        optionsPanel.add(maxResultsSpinner);

        // Separated mailing threads toggle.
        separateThreadsToggle.addActionListener(e -> {
            dirSelectField.setVisible(separateThreadsToggle.isSelected());
            fileSelectField.setVisible(!separateThreadsToggle.isSelected());
        });
        optionsPanel.add(separateThreadsToggle);
        dirSelectField.setVisible(false);

        optionsPanel.add(dirSelectField);
        optionsPanel.add(fileSelectField);

        contentPanel.add(optionsPanel);

        // Adds buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> onExportClicked());
        buttonPanel.add(exportButton);

        contentPanel.add(buttonPanel);

        setContentPane(contentPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBounds(0, 0, 400, 190);
        setLocationRelativeTo(owner);
    }

    private void addExportTarget(ExportTarget target) {
        exportTypes.addItem(target.getName());
        exportTargets.put(target.getName(), target);
    }

    private void updateCurrentTarget() {
        this.currentTarget = exportTargets.get((String) exportTypes.getSelectedItem());
        this.fileSelectField.setFileFilter(this.currentTarget.getFileNameExtentionFilter());
    }

    private void onExportClicked() {
        // Starts progress dialog.
        ProgressDialog progress = new ProgressDialog(
                SwingUtilities.getWindowAncestor(this),
                "Exporting Results",
                null,
                true,
                false,
                true);
        progress.activate();
        progress.append(String.format("Generating export with target %s ...", this.currentTarget.getName()));

        // Generates export parameters.
        QueryExportParams params = new QueryExportParams()
                .withMaxResultCount((int) this.maxResultsSpinner.getValue())
                .withSeparateEmailThreads(this.separateThreadsToggle.isSelected());
        params = this.exportAction.specifyParameters(params);
        QueryExporter exporter = this.currentTarget.buildExporter(params);

        Path outputPath = params.isSeparateEmailThreads()
                ? this.dirSelectField.getSelectedPath()
                : this.fileSelectField.getSelectedPath();

        // Performs export and completes dialog.
        exporter.export(this.dataset, outputPath)
                .whenComplete((v, throwable) -> {
                    if (throwable != null) {
                        progress.append("Export failed with message:");
                        progress.append(throwable.getMessage());
                    } else {
                        progress.append("Export Completed!");
                    }
                    progress.done();
                });
        dispose();
    }
}
