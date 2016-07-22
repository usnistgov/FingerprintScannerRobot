package view.configurator;

import java.awt.BorderLayout;
import java.text.ParseException;

import view.util.LabeledComponent;

/**
 * A configurator with a label.
 *
 * @author Jacob Glueck
 *
 * @param <T>
 *            the type of object this configurator configures.
 */
public class LabeledConfigurator<T> extends Configurator<T> {

	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The labled component.
	 */
	private final LabeledComponent<Configurator<T>> labled;

	/**
	 * Makes a new labeled configurator with the specified information.
	 *
	 * @param label
	 *            the label to use.
	 * @param config
	 *            the configurator to use.
	 * @param space
	 *            the amount of space between the label and the configurator.
	 */
	public LabeledConfigurator(String label, Configurator<T> config, int space) {
	
		setLayout(new BorderLayout());
		labled = new LabeledComponent<Configurator<T>>(label, config, space);
		add(labled, BorderLayout.CENTER);
	}
	
	/**
	 * Makes a new labeled configurator with the specified information.
	 *
	 * @param label
	 *            the label to use.
	 * @param config
	 *            the configurator to use.
	 * @param space
	 *            the amount of space between the label and the configurator.
	 * @param labelWidth
	 *            the width of the label.
	 */
	public LabeledConfigurator(String label, Configurator<T> config, int space, int labelWidth) {

		this(label, config, space);
		labled.setLabelWidth(labelWidth);

	}
	
	@Override
	public T get() throws ParseException {

		return labled.getComponent().get();
	}
	
	@Override
	public void load(T toLoad) {

		labled.getComponent().load(toLoad);
	}
	
	@Override
	public void clear() {

		labled.getComponent().clear();
	}
}