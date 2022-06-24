package nl.andrewl.emaildatasetbrowser.view.common;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import java.awt.Font;

/**
 * Simple label that allows you to select its contents.
 */
public class SelectableLabel extends JTextPane {
    private final Font font;

    public SelectableLabel() {
        Font font = UIManager.getFont("Label.font");
        this.font = (font != null) ? font : new Font("Tahoma", Font.PLAIN, 11);
        build();
    }

    private void build() {
        setContentType("text/html");

        setEditable(false);
        setBackground(null);
        setBorder(null);

        putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        setFont(font);
    }
}