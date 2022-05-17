package nl.andrewl.emaildatasetbrowser.view.tag;

import nl.andrewl.email_indexer.data.EmailDataset;
import nl.andrewl.email_indexer.data.Tag;
import nl.andrewl.email_indexer.data.TagRepository;
import nl.andrewl.emaildatasetbrowser.view.LabelledField;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Optional;

/**
 * A dialog for editing a single tag, or creating a new one.
 */
public class TagEditDialog extends JDialog {

	private final JTextField nameField = new JTextField();
	private final JTextArea descriptionField = new JTextArea();

	private final Tag tag;
	private final EmailDataset ds;

	public TagEditDialog(Window owner, Tag tag, EmailDataset ds) {
		super(owner, "Edit Tag", ModalityType.APPLICATION_MODAL);
		this.ds = ds;
		this.tag = tag;
		if (tag != null) {
			nameField.setText(tag.name());
			descriptionField.setText(tag.description());
		}
		setContentPane(buildUI());
		setPreferredSize(new Dimension(400, 300));
		pack();
		setLocationRelativeTo(owner);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public String getName() {
		return nameField.getText().trim();
	}

	public String getDescription() {
		if (descriptionField.getText() != null && descriptionField.getText().isBlank()) return null;
		return descriptionField.getText().trim();
	}

	private Container buildUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.add(new LabelledField("Name", nameField), BorderLayout.NORTH);
		descriptionField.setLineWrap(true);
		descriptionField.setWrapStyleWord(true);
		mainPanel.add(new LabelledField(
				"Description",
				new JScrollPane(descriptionField, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
		), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> dispose());
		JButton okayButton = new JButton("Okay");
		okayButton.addActionListener(e -> {
			if (!validateTag()) return;
			onSubmit();
			dispose();
		});
		buttonPanel.add(cancelButton);
		buttonPanel.add(okayButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		return mainPanel;
	}

	private boolean validateTag() {
		var repo = new TagRepository(this.ds);
		String name = getName();
		if (name == null || name.isBlank()) {
			JOptionPane.showMessageDialog(this, "Name is required.");
			return false;
		}
		if (name.length() > 255) {
			JOptionPane.showMessageDialog(this, "Name must be less than 256 characters long.");
			return false;
		}
		Optional<Tag> optionalTag = repo.getTagByName(name);
		if (optionalTag.isPresent() && (tag == null || tag.id() != optionalTag.get().id())) {
			JOptionPane.showMessageDialog(this, "This tag name is already taken.");
			return false;
		}
		return true;
	}

	private void onSubmit() {
		var repo = new TagRepository(this.ds);
		if (tag != null) {
			String name = getName();
			if (!name.equals(tag.name())) {
				repo.setName(tag.id(), name);
			}
			String description = getDescription();
			if (!Objects.equals(description, tag.description())) {
				repo.setDescription(tag.id(), description);
			}
		} else {
			repo.createTag(getName(), getDescription());
		}
	}
}
