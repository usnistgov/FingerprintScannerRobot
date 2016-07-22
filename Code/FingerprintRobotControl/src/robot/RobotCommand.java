package robot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a method that is a robot command. This allows the UI to use reflection at runtime to build the graphical configurator.
 *
 * @author Jacob Glueck
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RobotCommand {

	/**
	 * Gets the command name.
	 *
	 * @return the command name.
	 */
	String commandName();

	/**
	 * Gets the parameter names.
	 * 
	 * @return the paramater names.
	 */
	String[] paramNames();
}
