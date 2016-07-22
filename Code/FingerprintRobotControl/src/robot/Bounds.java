package robot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the bounds on an integer parameter.
 *
 * @author Jacob Glueck
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Bounds {
	
	/**
	 * Gets the minimum allowed value
	 *
	 * @return the minimum allowed value
	 */
	int min();
	
	/**
	 * Gets the maximum allowed value
	 *
	 * @return the maximum allowed value
	 */
	int max();
}
