package nl.andrewl.emaildatasetbrowser.view;

import nl.andrewl.emaildatasetbrowser.control.DirectoryFileFilter;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A component for selecting a {@link Path} in the user's file system.
 */
public class PathSelectField extends JPanel {
	private final JTextField pathField;
	private final JButton selectPathButton;
	private final MouseListener pathFieldMouseListener;

	private final int fileSelectionMode;
	private final boolean acceptAll;
	private final boolean allowMultiple;
	private FileFilter fileFilter;
	private final String title;
	private final String buttonText;

	private final List<Path> selectedPaths = new ArrayList<>();

	/**
	 * Constructs the field.
	 * @param fileSelectionMode The file selection mode, from {@link JFileChooser}'s constants.
	 * @param acceptAll Whether to allow the "Accept All" file filter.
	 * @param allowMultiple Whether to allow the user to select multiple paths.
	 * @param filter The file filter to use.
	 * @param label The label that's placed next to the input field.
	 * @param title The title for the file chooser that will be shown.
	 * @param buttonText The text for the file chooser's "select" button.
	 */
	public PathSelectField(
			int fileSelectionMode,
			boolean acceptAll,
			boolean allowMultiple,
			FileFilter filter,
			String label,
			String title,
			String buttonText
	) {
		super(new BorderLayout());
		this.fileSelectionMode = fileSelectionMode;
		this.acceptAll = acceptAll;
		this.allowMultiple = allowMultiple;
		this.fileFilter = filter;
		this.title = title;
		this.buttonText = buttonText;

		pathField = new JTextField(0);
		pathField.setMinimumSize(new Dimension(100, 30));
		pathField.setEditable(false);
		add(pathField, BorderLayout.CENTER);

		selectPathButton = new JButton(label);
		selectPathButton.setMinimumSize(new Dimension(50, 30));
		add(selectPathButton, BorderLayout.EAST);

		selectPathButton.addActionListener(e -> selectFile());
		pathFieldMouseListener = new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == 1) {
					selectFile();
				}
			}
		};
		pathField.addMouseListener(pathFieldMouseListener);
	}

	/**
	 * Convenience method for building a field for selecting a single directory.
	 * @return The path select field.
	 */
	public static PathSelectField directorySelectField() {
		return new PathSelectField(
				JFileChooser.DIRECTORIES_ONLY,
				false,
				false,
				new DirectoryFileFilter(),
				"Select directory...",
				"Select Directory",
				"Select"
		);
	}

	/**
	 * Convenience method for building a field for selecting a single file of
	 * a specified type.
	 * @param extension The file extension.
	 * @param name The name of the file type, to use as the filter name.
	 * @return The path select field.
	 */
	public static PathSelectField fileTypeSelectField(String extension, String name) {
		return new PathSelectField(
				JFileChooser.FILES_ONLY,
				false,
				false,
				new FileNameExtensionFilter(name, extension),
				"Select file...",
				"Select File",
				"Select"
		);
	}

	/**
	 * Sets this field as enabled or disabled.
	 * @param enabled True if this component should be enabled, false otherwise
	 */
	public void setEnabled(boolean enabled) {
		selectPathButton.setEnabled(enabled);
		if (enabled) {
			pathField.addMouseListener(pathFieldMouseListener);
		} else {
			pathField.removeMouseListener(pathFieldMouseListener);
		}
	}

	private void selectFile() {
		JFileChooser fc = new JFileChooser();
		if (!selectedPaths.isEmpty()) {
			if (allowMultiple) {
				File[] files = new File[selectedPaths.size()];
				for (int i = 0; i < selectedPaths.size(); i++) {
					files[i] = selectedPaths.get(i).toFile();
				}
				fc.setSelectedFiles(files);
			} else {
				fc.setSelectedFile(selectedPaths.get(0).toFile());
			}
		} else {
			fc.setCurrentDirectory(Path.of(".").toFile());
		}

		fc.setFileSelectionMode(fileSelectionMode);
		fc.setFileFilter(fileFilter);
		fc.setAcceptAllFileFilterUsed(acceptAll);
		fc.setMultiSelectionEnabled(allowMultiple);
		fc.setDialogTitle(title);
		int result = fc.showDialog(this, buttonText);
		if (result == JFileChooser.APPROVE_OPTION) {
			setSelectedPaths(Arrays.stream(fc.getSelectedFiles()).map(File::toPath).toList());
			setSelectPath(fc.getSelectedFile().toPath());
		}
	}

	/**
	 * Sets the list of selected paths.
	 * @param paths The paths to select.
	 */
	public void setSelectedPaths(List<Path> paths) {
		selectedPaths.clear();
		selectedPaths.addAll(paths);
		String fieldText = paths.stream()
				.map(Path::toAbsolutePath)
				.map(Path::toString)
				.collect(Collectors.joining("; "));
		pathField.setText(fieldText);
	}

	/**
	 * Sets a single path as selected.
	 * @param p The path to select.
	 */
	public void setSelectPath(Path p) {
		setSelectedPaths(List.of(p));
	}

	/**
	 * Gets a list of all selected paths.
	 * @return The list of selected paths.
	 */
	public List<Path> getSelectedPaths() {
		return new ArrayList<>(selectedPaths);
	}

	/**
	 * Gets the first selected path, or null if none is selected. Note that
	 * if you set the field to allow multiple selected paths, only the first
	 * is returned by this method. Use {@link PathSelectField#getSelectedPaths()}
	 * in that case.
	 * @return The selected path.
	 */
	public Path getSelectedPath() {
		if (selectedPaths.isEmpty()) return null;
		return selectedPaths.get(0);
	}

	public void setFileFilter(FileFilter filter) {
		this.fileFilter = filter;
	}
}
