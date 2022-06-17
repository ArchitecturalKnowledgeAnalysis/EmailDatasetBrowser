package nl.andrewl.emaildatasetbrowser;

import com.formdev.flatlaf.FlatDarkLaf;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The application entry point.
 */
public class EdbApp {
	public static void main(String[] args) {
		FlatDarkLaf.setup();
		var browser = new EmailDatasetBrowser();
		if (args.length > 0) {
			Path datasetPath = Path.of(args[0].trim());
			loadDatasetOnOpen(datasetPath, browser);
		}
		browser.setVisible(true);
	}

	public static void loadDatasetOnOpen(Path path, EmailDatasetBrowser browser) {
		System.out.println("Loading dataset from " + path);
		if (Files.notExists(path)) {
			System.err.println(path + " doesn't exist.");
			System.exit(1);
		}
		browser.openDataset(path)
				.exceptionally(throwable -> {
					throwable.printStackTrace();
					System.exit(1);
					return null;
				});
	}
}
