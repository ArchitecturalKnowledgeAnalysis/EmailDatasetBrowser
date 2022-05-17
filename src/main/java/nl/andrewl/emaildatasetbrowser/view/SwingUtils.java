package nl.andrewl.emaildatasetbrowser.view;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public final class SwingUtils {
	private SwingUtils() {
	}

	public static void setAllButtonsEnabled(Container c, boolean enabled) {
		for (var component : c.getComponents()) {
			if (component instanceof JButton button) {
				button.setEnabled(enabled);
			} else if (component instanceof JTextComponent text) {
				text.setEnabled(enabled);
			} else if (component instanceof JCheckBox checkBox) {
				checkBox.setEnabled(enabled);
			} else if (component instanceof JComboBox<?> comboBox) {
				comboBox.setEnabled(enabled);
			} else if (component instanceof Container nested) {
				setAllButtonsEnabled(nested, enabled);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Component> T findFirstInstance(Container c, Class<T> type) {
		for (var component : c.getComponents()) {
			if (component.getClass().equals(type)) {
				return (T) component;
			} else if (component instanceof Container nested) {
				T result = findFirstInstance(nested, type);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	public static boolean confirm(Component parent, String msg) {
		int result = JOptionPane.showConfirmDialog(parent, msg, "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		return result == JOptionPane.OK_OPTION;
	}
}
