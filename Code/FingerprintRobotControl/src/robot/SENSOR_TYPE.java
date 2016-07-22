package robot;

/**
 * The list of sensor types on the Arduino.
 *
 * @author Jacob Glueck
 *
 */
public enum SENSOR_TYPE {
	/**
	 * The LDR.
	 */
	LDR,
	/**
	 * The FSR.
	 */
	FSR;

	/**
	 * Returns the character ID of the sensor. For example, the character ID of LDR is 'l'.
	 *
	 * @return the character ID.
	 */
	public char getID() {
	
		return Character.toLowerCase(toString().charAt(0));
	}
	
	/**
	 * Gets the sensor type with the specified ID or null if no match is found.
	 *
	 * @param id
	 *            the id.
	 * @return the sensor type or null if no match is found.
	 */
	public static SENSOR_TYPE getType(char id) {

		for (SENSOR_TYPE t : SENSOR_TYPE.values())
			if (t.getID() == id)
				return t;
		return null;
	}
}
