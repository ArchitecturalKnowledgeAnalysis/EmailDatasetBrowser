package nl.andrewl.emaildatasetbrowser.view.search.export;

import java.util.HashMap;
import java.awt.*;
import java.nio.file.Path;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.export.ExporterParameters;
import nl.andrewl.email_indexer.data.export.datasample.datatype.TypeExporter;
import nl.andrewl.email_indexer.data.export.datasample.sampletype.SampleExporter;
import nl.andrewl.emaildatasetbrowser.control.DirectoryFileFilter;
import nl.andrewl.emaildatasetbrowser.view.PathSelectField;
import nl.andrewl.emaildatasetbrowser.view.ProgressDialog;
import nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets.CsvExportType;
import nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets.PdfExportType;
import nl.andrewl.emaildatasetbrowser.view.search.export.exporttargets.TxtExportType;

/**
 * Dialog with a number of fields relevant to exporting emails.
 * Does not manage the type of data exported, but does manage the output format.
 */
public class ExportPanel extends JDialog {

    private final ExportSample exporter;
    private final EmailDataset dataset;
    private final HashMap<String, ExportType> exportTargets = new HashMap<>();
    private ExportType currentTarget;

    private final JPanel exportPanel = new JPanel();
    private final JPanel fileSelectPanel = new JPanel();
    private final JComboBox<String> exportTypes = new JComboBox<>();;
    private final JSpinner maxResultsSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
    private final PathSelectField dirSelectField = new PathSelectField(JFileChooser.DIRECTORIES_ONLY, false, false,
            new DirectoryFileFilter(), "Select Directory", "Select Export Directory", "Select");;
    private final PathSelectField fileSelectField = new PathSelectField(JFileChooser.FILES_ONLY, true, false,
            new FileNameExtensionFilter("No Filter", ".*"), "Select File", "Select Export File", "Select");
    private final JCheckBox separateThreadsToggle = new JCheckBox("Separate mailing threads");

    /**
     * @param owner          Window that owns this object.
     * @param dataset        the dataset used to export.
     * @param sampleExporter concrete exporter that is used.
     */
    public ExportPanel(Window owner, EmailDataset dataset, ExportSample sampleExporter) {
        super(owner, "Export Emails", ModalityType.APPLICATION_MODAL);
        this.exporter = sampleExporter;
        this.dataset = dataset;

        exportPanel.setLayout(new GridLayout(6, 1));
        exportPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Adds all export types.
        addExportTarget(new PdfExportType());
        addExportTarget(new TxtExportType());
        addExportTarget(new CsvExportType());
        exportTypes.addActionListener(e -> updateCurrentTarget());
        exportPanel.add(exportTypes);

        // Adds Contents
        exportPanel.add(new JLabel("Max. result count:"));
        exportPanel.add(maxResultsSpinner);
        separateThreadsToggle.addActionListener(e -> updateSeparateThreadsToggle());
        exportPanel.add(separateThreadsToggle);
        fileSelectPanel.setLayout(new BoxLayout(fileSelectPanel, BoxLayout.X_AXIS));
        fileSelectPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        fileSelectPanel.add(dirSelectField);
        fileSelectPanel.add(fileSelectField);
        exportPanel.add(fileSelectPanel);

        // Adds buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> onExportClicked());
        buttonPanel.add(exportButton);
        exportPanel.add(buttonPanel);

        // Final setup
        setContentPane(exportPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBounds(0, 0, 400, 225);
        setLocationRelativeTo(owner);
        updateCurrentTarget();
        updateSeparateThreadsToggle();
    }

    private void addExportTarget(ExportType target) {
        exportTypes.addItem(target.getName());
        exportTargets.put(target.getName(), target);
    }

    private void updateCurrentTarget() {
        this.currentTarget = exportTargets.get((String) exportTypes.getSelectedItem());
        this.fileSelectField.setFileFilter(this.currentTarget.getFileNameExtentionFilter());
    }

    private void updateSeparateThreadsToggle() {
        dirSelectField.setVisible(this.separateThreadsToggle.isSelected());
        fileSelectField.setVisible(!this.separateThreadsToggle.isSelected());
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
        ExporterParameters params = new ExporterParameters()
                .withMaxResultCount((int) this.maxResultsSpinner.getValue())
                .withSeparateMailingThreads(this.separateThreadsToggle.isSelected());
        params = this.exporter.specifyParameters(params);

        TypeExporter typeExporter = this.currentTarget.buildTypeExporter();
        SampleExporter sampleExporter = this.exporter.buildSampleExporter(typeExporter, params);

        Path outputPath = params.mailingThreadsAreSeparate()
                ? this.dirSelectField.getSelectedPath()
                : this.fileSelectField.getSelectedPath();

        // Performs export and completes dialog.
        sampleExporter.export(this.dataset, outputPath)
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
