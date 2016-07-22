#include <Arduino.h>
#include <FingerprintRobotCom.h>
#include <HardwareSerial.h>
#include <RGBTools/RGBTools.h>
#include <stdbool.h>
#include <stdint.h>
#include <VNH5019Driver.h>
#include <WString.h>

using namespace FingerprintRobotComLib;
using namespace VNH5019DriverLib;

//Define numbers that correspond to commands
#define COMMAND_STROKE 0
#define COMMAND_SET 1
#define COMMAND_RESET 2
#define COMMAND_VERIFY 3
//Define the number of commands
#define NUM_COMMANDS 4
//Define an array of string commands in the same order as the constants above
const String COMMANDS[NUM_COMMANDS] = { "stroke", "set", "reset", CHALLENGE };
int maxCommandLength;

//Pins
#define RGB_LED_R 3
#define RGB_LED_G 5
#define RGB_LED_B 6
#define VNH5019_INA 7
#define VNH5019_INB 8
#define VNH5019_PWM 9
#define ACUTATOR_PARITY -1
#define LIMIT_SWITCH_PIN A0
#define TRIGGER_SWITCH_PIN A1
#define NETWORKING_SWITCH_PIN A2

#define FOWARD_SPEED_LOC 0
#define BUTTON_WAIT_TIME_LOC 1
#define RETURN_SPEED_LOC 2
#define RETURN_WAIT_TIME_LOC 3
#define NUM_MOVE_PARAMETERS 4
int moveMatrix[NUM_MOVE_PARAMETERS];
#define DEFAULT_FOWARD_SPEED 120
#define DEFAULT_BUTTON_WAIT_TIME 4000
#define DEFAULT_RETURN_SPEED DEFAULT_FOWARD_SPEED
#define DEFAULT_RETURN_WAIT_TIME DEFAULT_BUTTON_WAIT_TIME
const uint32_t COLOR_RED = 0xff0000L;
const uint32_t COLOR_YELLOW = 0xffff00;
const uint32_t COLOR_PURPLE = 0xff00ff;
const uint32_t COLOR_GREEN = 0x00ff00;
const uint32_t COLOR_BLUE = 0x0000ff;
const uint32_t COLOR_AQUA = 0x00ffff;
const uint32_t COLOR_WHITE = 0xffffff;
const uint32_t COLOR_ORANGE = 0xffa500;
#define BLINK_TIME 500
//Holds the move start time
unsigned long startTime;
uint32_t nextColor = COLOR_RED;

FingerprintRobotCom narfstrCom("NARFSTR");
RGBTools indicatorLED(RGB_LED_R, RGB_LED_G, RGB_LED_B, COMMON_CATHODE);
VNH5019Driver actuator(VNH5019_INA, VNH5019_INB, VNH5019_PWM, ACUTATOR_PARITY);

bool lastTriggerSwitchState;
bool lastNetworkSwitchState;

//Setup method
void setup() {
	indicatorLED.setColor(COLOR_ORANGE);
	int ret = narfstrCom.begin();
	if (ret == MAC_ERROR || ret == DHCP_START_ERROR) {
		narfstrCom.setNetworking(false);
	}

	//Find the maximum command length
	maxCommandLength = narfstrCom.maxCommandLength(COMMANDS, NUM_COMMANDS);

	//Write default move
	moveMatrix[FOWARD_SPEED_LOC] = DEFAULT_FOWARD_SPEED;
	moveMatrix[BUTTON_WAIT_TIME_LOC] = DEFAULT_BUTTON_WAIT_TIME;
	moveMatrix[RETURN_SPEED_LOC] = DEFAULT_RETURN_SPEED;
	moveMatrix[RETURN_WAIT_TIME_LOC] = DEFAULT_RETURN_WAIT_TIME;

	lastTriggerSwitchState = false;
	lastNetworkSwitchState = false;
}

void loop() {
	//Check the DHCP status
	int dhcpStatus = narfstrCom.pollDHCP();
	if (dhcpStatus == DHCP_IN_PROGRESS)
		nextColor = COLOR_PURPLE;
	else if (dhcpStatus == DHCP_SUCCESSFUL) {
		nextColor = COLOR_GREEN;
		/*for (byte thisByte = 0; thisByte < 4; thisByte++) {
		 Serial.print(narfstrCom.localIP()[thisByte], DEC);
		 Serial.print(".");
		 }
		 Serial.println();*/
	} else if (dhcpStatus == NETWORKING_OFF)
		nextColor = COLOR_RED;
	else
		nextColor = COLOR_ORANGE;

	if (wasPulse(&lastTriggerSwitchState, isButtonDown(TRIGGER_SWITCH_PIN)))
		stroke();
	if(wasPulse(&lastNetworkSwitchState, isButtonDown(NETWORKING_SWITCH_PIN)))
		narfstrCom.retryDHCP();

//	Serial.println(millis());

//Check to see if there is any data and respond to UDP discovery requests.
	checkForData();
	indicatorLED.setColor(nextColor);
}

