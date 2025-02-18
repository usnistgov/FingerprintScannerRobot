###################
Program Development
###################

There are two programs associated with each robot: (1) a Java program that runs on a computer and controls the robot and (2) a C++ program that runs on the robot. The Java program works with both a Graphical User Interface (GUI) and the command line. With the GUI, the user specifies the actions for the robot to execute, and then connects to the robot and executes the actions. With the command line, the user can supply a text file of commands for the robot to execute, and the program will send the commands to the robot. The C++ program on the robot receives all the commands and executes them. It also responds to any button presses.

Java Robot Control Program
==========================

The Java program has 3 main parts:

#. The back end, which handles all communication with the robot.
#. The GUI, which provides a simple way to control the robot.
#. The command line interface, which lets the user send a list of commands to the robot.

This guide will explain each of the three parts.

Back End
--------

The back end consists of the robot package, which contains four main classes:

#. ``Robot``
#. ``RobotCom``
#. ``RobotInfo``
#. ``Command``

``Robot`` is an abstract class, and it represents any type of robot, either a NAFSTR or a NARFSTR. It currently has one subclass, ``NARFSTR``. The current version of the program does not support the NAFSTR robot; to control a NAFSTR, the older version must be used. ``NARFSTR`` has one constructor, which takes a ``RobotCom`` object. A ``RobotCom`` is a channel for communicating with the robot, and has methods for reading and writing data. ``RobotCom`` has two subclasses: ``SerialRobotCom``\ , which represents a connection to a robot over a serial port (USB), and ``NetworkRobotCom``\ , which represents a connection to a robot over a network. In order to make an instance of either class, a ``RobotInfo`` object is required. A ``RobotInfo`` object describes the type of robot, the location of the robot (an IP address and a serial port), and the robot's MAC address, which is used as an ID.

In order to easily find robots, the ``Robot`` class provides three static methods:

#. ``findSerialRobots()``\ : finds all the robots connected over USB by checking every USB port.
#. ``findNetworkedRobots()``\ : finds all the robots connected over the network by sending out a UDP broadcast and listening for responses.
#. ``findRobots()``\ : finds all the robots over both USB and the network.

In order to execute commands on a robot, the ``Robot`` class provides an ``executeCommand(Command c)`` method, and an ``executeCommands(List<Command> commands)`` method. Both methods send commands to the robot, and wait for execution to finish. A ``Command`` object consists of two components: a String representing the command name, and a list of Objects representing the parameters.

For more documentation, see the Javadoc, in ``Code/FingerprintRobotControl/doc``\ .

GUI
---

The code for the GUI is in three packages: ``view``\ , ``view.configurator``\ , and ``view.robotpanel``\ . The program's GUI is an instance of ``view.RobotController``\ . The ``RobotController`` class has two main components: a ``view.robotpanel.RobotConnectionPanel``\ , which manages the connection with the robot, and a ``view.robotpanel.CommandEditorPanel``\ , which allows the user to edit the commands being sent to the robot.

Command Line
------------

The code for the command line interface is in ``main.FingerprintRobotControl``\ . When ``FingerprintRobotControl`` is executed with no arguments, it automatically creates an instance of ``view.RobotController``\ , creating the GUI. If there are arguments, it attempts to use them. The argument format is: ``<robot type> <file name> <connection location>``\ . The robot type should be either NAFSTR of NARFSTR. (NAFSTR is not currently supported, but will be soon.) The file name should be an absolute or relative path to the file of commands which the program is to execute. The file format is a text file with one command per line. Finally, the connection location could be one of four things: "ethernet", "usb", an IP address, or a serial port. If it is "ethernet" or "usb", to program will search for robots connected over the specified channel, and pick the first one.

C++ Local Control Program
=========================

Development Environment
-----------------------

The program was developed in Eclipse using the the Arduino Eclipse Extensions version 2.4.201506210212 by Jan Baeyens (http://www.baeyens.it/eclipse/), with the Arduino libraries version 1.6.5. The Robot's program consists of two parts: C++ libraries which are common to both robots (the libraries are currently only used with the NARFSTR, but should be used on the NAFSTR), and a ``.ino`` file which contains the main program for the robot. The ``FingerprintRobotCom`` library is stored in ``Code/FingerprintRobotLocalControl/lib``\ . In order to make the Arduino plugin include this library (and other libraries which are stored in the lib folder) in the build path, the "Private Library Path" in the Arduino Plugin Preferences must be set to ``Code/FingerprintRobotLocalControl/lib``\ .

Libraries
---------

The code needs several libraries in order to compile. First, it needs the ``FingperintRobotCom`` library, which handles communication between the robot and the computer. Second, it needs the ``EthernetNonBlocking`` library, which is a modified version of the standard Arduino Ethernet Library. I modified the standard library to make the DHCP functions non-blocking. This significantly improves responsiveness; with the standard blocking DHCP functions, the robot would freeze for 60 seconds if no Ethernet cable was plugged in. Both programs also need the following standard Arduino libraries:

#. ``EEPROM``
#. ``SPI``

Each of the NAFSTR and NARFSTR programs also require some specific libraries:

NAFSTR

  #. ``Servo``\ : standard Arduino library.

NARFSTR

  #. ``Arduino-RGB-Tools``\ : an open-source library for controlling RGB LEDs. It is on github (https://github.com/joushx/Arduino-RGB-Tools), stored in the git repository as a submodule, and licensed under The MIT License (MIT).
  #. ``RGBConverter``\ :  an open-source library for converting between RGB and HSV. It is on github (https://github.com/ratkins/RGBConverter), stored in the git repository as a submodule, and its license says that you can do whatever you want with it.
  #. ``VNH5019Driver``\ : a library I wrote for controlling the VNH5019 driver chip.

NARFSTR
-------

The NARFSTR control program is located at ``Code/FingerprintRobotLocalControl/NARFSTRLocalControl``\ . Its main file is ``NARFSTRLocalControl.ino``\ .

NAFSTR
------

The NAFSTR control program is located at ``Code/FingerprintRobotLocalControl/NAFSTRLocalControl``\ . Its main file is ``NAFSTRLocalControl.ino``\ . The NAFSTR program is old, and so does not use either the ``FingerprintRobotCom`` library or the ``EthernetNonBlocking`` library.

Development Information
-----------------------

The ``FingerpintRobotCom`` library attempts to read a MAC address from the Arduino's EEPROM on startup. If no MAC address is found, it will not work. In order to load a MAC address into EEPROM for the first time, use the ``MACUploader`` program located at ``Code/FingerprintRobotLocalControl/MACUploader``\ .

``FingerprintRobotCom`` Library
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``FingerprintRobotCom`` library header file (located at ``Code/FingerprintRobotLocalControl/lib/FingerprintRobotCom/FingerprintRobotCom.h``) contains comments which explain all the important functions.
