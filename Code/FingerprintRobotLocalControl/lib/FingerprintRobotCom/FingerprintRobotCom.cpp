/*
 * FingerprintRobotCom.cpp
 *
 *Created on: Jun 22, 2015
 *Author: jacob
 */

#include <EEPROM.h>
#include <Ethernet.h>
#include <FingerprintRobotCom.h>
#include <HardwareSerial.h>
#include <Print.h>
#include <string.h>
#include <WCharacter.h>

namespace FingerprintRobotComLib {

FingerprintRobotCom::FingerprintRobotCom(String robotType, unsigned long dhcpTimeout) :
		server(TCP_PORT) {
	comMode = COM_MODE_NONE;
	this->robotType = robotType;
	this->dhcpTimeout = dhcpTimeout;
	isConnected = false;
	dhcpStartTime = 0;
	isNetworkingEnabled = true;
}

int FingerprintRobotCom::begin() {
	//Begin serial communications
	Serial.begin(9600);
	Serial.setTimeout(COM_TIME_OUT);

	//Disable the SD card reader
	pinMode(SD_PORT, SD_STATE);
	pinMode(E_PORT, E_STATE);

	if (isNetworkingEnabled) {
		if (0 != readMAC(MAC)) {
			return MAC_ERROR;
		}

		dhcpStartTime = millis();
		//Begin Ethernet communications
		if (Ethernet.begin(MAC) == 0)
			return DHCP_START_ERROR;
	}

	return 0;
}

int FingerprintRobotCom::pollDHCP() {

	if (!isNetworkingEnabled)
		return NETWORKING_OFF;

	if (isConnected) {
		return DHCP_SUCCESSFUL;
	}

	if (millis() - dhcpStartTime > dhcpTimeout)
		return DHCP_TIMEOUT;

//	Serial.println("About to call Ethernet DHCP Poll");
	int ret = Ethernet.dhcp_poll();
//	Serial.println("Done with Ethernet DHCP Poll");
	if (ret == 1) {
		//Once we connect, start networking
		isConnected = true;
		//Start UDP
		udp.begin(UDP_PORT);
		//Start TCP
		server.begin();
		//Print IP address
		//Serial.println(Ethernet.localIP());
		return DHCP_SUCCESSFUL;
	} else
		return DHCP_IN_PROGRESS;
}

void FingerprintRobotCom::retryDHCP() {
	dhcpStartTime = millis();
}

void FingerprintRobotCom::setNetworking(bool state) {
	isNetworkingEnabled = state;
	if (!isNetworkingEnabled)
		isConnected = false;
}

void FingerprintRobotCom::sendVerificationResponse() {
	print(RESPONSE_PREFIX);
	print(ID_SEPERATOR);
	print(String(robotType));
	print(ID_SEPERATOR);
	for (unsigned int x = 0; x < 6; x++) {
		String macSeg = String(MAC[x], HEX);
		if (macSeg.length() == 1)
			print("0");
		print(macSeg);
		print(ID_SEPERATOR);
	}
	print("\n");
}

bool FingerprintRobotCom::dataCheck() {
	//If the communication mode was UDP last time, we have to stop the UDP server before trying another connection type.
	if (comMode == COM_MODE_UDP) {
		udpReturn.stop();
		comMode = COM_MODE_NONE;
	}

	if (isConnected && udp.parsePacket() != 0) {
		udpReturn.connect(udp.remoteIP(), TCP_PORT);
		comMode = COM_MODE_UDP;
	} else if (isConnected && (tcpReturn.available() > 0 || (tcpReturn = server.available()))) {
		comMode = COM_MODE_ETHERNET;
	} else if (Serial.available() > 0) {
		comMode = COM_MODE_USB;
	} else {
		return false;
	}

	return true;
}

//Writes the mac address to EEPROM.
void FingerprintRobotCom::saveMAC(byte mac[]) {
	EEPROM.write(1, '#');
	for (int index = 0; index < 6; index++) {
		EEPROM.write(index + 2, mac[index]);
	}
}

/*
 * Returns 0 if successful and -1 if the format is wrong.
 */
int FingerprintRobotCom::readMAC(byte mac[]) {
	if (EEPROM.read(1) == '#') {
		for (int index = 0; index < 6; index++) {
			mac[index] = EEPROM.read(index + 2);
		}
		return 0;
	}
	return -1;
}

byte FingerprintRobotCom::readCommandID(const String commands[], int numCommands, int maxCommandLength) {
	byte buf[maxCommandLength];
	int wordLength = readWord(buf, maxCommandLength);
	if (wordLength == 0)
		return EMPTY_COMMAND;
	return decode(buf, wordLength, commands, numCommands);
}

//Decodes a command string (represented as a byte array) by finding the index the the array of commands where the command is located.
byte FingerprintRobotCom::decode(byte command[], unsigned int len, const String commands[], int numCommands) {

	for (int x = 0; x < numCommands; x++) {
		if (arrayEquals(command, len, commands[x]))
			return x;
	}
	return BAD_COMMAND;
}

//Determines if a byte array is equal to a string. The byte array must be longer
boolean FingerprintRobotCom::arrayEquals(byte a[], unsigned int len, String b) {

	if (len != b.length())
		return false;

	for (unsigned int x = 0; x < b.length(); x++) {
		if (a[x] != (byte) b.charAt(x))
			return false;
	}
	return true;
}

//Reads bytes into the buffer until a space is reached, or the end of the buffer is reached. Returns the number of characters read in. Writes zeros into the buffer before starting.
int FingerprintRobotCom::readWord(byte buffer[], unsigned int bufferLen) {

	//Zero out the buffer
	for (unsigned int x = 0; x < bufferLen; x++) {
		buffer[x] = 0;
	}

	//Nothing we can do
	if (bufferLen == 0)
		return 0;

	//Read all the leading whitespace characters
	int next;
	while ((next = readByte()) != -1 && !isAlphaNumeric(next))
		;

	if (next == -1)
		return 0;
	//Read from the serial stream until the end of the command is reached
	unsigned int index;
	buffer[0] = next;
	index = 1;

	while ((next = readByte()) != -1 && isAlphaNumeric(next) && index < bufferLen) {
		buffer[index] = (byte) next;
		index++;
	}
	return index;
}

//Gets the next byte from the serial stream. Should be used to read all data. If no byte is found within the timeout, then -1 is returned.
int FingerprintRobotCom::readByte() {

	switch (comMode) {
	case COM_MODE_USB: {
		char buf[1];
		if (Serial.readBytes(buf, 1) == 0)
			return -1;
		else {
			return (byte) buf[0];
		}
		break;
	}
	case COM_MODE_ETHERNET: {

		if (!isNetworkingEnabled)
			return -1;

		//Wait at most TIME_OUT for a client to connect.
//		unsigned long endTime = millis() + COM_TIME_OUT;
		EthernetClient client = tcpReturn;
//		while (!(client = server.available()) && endTime > millis())
//			;

		//If a client connected
		if (client) {
			int next = client.read();
			if (next == -1) {
				return -1;
			} else {
				return next;
			}
		} else
			return -1;
		break;
	}
	case COM_MODE_UDP: {

		if (!isNetworkingEnabled)
			return -1;

		//Wait at most TIME_OUT for a client to connect.
		unsigned long endTime = millis() + COM_TIME_OUT;
		int result;
		while ((result = udp.read()) == -1 && endTime > millis())
			;

		return result;
		break;
	}
	}
	return -1;
}

//Sends a string back to the connected device
void FingerprintRobotCom::print(String str) {
	switch (comMode) {
	case COM_MODE_USB: {
		Serial.print(str);
		break;
	}
	case COM_MODE_ETHERNET: {
		tcpReturn.print(str);
		break;
	}
	case COM_MODE_UDP: {
		udpReturn.print(str);
		break;
	}
	}
}
//Simply a print followed by a line break
void FingerprintRobotCom::println(String str) {
	print(str + "\n");
//	print("\n");
}

void FingerprintRobotCom::sendStatus(String commandName, String status) {
	println(commandName + "-" + status);
}

//Parses an int from the currently active input. If there is an error, PARSE_INT_ERROR (0x7fff) is returned.
int FingerprintRobotCom::parseInt() {

	int result = 0;
	int sign = 1;

	//Read all the leading whitespace characters
	int next;
	while ((next = readByte()) != -1 && !isAlphaNumeric(next) && next != '-')
		;

	//If there is a negative sign, record it and advance next
	if (next == '-') {
		sign = -1;
		next = readByte();
	}

	if (next == -1 || !isDigit(next))
		return PARSE_INT_ERROR;
	else
		result = next - '0';

	while ((next = readByte()) != -1 && isDigit(next)) {
		result *= 10;
		result += (next - '0');
	}
	return result * sign;
}

int FingerprintRobotCom::maxCommandLength(const String commands[], int numCommands) {
	int maxLen = 0;
	for (int x = 0; x < numCommands; x++) {
		maxLen = max(maxLen, strlen(commands[x].c_str()));
	}
	return maxLen;
}

IPAddress FingerprintRobotCom::localIP() {
	return Ethernet.localIP();
}
}

