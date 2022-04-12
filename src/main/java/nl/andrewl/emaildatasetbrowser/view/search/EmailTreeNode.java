package nl.andrewl.emaildatasetbrowser.view.search;

import nl.andrewl.email_indexer.data.EmailEntryPreview;

import javax.swing.tree.DefaultMutableTreeNode;

public class EmailTreeNode extends DefaultMutableTreeNode {
    private final EmailEntryPreview email;

    public EmailTreeNode(EmailEntryPreview email) {
        this.email = email;
        for (var reply : email.replies()) {
            add(new EmailTreeNode(reply));
        }
    }

    public EmailEntryPreview getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return email.subject();
    }
}