void checkForData() {
	bool dataAvailable = narfstrCom.dataCheck();

	if (!dataAvailable) {
		return;
	}
	int commandID = narfstrCom.readCommandID(COMMANDS, NUM_COMMANDS, maxCommandLength);
	//If the command is empty, return
	if (commandID == EMPTY_COMMAND)
		return;

	if (commandID == BAD_COMMAND) {
		narfstrCom.println(SYNTAX_ERROR);
	} else {
		//Command is neither bad nor empty, execute it
		switch (commandID) {
		case COMMAND_SET:
			for (int x = 0; x < NUM_MOVE_PARAMETERS; x++) {
				int param = narfstrCom.parseInt();
				//Error checking: make sure the parameter is an positive number. If it is a speed (indices 0 or 2), make sure it is less than or equal to 255.
				if (param == PARSE_INT_ERROR || param < 0 || (x % 2 == 0 && param > 255)) {
					narfstrCom.println(SYNTAX_ERROR);
					return;
				} else {
					moveMatrix[x] = param;
				}
			}

			//Finished reading command, send received message
			narfstrCom.sendStatus(COMMANDS[commandID], RECEIVED);
			narfstrCom.sendStatus(COMMANDS[commandID], END);
			break;
		case COMMAND_STROKE: {
			narfstrCom.sendStatus(COMMANDS[commandID], RECEIVED);

			stroke();
			narfstrCom.sendStatus(COMMANDS[commandID], END);
			break;
		}
		case COMMAND_RESET: {
			int time = narfstrCom.parseInt();
			//Error checking: make sure the parameter is an positive number. If it is a speed (indices 0 or 2), make sure it is less than or equal to 255.
			if (time == PARSE_INT_ERROR || time < 0) {
				narfstrCom.println(SYNTAX_ERROR);
				return;
			}
			int speed = narfstrCom.parseInt();
			//Error checking: make sure the parameter is an positive number. If it is a speed (indices 0 or 2), make sure it is less than or equal to 255.
			if (speed == PARSE_INT_ERROR || speed < 0 || speed > 255) {
				narfstrCom.println(SYNTAX_ERROR);
				return;
			}
			narfstrCom.sendStatus(COMMANDS[commandID], RECEIVED);
			indicatorLED.setColor(COLOR_AQUA);
			actuator.setSpeed(speed * -1);
			delay(time);
			narfstrCom.sendStatus(COMMANDS[commandID], END);
			break;
		}
		case COMMAND_VERIFY:
			narfstrCom.sendVerificationResponse();
		}
	}
}

void stroke() {
	//Execute the stroke
	unsigned long strokeStartTime = millis();
	indicatorLED.setColor(COLOR_BLUE);
	actuator.setSpeed(moveMatrix[FOWARD_SPEED_LOC]);
	while (!isButtonDown(LIMIT_SWITCH_PIN))
		;
	actuator.setSpeed(0);
	unsigned long travelTime = millis() - strokeStartTime;
	indicatorLED.setColor(COLOR_WHITE);
	delay(moveMatrix[BUTTON_WAIT_TIME_LOC]);
	indicatorLED.setColor(COLOR_AQUA);
	actuator.setSpeed(moveMatrix[RETURN_SPEED_LOC] * -1);
	double ratio = moveMatrix[FOWARD_SPEED_LOC] / (double) (moveMatrix[RETURN_SPEED_LOC]);
	delay(round(ratio * travelTime));
	indicatorLED.setColor(COLOR_WHITE);
	delay(moveMatrix[RETURN_WAIT_TIME_LOC]);
	actuator.setSpeed(0);
}

bool isButtonDown(int pin) {
	return analogRead(pin) >= 200;
}

//Updates last state with current state, and if lastState is true and current state is false, returns true because a pulse must have occurred.
bool wasPulse(bool* lastState, bool currentState) {
	bool toReturn = false;
	if (*lastState == true && currentState == false)
		toReturn = true;
	*lastState = currentState;
	return toReturn;
}

//Gets the amount of free RAM
int freeRam() {
	extern int __heap_start, *__brkval;
	int v;
	return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval);
}
