# EmailDatasetBrowser
Application for interacting with datasets produced by the [Email Indexer](https://github.com/ArchitecturalKnowledgeAnalysis/EmailIndexer).

You can run this program as an executable JAR file using [Java 17 or later](https://adoptium.net/temurin/releases). To get it, go to the [releases page](https://github.com/ArchitecturalKnowledgeAnalysis/EmailDatasetBrowser/releases) and download the latest `EmailDatasetBrowser-X.X.X.jar`.
> If you're on Linux, you may need to mark the JAR file as executable before you can run it. Use `chmod +x <jarfile>` to do so, or use your desktop's default file property editor.

## How do I use this app?

Start by running the program by double-clicking on its JAR file, or by executing `java -jar emaildatasetbrowser-X.X.X.jar` (insert latest version number instead of "X.X.X"). The browser app will open to an empty view, because no dataset has yet been opened.

### Opening Datasets

There are two ways to open datasets with this application:
1. Via the command-line, you can provide the path to the dataset ZIP file or directory. For example: `java -jar emaildatasetbrowser-1.2.3.jar /home/andrew/docs/my-dataset`
2. Navigate to **File** > **Open Dataset**, and select a dataset directory or ZIP file to open.

Note that ZIP-compressed datasets are somewhat slow to open, since they must first be decompressed and their contents extracted. Thus, when working with a dataset, open it from its ZIP file first, then use the directory. **All changes made to an open dataset are saved in its directory, NOT the ZIP file.** If you want to re-package your dataset into ZIP format, export it as a `.zip` file.

### Generating Datasets

This application provides an interface to the functionality of the [Email Indexer](https://github.com/ArchitecturalKnowledgeAnalysis/EmailIndexer) utility for creating new datasets by parsing emails from `.mbox` files.

To get started, navigate to **File** > **Generate Dataset**. Here, you'll see a popup that shows a list of directories to read mbox files from. By default, it's empty. Add some directories to this list with the **Add Mbox Directory** button. If you need to download mbox files, you can click the **Download Emails** button.
> Note: **Download Emails** currently only supports downloading from Apache mailing lists.

Finally, click **Select directory...** to select the directory to build the dataset in, and click **Generate** to begin crunching the data.
> For more information about the structure of datasets and how they're generated, please refer to the [Email Indexer](https://github.com/ArchitecturalKnowledgeAnalysis/EmailIndexer) repository.
