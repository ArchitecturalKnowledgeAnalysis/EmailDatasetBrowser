package nl.andrewl.emaildatasetbrowser.control;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.Path;

public class PathSelectField extends JPanel {
	private final JTextField pathField;
	private final JButton selectPathButton;
	private final MouseListener pathFieldMouseListener;

	private final int fileSelectionMode;
	private final boolean acceptAll;
	private final FileFilter fileFilter;
	private Path selectedPath = null;

	public PathSelectField(int fileSelectionMode, boolean acceptAll, FileFilter filter) {
		super(new BorderLayout());
		this.fileSelectionMode = fileSelectionMode;
		this.acceptAll = acceptAll;
		this.fileFilter = filter;

		pathField = new JTextField(0);
		pathField.setMinimumSize(new Dimension(100, 30));
		pathField.setEditable(false);
		add(pathField, BorderLayout.CENTER);

		selectPathButton = new JButton("Select file...");
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

	public static PathSelectField directorySelectField() {
		return new PathSelectField(JFileChooser.DIRECTORIES_ONLY, false, new DirectoryFileFilter());
	}

	public static PathSelectField fileTypeSelectField(String extension, String name) {
		return new PathSelectField(JFileChooser.FILES_ONLY, false, new FileNameExtensionFilter(name, extension));
	}

	public void setEnabled(boolean enabled) {
		selectPathButton.setEnabled(enabled);
		if (enabled) {
			pathField.addMouseListener(pathFieldMouseListener);
		} else {
			pathField.removeMouseListener(pathFieldMouseListener);
		}
	}

	private void selectFile() {
		JFileChooser fc = new JFileChooser(selectedPath == null ? Path.of(".").toFile() : selectedPath.toFile());
		fc.setFileSelectionMode(fileSelectionMode);
		fc.setFileFilter(fileFilter);
		fc.setAcceptAllFileFilterUsed(acceptAll);
		int result = fc.showDialog(this, "Select");
		if (result == JFileChooser.APPROVE_OPTION) {
			setSelectPath(fc.getSelectedFile().toPath());
		}
	}

	public void setSelectPath(Path p) {
		selectedPath = p;
		String text = p == null ? null : p.toAbsolutePath().toString();
		pathField.setText(text);
	}

	public Path getSelectedPath() {
		return selectedPath;
	}
}
