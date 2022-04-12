package nl.andrewl.emaildatasetbrowser.control;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public final class SwingUtils {
	public static final Random random = new Random();
	private SwingUtils() {}

	public static void setAllButtonsEnabled(Container c, boolean enabled) {
		for (var component : c.getComponents()) {
			if (component instanceof JButton button) {
				button.setEnabled(enabled);
			} else if (component instanceof Container nested) {
				setAllButtonsEnabled(nested, enabled);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Component> T findFirstInstance(Container c, Class<T> type) {
		for (var component: c.getComponents()) {
			if (component.getClass().equals(type)) {
				return (T) component;
			} else if (component instanceof Container nested) {
				T result = findFirstInstance(nested, type);
				if (result != null) return result;
			}
		}
		return null;
	}

	public static Color getColor(String text) {
		random.setSeed(text.hashCode());
		float hue = random.nextFloat();
		float saturation = random.nextFloat() / 4f + 0.75f;
		float luminance = 0.9f;
		return Color.getHSBColor(hue, saturation, luminance);
	}
}
