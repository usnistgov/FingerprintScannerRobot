package robot;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import view.robotpanel.RobotMethodLoader;

/**
 * This class represents a command.
 *
 * @author Jacob Glueck
 *
 */
public class Command {
	
	/**
	 * The command name.
	 */
	private final String name;
	/**
	 * The command paramaters.
	 */
	private final Object[] params;

	/**
	 * Makes a new command with the specified information.
	 *
	 * @param name
	 *            the command name
	 * @param params
	 *            the command parameters.
	 */
	public Command(String name, Object... params) {
	
		this.name = name;
		this.params = params;
	}
	
	/**
	 * Makes a new command with the specified information.
	 *
	 * @param name
	 *            the command name
	 * @param params
	 *            the command parameters.
	 */
	public Command(String name, List<Object> params) {

		this(name, params.toArray());
	}

	/**
	 * @return the name
	 */
	public String getName() {
	
		return name;
	}

	/**
	 * @return the paramaters
	 */
	public Object[] getParams() {
	
		return params;
	}

	/**
	 * Gets the command string.
	 *
	 * @return the command string.
	 */
	public String getCommandString() {

		return getCommandName() + " " + getArgumentsString();
	}

	/**
	 * Gets the arguments string.
	 *
	 * @return the arguments string.
	 */
	public String getArgumentsString() {

		String paramString = "";
		if (params != null && params.length != 0) {
			
			StringBuilder paramStringBuilder = new StringBuilder();
			paramStringBuilder.append(' ');
			for (Object param : params) {
				paramStringBuilder.append(param);
				paramStringBuilder.append(" ");
			}
			paramStringBuilder.deleteCharAt(paramStringBuilder.length() - 1);
			paramString = paramStringBuilder.toString();
		}
		
		return paramString;
	}
	
	/**
	 * Gets the command name.
	 *
	 * @return the command name
	 */
	public String getCommandName() {
	
		return name;
	}
	
	@Override
	public String toString() {
	
		return getCommandString();
	}
	
	/**
	 * Returns true if any of the parameters are null.
	 *
	 * @return true if any of the parameters are null.
	 */
	public boolean toAdd() {

		for (Object param : params)
			if (param == null)
				return true;
		return false;
	}

	/**
	 * Parses a command from a string.
	 *
	 * @param toParse
	 *            the string to parse the command from.
	 * @param robotType
	 *            the type of robot the command is for.
	 * @return the command.
	 * @throws ParseException
	 *             if there is an error parsing. The offset represents the token which contains the error. 1 is the first token.
	 */
	public static Command parseCommand(String toParse, Class<? extends Robot> robotType) throws ParseException {

		// Make sure there is stuff to parse.
		if (toParse.length() == 0)
			throw new ParseException("The string to parse had length 0", 0);
		
		// Split the input on whitespace
		String[] split = toParse.split("\\s+");

		// Attempt to find the method that matches the command
		String commandName = split[0];
		Method m = RobotMethodLoader.getCommandMap(robotType).get(commandName);
		if (m == null)
			throw new ParseException("No method matching " + commandName + " was found for robot type " + robotType.getSimpleName(), 0);

		// Read the parameters
		Object[] params = new Object[m.getParameterTypes().length];
		int splitIndex = 1;
		for (int x = 0; x < params.length; x++) {
			Class<?> paramType = m.getParameterTypes()[x];
			// If the parameter is an integer
			if (paramType == Integer.class) {
				int min = Integer.MIN_VALUE;
				int max = Integer.MAX_VALUE;
				for (Annotation a : m.getParameterAnnotations()[x])
					if (a.annotationType().isAssignableFrom(Bounds.class)) {
						min = ((Bounds) a).min();
						max = ((Bounds) a).max();
					}
				int tempParam;
				try {
					tempParam = Integer.parseInt(split[splitIndex]);
				} catch (NumberFormatException e) {
					throw new ParseException("Error parsing int " + split[splitIndex], splitIndex);
				}
				if (tempParam < min || tempParam > max)
					throw new ParseException("Value " + tempParam + " is not in the range [" + min + ", " + max + "]", splitIndex);
				params[x] = tempParam;
				splitIndex++;
			}
			else
				throw new ParseException("Unable to parse object of type " + paramType.getSimpleName(), splitIndex);
			// TODO Make this work with classes other than just integer
		}
		
		// Make sure there are no extra tokens
		if (splitIndex != split.length)
			throw new ParseException("Extra tokens", splitIndex + 1);

		return new Command(commandName, params);
	}

	/**
	 * Parses a list of commands from a string. Each command must be on its own line.
	 *
	 * @param toParse
	 *            the string to parse.
	 * @param robotType
	 *            the type of the robot the commands are for.
	 * @return the list of commands.
	 * @throws ParseException
	 *             if there is an error parsing. The offset represents the line number, with 1 as the first line.
	 */
	public static List<Command> parseCommands(String toParse, Class<? extends Robot> robotType) throws ParseException {
	
		String[] lines = toParse.split("\\r?\\n");
		LinkedList<Command> commands = new LinkedList<>();
		for (int x = 0; x < lines.length; x++)
			if (lines[x].length() != 0)
				try {
					commands.add(Command.parseCommand(lines[x], robotType));
				} catch (ParseException e) {
					throw new ParseException(e.getMessage(), x + 1);
				}
		return commands;
	}

	/**
	 * Returns true if the command has any null parameters.
	 *
	 * @return true if the command has any null parameters.
	 */
	public boolean containsNullParameters() {
	
		for (Object o : params)
			if (o == null)
				return true;
		return false;
	}
}