package nl.andrewl.emaildatasetbrowser.view;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A dialog that can be used to show the progress of an ongoing task by
 * periodically posting messages to the dialog's log text component.
 */
public class ProgressDialog extends JDialog implements Consumer<String> {
	private static final int MIN_OPEN_TIME = 1000;

	private final JTextArea textBox;
	private final JButton doneButton;
	private final JButton cancelButton;
	private Runnable cancelAction;
	private Instant openedAt;

	public ProgressDialog(Window owner, String title, String description) {
		this(owner, title, description, true, true, true);
	}

	public ProgressDialog(Window owner, String title, String description, boolean showText, boolean showCancel, boolean showDone) {
		super(owner, title, ModalityType.APPLICATION_MODAL);

		JPanel p = new JPanel(new BorderLayout());
		if (description != null) {
			p.add(new JLabel(description), BorderLayout.NORTH);
		}

		if (showText) {
			textBox = new JTextArea();
			textBox.setEditable(false);
			textBox.setWrapStyleWord(true);
			textBox.setLineWrap(true);
			textBox.setFont(new Font("monospaced", textBox.getFont().getStyle(), 12));
			DefaultCaret caret = (DefaultCaret) textBox.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			JScrollPane scrollPane = new JScrollPane(textBox, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setPreferredSize(new Dimension(500, 400));
			p.add(scrollPane, BorderLayout.CENTER);
		} else {
			textBox = null;
		}

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		if (showCancel) {
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(e -> {
				if (cancelAction != null) cancelAction.run();
				dispose();
			});
			buttonPanel.add(cancelButton);
		} else {
			cancelButton = null;
		}
		if (showDone) {
			doneButton = new JButton("Done");
			doneButton.setEnabled(false);
			doneButton.addActionListener(e -> dispose());
			buttonPanel.add(doneButton);
		} else {
			doneButton = null;
		}
		if (showCancel || showDone) p.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(p);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setLocationRelativeTo(owner);
	}

	public static ProgressDialog minimal(Component component, String title, String description) {
		var dialog = new ProgressDialog(
				SwingUtilities.getWindowAncestor(component),
				title,
				description,
				false,
				false,
				false
		);
		dialog.activate();
		return dialog;
	}

	public static ProgressDialog minimalText(Component component, String title) {
		var dialog = new ProgressDialog(
				SwingUtilities.getWindowAncestor(component),
				title,
				null,
				true,
				false,
				false
		);
		dialog.activate();
		return dialog;
	}

	/**
	 * Begins showing the dialog. Use this instead of calling setVisible(true).
	 */
	public void activate() {
		new Thread(() -> {
			openedAt = Instant.now();
			setVisible(true);
		}).start();
	}

	/**
	 * Binds this progress dialog to the given future, such that when the
	 * future completes, this dialog is marked as done. If no other cancel
	 * action has been specified, this will also make it such that if the user
	 * cancels the progress dialog, the future will be cancelled.
	 * @param future The future to bind this progress dialog to.
	 */
	public void bind(CompletableFuture<?> future) {
		future.handle((o, throwable) -> {
			if (throwable != null) {
				append("An error occurred: " + throwable.getMessage());
			}
			done();
			return null;
		});
		if (cancelAction == null) {
			cancelAction = () -> future.cancel(true);
		}
	}

	/**
	 * Set what happens when the user cancels the progress dialog.
	 * @param cancelAction An action to perform when the user cancels.
	 */
	public void onCancel(Runnable cancelAction) {
		this.cancelAction = cancelAction;
	}

	/**
	 * Appends a message to the dialog.
	 * @param msg The message to append.
	 */
	public synchronized void append(String msg) {
		if (textBox != null) {
			SwingUtilities.invokeLater(() -> {
				textBox.setText(textBox.getText() + "\n" + msg);
				textBox.setCaretPosition(textBox.getText().length());
			});
		}
	}

	/**
	 * Helper function to append a formatted string to the dialog.
	 * @param msg The format string.
	 * @param args The arguments for the string.
	 */
	public void appendF(String msg, Object... args) {
		append(String.format(msg, args));
	}

	/**
	 * Marks this progress dialog as done. The user can no longer cancel, and
	 * if this dialog was configured to show a "Done" button upon completion,
	 * it will show that now, otherwise the dialog just closes.
	 */
	public void done() {
		if (doneButton != null) {
			if (cancelButton != null) {
				cancelButton.setEnabled(false);
			}
			doneButton.setEnabled(true);
		} else {
			close();
		}
	}

	private void close() {
		if (openedAt == null) {
			CompletableFuture.delayedExecutor(MIN_OPEN_TIME, TimeUnit.MILLISECONDS).execute(this::dispose);
		} else {
			Instant now = Instant.now();
			Duration openTime = Duration.between(openedAt, now);
			if (openTime.toMillis() < MIN_OPEN_TIME) {
				long timeToWait = MIN_OPEN_TIME - openTime.toMillis();
				CompletableFuture.delayedExecutor(timeToWait, TimeUnit.MILLISECONDS).execute(this::dispose);
			} else {
				dispose();
			}
		}
	}

	@Override
	public void accept(String s) {
		append(s);
	}
}
