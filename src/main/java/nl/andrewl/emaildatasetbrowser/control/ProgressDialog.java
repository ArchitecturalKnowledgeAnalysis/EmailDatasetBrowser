package nl.andrewl.emaildatasetbrowser.control;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.function.Consumer;

/**
 * A dialog that can be used to show the progress of an ongoing task by
 * periodically posting messages to the dialog's log text component.
 */
public class ProgressDialog extends JDialog implements Consumer<String> {
	private final JTextArea textBox = new JTextArea();
	private final JButton doneButton = new JButton("Done");

	public ProgressDialog(Window owner, String title, String description) {
		super(owner, title, ModalityType.APPLICATION_MODAL);

		JPanel p = new JPanel(new BorderLayout());
		if (description != null) {
			p.add(new JLabel(description), BorderLayout.NORTH);
		}
		textBox.setEditable(false);
		textBox.setWrapStyleWord(true);
		textBox.setLineWrap(true);
		textBox.setFont(new Font("monospaced", textBox.getFont().getStyle(), 12));
		DefaultCaret caret = (DefaultCaret) textBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane(textBox, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(500, 400));
		p.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		doneButton.setEnabled(false);
		doneButton.addActionListener(e -> dispose());
		buttonPanel.add(doneButton);
		p.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(p);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setLocationRelativeTo(owner);
	}

	/**
	 * Begins showing the dialog. Use this instead of calling setVisible(true).
	 */
	public void activate() {
		new Thread(() -> setVisible(true)).start();
	}

	public synchronized void append(String msg) {
		SwingUtilities.invokeLater(() -> {
			textBox.setText(textBox.getText() + "\n" + msg);
			textBox.setCaretPosition(textBox.getText().length());
		});
	}

	public void done() {
		doneButton.setEnabled(true);
	}

	@Override
	public void accept(String s) {
		append(s);
	}
}
