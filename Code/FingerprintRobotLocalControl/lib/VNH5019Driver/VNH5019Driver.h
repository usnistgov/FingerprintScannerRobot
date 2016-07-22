/*
 * VNH5019Driver.h
 *
 *  Created on: Jun 23, 2015
 *      Author: jacob
 */

#ifndef LIBRARIES_VNH5019DRIVER_VNH5019DRIVER_H_
#define LIBRARIES_VNH5019DRIVER_VNH5019DRIVER_H_

namespace VNH5019DriverLib {

class VNH5019Driver {
public:

	/**
	 * Makes a new driver with the specified information. If the motor goes fowards when you want it to go backwards, make parity -1. By default, it is 1.
	 */
	VNH5019Driver(int INA, int INB, int PWM, int parity = 1);
	/**
	 * Sets the speed of the motor. Takes a speed in the range [-255,255]. A positive speed will set INA to 0 and INB to 1, putting the chip into counterclockwise mode. A negative speed will set INA to 1 and INB to 0, putting the chip into clockwise mode. A speed of zero will also put the chip into counterclockwise mode, but will not cause the motor to spin.
	 */
	void setSpeed(int speed);
	/**
	 * Not really sure what this does, but it sets INA and INB to HIGH.
	 */
	void brakeToVCC();
	/**
	 * Not really sure what this does, but it sets INA and INB to LOW.
	 */
	void brakeToGround();
private:
	int INA, INB, PWM, parity;
	void setControlPins(int state);
};

} /* namespace VNH5019DriverLib */

#endif /* LIBRARIES_VNH5019DRIVER_VNH5019DRIVER_H_ */
