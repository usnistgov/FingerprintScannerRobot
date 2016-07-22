package robot;

import java.io.IOException;

/**
 * Represents a Networked Automated Rolled Fingerprint Scanner Test Robot (NARFSTR).
 *
 * @author Jacob Glueck
 *
 */
public class NARFSTR extends Robot {

	/**
	 * Makes a new NARFSTR that is connected over the specified RobotCom.
	 *
	 * @param c
	 *            the RobotCom over which the robot is connected.
	 */
	public NARFSTR(RobotCom<?> c) {
	
		super(c);
		if (!c.getRobotInfo().getRobotType().equals(getClass().getSimpleName()))
			throw new IllegalArgumentException("Cannot connect class of type " + getClass().getSimpleName() + " to robot of type " + c.getRobotInfo().getRobotType());
	}

	/**
	 * The stroke command. This command makes the robot move. It does not return until the move is complete, but can be interrupted.
	 *
	 * @throws IOException
	 *             if there is a problem communicating with the robot.
	 * @throws InterruptedException
	 *             if the move is interrupted.
	 */
	@RobotCommand(commandName = "stroke", paramNames = {})
	public void stroke() throws IOException, InterruptedException {

		executeCommand("stroke");
	}

	/**
	 * The set command. This command sets the parameters for the stroke move. It does not return until complete, but can be interrupted.
	 *
	 * @param forwardSpeed
	 *            the motor's speed when driving the finger away from the starting position. Between 0 and 255, inclusive.
	 * @param buttonWaitTime
	 *            the amount of time, in ms, the robot should wait once the finger hits the limit switch.
	 * @param reverseSpeed
	 *            the motor's speed when driving the finger back to the starting position. Between 0 and 255, inclusive.
	 * @param returnWaitTime
	 *            the amount of time to wait once the finger has returned to its original position.
	 * @throws IOException
	 *             if there is a problem communicating with the robot.
	 * @throws InterruptedException
	 *             if the move is interrupted.
	 */
	@RobotCommand(commandName = "set", paramNames = { "Forward Speed", "Button Wait Time", "Reverse Speed", "Return Wait Time" })
	public void set(@Bounds(min = 0, max = 255) Integer forwardSpeed, @Bounds(min = 0, max = Short.MAX_VALUE) Integer buttonWaitTime, @Bounds(min = 0, max = 255) Integer reverseSpeed,
			@Bounds(min = 0, max = Short.MAX_VALUE) Integer returnWaitTime) throws IOException, InterruptedException {

		executeCommand("set", forwardSpeed, buttonWaitTime, reverseSpeed, returnWaitTime);
	}

	/**
	 * The reset command. This command causes the robot to drive the finger towards the starting position.
	 *
	 * @param time
	 *            the amount of time the finger will be in motion, in ms.
	 * @param speed
	 *            the motor's speed, between 0 and 255 inclusive.
	 * @throws IOException
	 *             if there is a problem communicating with the robot.
	 * @throws InterruptedException
	 *             if the move is interrupted.
	 */
	@RobotCommand(commandName = "reset", paramNames = { "Time", "Speed" })
	public void reset(@Bounds(min = 0, max = Short.MAX_VALUE) Integer time, @Bounds(min = 0, max = 255) Integer speed) throws IOException, InterruptedException {

		executeCommand("reset", time, speed);
	}
}
