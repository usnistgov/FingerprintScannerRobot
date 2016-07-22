This Java program allows for easy control of the robot.

1.	Instructions:
	To launch the GUI version, pass in no arguments. To launch the command line version, pass in two arguments. First, the name of the text file which contains the commands. Second, the location of the Arduino to connect to or the autoconnect preference. Acceptable values are a serial port name, an IP address, ethernet, or usb. "ethernet" sets the autoconnect preference to Ethernet and "usb" set the autoconnect preference to USB.
2. Classes:
	2.1	Arduino: represents a robot and is used to control the NAFSTR.
	2.2	ArduinoCom: represents a way of communicating with an Arduino.
	2.3	Command: represents an action that an Arduino can execute.
3. Javadoc: for more documentation, see the Javadoc at /doc/index.html