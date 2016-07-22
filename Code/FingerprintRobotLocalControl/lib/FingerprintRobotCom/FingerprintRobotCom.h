/*
 * FingerprintRobotCom.h
 *
 * Created on: Jun 22, 2015
 *   Author: jacob
 */

#ifndef LIBRARIES_FINGERPRINTROBOTCOM_FINGERPRINTROBOTCOM_H_
#define LIBRARIES_FINGERPRINTROBOTCOM_FINGERPRINTROBOTCOM_H_

#include <Arduino.h>
#include <EthernetClient.h>
#include <EthernetServer.h>
#include <EthernetUdp.h>
#include <stdbool.h>
#include <WString.h>

namespace FingerprintRobotComLib {

#define BAD_COMMAND 255
#define EMPTY_COMMAND 254

#define COM_MODE_ETHERNET 0
#define COM_MODE_USB 1
#define COM_MODE_BUTTONS 2
#define COM_MODE_UDP 3
#define COM_MODE_NONE 4

#define PARSE_INT_ERROR 0x7fff
#define MAC_ERROR 1

//DHCP status codes
#define DHCP_START_ERROR 2
#define DHCP_TIMEOUT 3
#define DHCP_SUCCESSFUL 4
#define DHCP_IN_PROGRESS 5
#define NETWORKING_OFF 6

//The port to write to to disable the SD card
#define SD_PORT 4
//The state to write to the SD_PORT
#define SD_STATE HIGH
//The port to write to to enable the Ethernet
#define E_PORT 10
//The state to write to the E_PORT
#define E_STATE LOW
#define TCP_PORT 2424
#define UDP_PORT 2424
#define COM_TIME_OUT 200

//Define the challenge and response authentication keys
#define CHALLENGE String("fingerrobot")
#define RESPONSE_PREFIX String("found")
#define ID_SEPERATOR String(":")
#define RECEIVED String("received")
#define END String("end")
#define SYNTAX_ERROR String("bad-command")

class FingerprintRobotCom {
public:

	/*
	 *Makes a new fingerprint robot communicator with a specified robot type and a specified DHCP timeout. The dhcp timeout is in milliseconds and the default value is 10000.
	 */
	FingerprintRobotCom(String robotType, unsigned long dhcpTimeout = 10000);

	/**
	 * Starts up all communications
	 */
	int begin();
	/**
	 * Tries to get a DHCP lease and returns one of the DHCP status codes.
	 */
	int pollDHCP();
	/**
	 * Resets the DHCP timeout counter so that the program will attempt to obtain a lease even though the previous attempt timed out
	 */
	void retryDHCP();
	/**
	 * Set networking state to either enabled or disabled.
	 */
	void setNetworking(bool state);
	/**
	 * Saves the specified MAC address to EEPROM for use later
	 */
	void saveMAC(byte mac[]);
	/**
	 * Reads the specified MAC address to EEPROM for use later
	 */
	int readMAC(byte mac[]);
	/**
	 * Sends the response to the verify command (something like found:NARFSTR:08:A4:4d:90:00:01:).
	 */
	void sendVerificationResponse();
	/**
	 * Checks to see if there is any data available from any input. Returns true if data is available, false otherwise.
	 */
	bool dataCheck();
	/**
	 * Reads the next command ID
	 */
	byte readCommandID(const String commands[], int numCommands, int maxCommandLength);
	/**
	 * /Decodes a command string (represented as a byte array) by finding the index the the array of commands where the command is located
	 */
	byte decode(byte command[], unsigned int len, const String commands[], int numCommands);
	/*
	 * Determines if a byte array is equal to a string
	 */
	bool arrayEquals(byte a[], unsigned int len, String b);
	/*
	 * Reads bytes into the buffer until a space is reached, or the end of the buffer is reached. Returns the number of characters read in. Writes zeros into the buffer before starting.
	 */
	int readWord(byte buffer[], unsigned int bufferLen);
	/*
	 * Gets the next byte from the serial stream. Should be used to read all data. If no byte is found within the timeout, then -1 is returned.
	 */
	int readByte();
	/*
	 * Sends a string back to the connected device
	 */
	void print(String str);
	/*
	 * Simply a print followed by a line break
	 */
	void println(String str);
	/**
	 * Send a status string of the format <command name>-<status>
	 */
	void sendStatus(String commandName, String status);
	/*
	 * Parses an int from the currently active input. If there is an error, PARSE_INT_ERROR (0x7fff) is returned.
	 */
	int parseInt();
	/**
	 * Returns the length of the biggest command in the command array. This is useful for determining the needed buffer size for reading commands.
	 */
	int maxCommandLength(const String commands[], int numCommands);
	/**
	 * Returns the IP address of the Ethernets shield
	 */
	IPAddress localIP();

private:

	//Ethernet server
	EthernetServer server;
	//The UDP server
	EthernetUDP udp;
	//The client used for sending data over tcp when the verification command was sent over udp.
	EthernetClient udpReturn;
	//The client used for sending data over tcp when the command came over tcp.
	EthernetClient tcpReturn;
	byte comMode;
	String robotType;bool isConnected;bool isNetworkingEnabled;
	unsigned long dhcpTimeout;
	unsigned long dhcpStartTime;
	byte MAC[6];
};
}

#endif /* LIBRARIES_FINGERPRINTROBOTCOM_FINGERPRINTROBOTCOM_H_ */
