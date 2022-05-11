package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.EmailEntryPreview;
import nl.andrewl.email_indexer.data.EmailRepository;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A node that's used to display an email in a tree-styled search results JTree.
 */
public class EmailTreeNode extends DefaultMutableTreeNode {
    private Integer rootResultIndex;
    private final EmailEntryPreview email;
    private boolean loadedReplies = false;

    public EmailTreeNode(EmailEntryPreview email, Integer rootResultIndex) {
        this.email = email;
        this.rootResultIndex = rootResultIndex;
    }

    public EmailTreeNode(EmailEntryPreview email) {
        this(email, null);
    }

    public EmailEntryPreview getEmail() {
        return email;
    }

    public void loadReplies(EmailDataset dataset) {
        if (loadedReplies) return;
        for (var reply : new EmailRepository(dataset).findAllReplies(email.id())) {
            add(new EmailTreeNode(reply));
        }
        loadedReplies = true;
    }

    public void unloadReplies() {
        removeAllChildren();
        loadedReplies = false;
    }

    public void setRootResultIndex(Integer rootResultIndex) {
        this.rootResultIndex = rootResultIndex;
    }

    @Override
    public String toString() {
        return (rootResultIndex != null ? rootResultIndex + ". " : "") + email.subject();
    }
}
