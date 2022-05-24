package nl.andrewl.emaildatasetbrowser.view;

import javax.swing.*;

/**
 * A specialized combo box for a labelled selection of a nullable boolean
 * value, which is especially useful for things like optional search filters.
 */
public class BooleanSelect extends JComboBox<String> {
	private final String nullLabel;
	private final String trueLabel;
	private final String falseLabel;

	/**
	 * Constructs the select element.
	 * @param nullLabel A label to show when null is selected. If this label is
	 *                  null, no null option will be given.
	 * @param trueLabel A label to show when true is selected.
	 * @param falseLabel A label to show when false is selected.
	 */
	public BooleanSelect(String nullLabel, String trueLabel, String falseLabel) {
		this.nullLabel = nullLabel;
		this.trueLabel = trueLabel;
		this.falseLabel = falseLabel;
		if (nullLabel != null) {
			addItem(nullLabel);
		}
		addItem(trueLabel);
		addItem(falseLabel);
	}

	/**
	 * Constructs the select element with default labels.
	 */
	public BooleanSelect() {
		this("N/A", "True", "False");
	}

	/**
	 * Sets the boolean value of this select.
	 * @param value The value to set.
	 */
	public void setSelectedValue(Boolean value) {
		if (value == null) {
			setSelectedItem(nullLabel);
		} else {
			if (value) {
				setSelectedItem(trueLabel);
			} else {
				setSelectedItem(falseLabel);
			}
		}
	}

	/**
	 * Gets the boolean value of this select.
	 * @return The selected value.
	 */
	public Boolean getSelectedValue() {
		String selectedValue = (String) this.getSelectedItem();
		if (trueLabel.equals(selectedValue)) return true;
		if (falseLabel.equals(selectedValue)) return false;
		return null;
	}
}
