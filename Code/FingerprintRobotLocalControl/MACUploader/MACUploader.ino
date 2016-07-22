using namespace FingerprintRobotComLib;

#include <FingerprintRobotCom.h>

FingerprintRobotCom com("test");

byte MAC[] = { 0x90, 0xA2, 0xDA, 0x0F, 0x95, 0x39 };
byte test[6];

//The setup function is called once at startup of the sketch
void setup() {
	com.saveMAC(MAC);
	com.readMAC(test);
	Serial.begin(9600);
}

// The loop function is called in an endless loop
void loop() {

	for (unsigned int x = 0; x < 6; x++) {
		Serial.print(String(test[x], HEX));
		Serial.print(":");
	}
	Serial.println();
}
