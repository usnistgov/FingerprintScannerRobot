package view.robotpanel;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import robot.Robot;
import robot.RobotCommand;

/**
 * This class loads the methods and annotations in the robot classes.
 *
 * @author Jacob Glueck
 *
 */
public class RobotMethodLoader {

	/**
	 * The map that links robot types and command names to methods.
	 */
	private static Map<Class<? extends Robot>, Map<String, Method>> robotCommandMap = new HashMap<>();
	
	/**
	 * Prevents instances of this class.
	 */
	private RobotMethodLoader() {
	
	}

	/**
	 * Gets the command map for the specified robot type.
	 *
	 * @param r
	 *            the type of robot to get the command map for.
	 * @return the command map, built using the annotations in the specified class.
	 */
	public static Map<String, Method> getCommandMap(Class<? extends Robot> r) {
	
		if (!RobotMethodLoader.robotCommandMap.containsKey(r)) {
			HashMap<String, Method> commandMap = new HashMap<String, Method>();
			RobotMethodLoader.robotCommandMap.put(r, commandMap);
			Method[] methods = r.getMethods();
			for (Method m : methods)
				if (m.isAnnotationPresent(RobotCommand.class))
					commandMap.put(m.getAnnotation(RobotCommand.class).commandName(), m);
		}
		return RobotMethodLoader.robotCommandMap.get(r);
	}
	
}
