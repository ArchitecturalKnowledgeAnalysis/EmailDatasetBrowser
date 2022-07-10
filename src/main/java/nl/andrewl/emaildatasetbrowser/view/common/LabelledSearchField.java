package nl.andrewl.emaildatasetbrowser.view.common;

import nl.andrewl.emaildatasetbrowser.view.ConcreteKeyEventListener;
import nl.andrewl.emaildatasetbrowser.view.ConcreteKeyEventListener.KeyEventType;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

import javax.swing.*;

/**
 * Implements an interactive JTextArea with a corresponding label.
 */
public class LabelledSearchField extends JPanel {
    private JTextArea queryField;

    public LabelledSearchField(String label, Consumer<KeyEvent> onSearch) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JPanel labelWrapper = new JPanel(new GridLayout(1, 1));
        labelWrapper.add(new JLabel(label));
        add(labelWrapper);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
        queryField = new JTextArea();
        queryField.setMargin(new Insets(0, 0, 5, 0));
        queryField.setLineWrap(true);
        ConcreteKeyEventListener rText = new ConcreteKeyEventListener()
                .addKeyListener(KeyEventType.KEY_RELEASED, KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK, onSearch);
        queryField.addKeyListener(rText);
        var queryScrollPane = new JScrollPane(queryField);
        queryScrollPane.setPreferredSize(new Dimension(-1, 100));
        add(queryScrollPane, BorderLayout.CENTER);
    }

    public JTextArea getQueryField() {
        return queryField;
    }
}
