package view.configurator;

import java.util.List;

import robot.Command;
import robot.Robot;

/**
 * A configurator that configures a list of commands for a robot.
 *
 * @author Jacob Glueck
 *
 */
public abstract class RobotConfigurator extends Configurator<List<Command>> {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The type of robot this configurator configures commands for.
	 */
	private Class<? extends Robot> robotType;

	/**
	 * Makes a new robot configurator with the specified information.
	 *
	 * @param robotType
	 *            the type of robot this configurator configures commands for.
	 */
	public RobotConfigurator(Class<? extends Robot> robotType) {

		this.robotType = robotType;
	}

	/**
	 * Sets the robot type.
	 *
	 * @param r
	 *            the new type.
	 * @return true if allowed.
	 */
	public final boolean setRobotType(Class<? extends Robot> r) {
	
		if (handleRobotChange(r)) {
			robotType = r;
			return true;
		} else
			return false;
		
	}

	/**
	 * Determines if and robot type change is permitted and if so, does it.
	 * 
	 * @param newRobotType
	 *            the new robot type.
	 * @return true if the change occurred, false otherwise.
	 */
	protected abstract boolean handleRobotChange(Class<? extends Robot> newRobotType);

	/**
	 * @return the robotType
	 */
	public Class<? extends Robot> getRobotType() {
	
		return robotType;
	}
	
}