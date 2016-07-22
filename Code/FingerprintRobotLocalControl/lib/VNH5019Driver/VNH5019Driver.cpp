/*
 * VNH5019Driver.cpp
 *
 *  Created on: Jun 23, 2015
 *      Author: jacob
 */

#include <Arduino.h>
#include <VNH5019Driver.h>

namespace VNH5019DriverLib {

#define BRAKE_VCC 3 //0b11
#define CLOCKWISE 2 //0b10
#define COUNTERCLOCKWISE 1 //0b01
#define BRAKE_GND 0 // 0b00

VNH5019Driver::VNH5019Driver(int INA, int INB, int PWM, int parity) {
	this->INA = INA;
	this->INB = INB;
	this->PWM = PWM;
	this->parity = parity;

	pinMode(INA, OUTPUT);
	pinMode(INB, OUTPUT);
	pinMode(PWM, OUTPUT);
}
/**
 * Sets the speed of the motor. Takes a speed in the range [-255,255]. A positive speed will set INA to 0 and INB to 1, putting the chip into counterclockwise mode. A negative speed will set INA to 1 and INB to 0, putting the chip into clockwise mode. A speed of zero will also put the chip into counterclockwise mode, but will not cause the motor to spin.
 */
void VNH5019Driver::setSpeed(int speed) {

	speed *= parity;

	setControlPins(speed >= 0 ? COUNTERCLOCKWISE : CLOCKWISE);
	//Serial.println(abs(speed));
	analogWrite(PWM, abs(speed));
	//Serial.print("PWM: ");
	//Serial.println(PWM);
}
/**
 * Not really sure what this does, but it sets INA and INB to HIGH.
 */
void VNH5019Driver::brakeToVCC() {
	analogWrite(PWM, abs(0));
	setControlPins(BRAKE_VCC);
}
/**
 * Not really sure what this does, but it sets INA and INB to LOW.
 */
void VNH5019Driver::brakeToGround() {
	analogWrite(PWM, abs(0));
	setControlPins(BRAKE_GND);
}

void VNH5019Driver::setControlPins(int state) {

	//Serial.print("INA: ");
	uint8_t INAState = (state & 2) >> 1;
	uint8_t INBState = state & 1;
	//Serial.println(INAState);
	//Serial.print("INB: ");
	//Serial.println(INBState);

	digitalWrite(INA, INAState);
	digitalWrite(INB, INBState);
}

} /* namespace VNH5019DriverLib */
