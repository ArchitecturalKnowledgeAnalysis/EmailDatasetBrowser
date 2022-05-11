package nl.andrewl.emaildatasetbrowser.util;

import java.awt.Color;
import java.util.Random;

public class ColorHelper {
    public static final Random random = new Random();

    private static final Color BG_COLOR = new Color(70, 73, 75);
    private static final double CONTRAST_THRESHOLD = 2.25F;

    /**
     * Returns a random color that has high contrast with the background.
     */
    public static Color getColor(String text) {
        random.setSeed(text.hashCode());
        Color color = null;
        do {
            float hue = random.nextFloat();
            float saturation = random.nextFloat() / 4f + 0.75f;
            float luminance = 0.9f;
            color = Color.getHSBColor(hue, saturation, luminance);
        } while (!hasEnoughContrast(color, BG_COLOR));
        return color;
    }

    /**
     * Calculates the luminosity contrast between two colors.
     * Returns whether the contrast between these two is high enough.
     */
    private static boolean hasEnoughContrast(Color colorA, Color colorB) {
        double L1 = 0.2126 * Math.pow((double) colorA.getRed() / 255f, 2.2) +
                0.7152 * Math.pow((double) colorA.getGreen() / 255f, 2.2) +
                0.0722 * Math.pow((double) colorA.getBlue() / 255f, 2.2);
        double L2 = 0.2126 * Math.pow((double) colorB.getRed() / 255f, 2.2) +
                0.7152 * Math.pow((double) colorB.getGreen() / 255f, 2.2) +
                0.0722 * Math.pow((double) colorB.getBlue() / 255f, 2.2);
        double luminosityContrast = L1 > L2
                ? (L1 + 0.05) / (L2 + 0.05)
                : (L2 + 0.05) / (L1 + 0.05);
        return luminosityContrast > CONTRAST_THRESHOLD;
    }
}
