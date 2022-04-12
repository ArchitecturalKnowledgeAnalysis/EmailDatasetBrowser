package nl.andrewl.emaildatasetbrowser.control;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A file filter that only allows users to select directories.
 */
public class DirectoryFileFilter extends FileFilter {
	@Override
	public boolean accept(File f) {
		return f.isDirectory();
	}

	@Override
	public String getDescription() {
		return "Directories";
	}
}
