/*
 DHCP-based IP printer

 This sketch uses the DHCP extensions to the Ethernet library
 to get an IP address via DHCP and print the address obtained.
 using an Arduino Wiznet Ethernet shield.

 Circuit:
 * Ethernet shield attached to pins 10, 11, 12, 13

 created 12 April 2011
 modified 9 Apr 2012
 by Tom Igoe

 */

#include <SPI.h>
#include <Ethernet.h>

// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = { 0x00, 0xAA, 0xBB, 0xCC, 0xDE, 0x02 };

//The port to write to to disable the SD card
const byte SD_PORT = 4;
//The state to write to the SD_PORT
const byte SD_STATE = HIGH;
//The port to write to to enable the Ethernet
const byte E_PORT = 10;
//The state to write to the E_PORT
const byte E_STATE = LOW;

// Initialize the Ethernet client library
// with the IP address and port of the server
// that you want to connect to (port 80 is default for HTTP):
EthernetClient client;

unsigned long startTime;

void setup() {

	startTime = millis();

	//Disable the SD card reader
	pinMode(SD_PORT, SD_STATE);
	pinMode(E_PORT, E_STATE);

	// Open serial communications and wait for port to open:
	Serial.begin(9600);
	// this check is only needed on the Leonardo:
	while (!Serial) {
		; // wait for serial port to connect. Needed for Leonardo only
	}

	Serial.print(millis() - startTime);
	Serial.print(": ");
	Serial.println("Ethernet begin");
	// start the Ethernet connection:
	if (Ethernet.begin(mac) == 0) {
		Serial.print(millis() - startTime);
		Serial.print(": ");
		Serial.println("Failed to configure Ethernet using DHCP");
		// no point in carrying on, so do nothing forevermore:
		while (true)
			;
	}
}

bool isConnected = false;

long lastTime = 0;

void loop() {

	long currentTime = millis();
	Serial.print(millis() - lastTime);
	lastTime = currentTime;

	if (!isConnected) {

		Serial.print(": DHCP Status");
		int ret = Ethernet.dhcp_poll();
		Serial.println(ret);
		if (ret == 1)
			isConnected = true;
	} else {
		// print your local IP address:
		Serial.print(": ");
		Serial.print("My IP address: ");
		for (byte thisByte = 0; thisByte < 4; thisByte++) {
			// print the value of each byte of the IP address:
			Serial.print(Ethernet.localIP()[thisByte], DEC);
			Serial.print(".");
		}
		Serial.println();
	}
}

