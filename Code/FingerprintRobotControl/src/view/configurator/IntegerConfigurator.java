package view.configurator;

import java.awt.BorderLayout;
import java.text.ParseException;

import javax.swing.JTextField;

/**
 * Allows for configuration of an integer.
 *
 * @author Jacob Glueck
 *
 */
public class IntegerConfigurator extends Configurator<Integer> {

	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The text field used for entering the number.
	 */
	private final JTextField comp;
	/**
	 * The minimum value.
	 */
	private final int min;
	/**
	 * The maximum value.
	 */
	private final int max;
	
	/**
	 * Makes a new integer configurator with the specified information.
	 *
	 * @param min
	 *            the minimum allowed value in the text field.
	 * @param max
	 *            the maximum allowed value in the text field.
	 * @param columns
	 *            the number of columns in the text field.
	 */
	public IntegerConfigurator(int min, int max, int columns) {

		comp = new JTextField(columns);
		// Add a verifier
		this.min = min;
		this.max = max;
		setLayout(new BorderLayout());
		add(comp, BorderLayout.CENTER);
	}
	
	@Override
	public Integer get() throws ParseException {

		int result = 0;
		result = getInput();
		return result;
	}
	
	@Override
	public void load(Integer toLoad) {

		if (toLoad != null)
			comp.setText(toLoad.toString());
		else
			comp.setText("");

	}
	
	@Override
	public void clear() {
	
		comp.setText("");

	}

	/**
	 * Gets the input from the text field and throws a parse exception if there is an error.
	 *
	 * @return the input.
	 * @throws ParseException
	 *             the error.
	 */
	private int getInput() throws ParseException {
	
		try {
			
			// Makes sure the field is not empty
			if (comp.getText().length() == 0)
				throw new ParseException("Field blank", 0);

			// Checks the bounds
			int result = Integer.parseInt(comp.getText());
			if (result >= min && result <= max)
				return result;
			else
				throw new ParseException("Value " + result + " is not in the range [" + min + ", " + max + "]", 0);
		} catch (NumberFormatException e) {
			throw new ParseException(e.getMessage(), 0);
		}
	}
}