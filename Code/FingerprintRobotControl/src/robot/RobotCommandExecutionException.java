package robot;

/**
 * The exception throw if there is an error executing a command.
 *
 * @author Jacob Glueck
 *
 */
public class RobotCommandExecutionException extends RuntimeException {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Makes a new exception with the specified information.
	 *
	 * @param commandName
	 *            the name of the command that caused the error.
	 * @param paramString
	 *            the parameters the caused the error.
	 * @param response
	 *            the robot's response.
	 */
	public RobotCommandExecutionException(String commandName, String paramString, String response) {
	
		super("Error executing " + commandName + " command with parameters: " + paramString + ". Received response: " + response + ".");
	}
}
