package view.configurator;

import java.text.ParseException;

import javax.swing.JPanel;

/**
 * Represents an abstract object used to configure moves.
 *
 * @author Jacob Glueck
 *
 * @param <T>
 *            the type of move that this MoveConfigurator configures.
 */
public abstract class Configurator<T> extends JPanel {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Gets the move that has been configured, or null if this configurator is empty.
	 *
	 * @return the configured move.
	 * @throws ParseException
	 *             if there is an error parsing the value.
	 */
	public abstract T get() throws ParseException;

	/**
	 * Loads the specified move into the UI.
	 *
	 * @param toLoad
	 *            the move to load.
	 */
	public abstract void load(T toLoad);

	/**
	 * Clears the configurator by deleting all user input and by setting everything to the default state.
	 */
	public abstract void clear();
}
