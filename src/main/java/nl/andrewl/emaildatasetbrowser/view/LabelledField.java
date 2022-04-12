package nl.andrewl.emaildatasetbrowser.view;

import javax.swing.*;
import java.awt.*;

public class LabelledField extends JPanel {
    public LabelledField(String label, Component component) {
        super(new BorderLayout());
        add(new JLabel(label), BorderLayout.NORTH);
        add(component, BorderLayout.CENTER);
    }
}
