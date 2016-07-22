#include <SPI.h>

#include <Dhcp.h>
#include <Dns.h>
#include <Ethernet.h>
#include <EthernetClient.h>
#include <EthernetServer.h>
#include <EthernetUdp.h>

#include <Servo.h>

//START Networking Variables ===================================

//The MAC address
byte MAC[] = {0x90, 0xA2, 0xDA, 0x0F, 0x4C, 0xD8};
//The port to write to to disable the SD card
const byte SD_PORT = 4;
//The state to write to the SD_PORT
const byte SD_STATE = HIGH;
//The port to write to to enable the Ethernet
const byte E_PORT = 10;
//The state to write to the E_PORT
const byte E_STATE = LOW;
const int TCP_PORT = 80;
//Ethernet server
EthernetServer server(TCP_PORT);
//The UDP server
EthernetUDP udp;
//The client used for sending data over tcp when the verification command was sent over udp.
EthernetClient udpReturn;
//The client used for sending data over tcp when the command came over tcp.
EthernetClient tcpReturn;
//The port on which to listen for UDP packets
const int UDP_PORT = 80;

//END Networking Variables =====================================

//START Multiplexer Variables ==================================

//The ports for the select pins on the multiplexer
const byte NUM_SELECT_PINS = 4;
//The select pins in the order S0, S1, S2, S3
const byte SELECT_PINS[] = {A1, A2, A3, A4};
//The common IO pin from the multiplexer
const byte IO_PIN = A0;

//END Multiplexer Variables ====================================

//START Servo Variables ========================================

//The number of servos
const byte NUM_SERVOS = 4;
//The ports for each servo's control line.
const byte servoPorts[] = {
  3, 5, 6, 9
};
//The array of all the servos. To get servo i, go to servos[i-1]; servo two is located at servos[1].
Servo servos[NUM_SERVOS];
//The position used to initialize the servos
const byte INIT_SERVO_POS[NUM_SERVOS] = {
  90, 90, 106, 106
};
//The value to pass to writeMicroseconds to relax the servos
const byte RELAX_PULSE = LOW;

//END Servo Variables ========================================

//START Sensor Variables =====================================

//The number of sensors
const byte NUM_SENSORS = 4;
//The ports for each sensor
const byte lightSensorPorts[] = {
  0, 1, 2, 3
};
//The ports for each sensor
const byte forceSensorPorts[] = {
  4, 5, 6, 7
};
//The ports for each sensor
const byte buttonPorts[] = {
  8, 9, 10, 11
};
const byte activateButtonPort = 12;

//END Sensor Variables =======================================

//START Move Type Variables ==================================

//Define move types
const byte POS = 0;
const byte SEN = 1;
//Define the number of move types
const byte NUM_MOVE_TYPES = 2;
//Define an array of string move types in the same order as the constants above
const String MOVE_TYPES[NUM_MOVE_TYPES] = {
  String("pos"), String("sen")
};

//END Move Type Variables ====================================

//START Sensor Type Variables ================================

//Define sensor types
const byte FSR = 0;
const byte LDR = 1;
//Define the number of sensor types
const byte NUM_SENSOR_TYPES = 2;
//Define an array of string sensor types in the same order as the constants above
const String SENSOR_TYPES[NUM_SENSOR_TYPES] = {
  String("f"), String("l")
};

//END Sensor Type Variables ==================================

//START Verification Variables ===============================

//Define the challenge and response authentication keys
const String CHALLENGE = String("fingerrobot");
const String RESPONSE = String("youfoundme");
const String RECEIVED = String("received");
const String SYNTAX_ERROR = String("bad-command");
const String END = String("end");

//END Verification Variables =================================

//START Command Type Variables ===============================

//Define numbers that correspond to commands
const byte VERIFY = 0;
const byte SET = 1;
const byte MOVE = 2;
const byte GET = 3;
const byte RELAX = 4;
const byte HOLD = 5;
//Define the number of commands
const byte NUM_COMMANDS = 6;
//Define an array of string commands in the same order as the constants above
const String COMMANDS[NUM_COMMANDS] = {
  CHALLENGE, String("set"), String("move"), String("get"), String("relax"), String("hold")
};
const byte BAD_COMMAND = -1;
const byte EMPTY_COMMAND = -2;

//END Command Type Variables =================================

//START Move Variables =======================================

//Define a null move
const byte NO_MOVE = -1;
const byte MOVE_TYPE_LOC = 0;
//The move matrix. Each row of the matrix is a move parameter; row 0 is the position 1, row 1 is wait time, and row 2 is position 2. Each column represents a servo.
int moveMatrix[2][NUM_SERVOS][6];
//Holds the move start time
long startTime;
//Holds start positions for the servos
byte startPos[NUM_SERVOS];
const byte NO_DATA = -1;
//Holds a piece of data for each servo during moves. Should be initialized to NO_DATA before every move.
long moveData[NUM_SERVOS];

//END Move Variables =========================================

//START Communication Type Variables =========================

const byte ETHERNET = 0;
const byte USB = 1;
const byte BUTTONS = 2;
const byte UDP = 3;
byte comMode;

//END Communication Type Variables ===========================

//START Communication Variables ==============================

const byte TIME_OUT = 200;

//END Communication Variables ================================

//START Other Variables ======================================

const int PARSE_INT_ERROR = 0x7fff;

//END Other Variables ========================================

//Setup method
void setup()
{

  //Attach and initialize all the servos
  for (int x = 0; x < NUM_SERVOS; x++) {
    //Attach each servo
    servos[x].attach(servoPorts[x]);
    //Send each servo the initialization postion. This seems to make them work better; sometimes they will do nothing unless they receive this.
    servos[x].write(INIT_SERVO_POS[x]);
  }

  //Fill the move matrix with null moves
  for (int x = 0; x < 2; x++) {
    for (int servo = 0; servo < NUM_SERVOS; servo++) {
      moveMatrix[x][servo][MOVE_TYPE_LOC] = NO_MOVE;
    }
  }

  //Set the select pins to output
  for (int x = 0; x < NUM_SELECT_PINS; x++) {
    pinMode(SELECT_PINS[x], OUTPUT);
  }

  //Begin serial communications
  Serial.begin(9600);
  Serial.setTimeout(TIME_OUT);

  //Disable the SD card reader
  pinMode(SD_PORT, SD_STATE);
  pinMode(E_PORT, E_STATE);

  //Begin ethernet communications
  if (Ethernet.begin(MAC) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
  }

  //byte ip[4] = {10,0,34,100};
  //Ethernet.begin(MAC, ip);

  //Print IP address
  //Serial.println(Ethernet.localIP());

  //Start UDP
  udp.begin(UDP_PORT);

  //Start TCP
  server.begin();
  // Serial.println("READY");
}

void loop()
{

  //Check for UDP packets

  boolean ioAvailable = true;
  if ((tcpReturn = server.available())) {
    comMode = ETHERNET;
  } else if (Serial.available() > 0) {
    comMode = USB;
  }  else if (udp.parsePacket() != 0) {
    comMode = UDP;
    udpReturn.connect(udp.remoteIP(), TCP_PORT);
  } else
    ioAvailable = false;

  if (ioAvailable) {

    //Make a buffer to store the incoming command
    //As the challenge will be the longest thing read in, make the buffer the length of the challenge
    byte commandBuffer[CHALLENGE.length()];
    if (readWord(commandBuffer) != 0) {
      //Decode the command
      byte command = decode(commandBuffer, COMMANDS, NUM_COMMANDS);

      //Switch on the command
      switch (command) {
        case VERIFY:
          {
            println(RESPONSE);
            break;
          }
        case SET:
          {
            //Read in the servo number and parse the two moves
            int servoNum = parseInt();
            if (servoNum == PARSE_INT_ERROR || servoNum < 0 || servoNum >= NUM_SERVOS) {
              println(SYNTAX_ERROR);
              return;
            }
            if (!parseMove(servoNum, 0)) {
              println(SYNTAX_ERROR);
              return;
            }
            if (!parseMove(servoNum, 1)) {
              println(SYNTAX_ERROR);
              return;
            };
            println(getReceivedString(SET));
            println(getEndString(SET));
            break;
          }
        case MOVE:
          {
            //Get the number of servos to move
            int numServos = parseInt();
            if (numServos == PARSE_INT_ERROR) {
              println(SYNTAX_ERROR);
              return;
            }

            //If it is zero, move all the servos
            if (numServos == 0)
              numServos = NUM_SERVOS;

            //Make an array that will contain the IDs of the servos to be moved
            byte toMove[numServos];

            //If some servos will not move, put only the ones that should move into the array
            if (numServos != NUM_SERVOS) {
              for (int x = 0; x < numServos; x++) {
                int servoToMove = parseInt();
                if (servoToMove == PARSE_INT_ERROR) {
                  println(SYNTAX_ERROR);
                  return;
                }
                toMove[x] = servoToMove;
              }
            }
            else {

              //Put all the servos into the array
              for (int x = 0; x < numServos; x++) {
                toMove[x] = x;
              }
            }
            println(getReceivedString(MOVE));

            //For every move stage
            for (int x = 0; x < 2; x++) {

              //Record the start time
              startTime = millis();
              //Record the starting position
              for (int servo = 0; servo < numServos; servo++) {
                startPos[toMove[servo]] = servos[toMove[servo]].read();
                moveData[toMove[servo]] = NO_DATA;
              }
              byte numDone = 0;
              boolean done[numServos];
              for (int x = 0; x < numServos; x++) {
                done[x] = false;
              }
              do {

                //Update each servo and record the number of servos that are done
                for (int servo = 0; servo < numServos; servo++) {

                  //Prevents updating of moves that are done
                  if (!done[servo] && updateMove(toMove[servo], x)) {
                    //Increase the count and mark the move as done
                    numDone++;
                    done[servo] = true;
                  }
                }
              }
              while (numDone != numServos);
            }
            println(getEndString(MOVE));
            break;
          }
        case GET:
          {
            println(getReceivedString(GET));
            int servo = parseInt();
            if (servo == PARSE_INT_ERROR) {
              println(SYNTAX_ERROR);
              return;
            }
            byte sensorBuf[1];
            if (readWord(sensorBuf) == 0) {
              println(SYNTAX_ERROR);
              return;
            }
            int sensor = decode(sensorBuf, SENSOR_TYPES, NUM_SENSOR_TYPES);
            if (sensor == BAD_COMMAND) {
              println(SYNTAX_ERROR);
              return;
            }
            if (sensor == LDR) {
              println(String(readSensor(lightSensorPorts[servo])));
            } else if (sensor == FSR) {
              println(String(readSensor(forceSensorPorts[servo])));
            }
            println(getEndString(GET));
            break;
          }
        case RELAX:
          {
            println(getReceivedString(RELAX));
            //Attach and initialize all the servos
            for (int x = 0; x < NUM_SERVOS; x++) {
              servos[x].detach();
              digitalWrite(servoPorts[x], RELAX_PULSE);
              // .writeMicroseconds(RELAX_PULSE);
            }
            println(getEndString(RELAX));
            break;
          }
        case HOLD:
          {
            println(getReceivedString(HOLD));
            //Attach and initialize all the servos
            for (int x = 0; x < NUM_SERVOS; x++) {
              servos[x].attach(servoPorts[x]);
              servos[x].write(INIT_SERVO_POS[x]);
            }
            println(getEndString(HOLD));
            break;
          }
        case BAD_COMMAND:
          {
            println(SYNTAX_ERROR);
          }
      }
    }
  }

  //Stop the UDP connection
  if (comMode == UDP) {
    udpReturn.stop();
  }
}


//Decodes a command string (represented as a byte array) by finding the index the the array of commands where the command is located.
byte decode(byte command[], const String commands[], int len) {

  for (int x = 0; x < len; x++) {
    if (arrayEquals(command, commands[x]))
      return x;
  }
  return BAD_COMMAND;
}

//Deterines if a byte array is equal to a string
boolean arrayEquals(byte a[], String b) {
  for (int x = 0; x < b.length(); x++) {
    if (a[x] != (byte)b.charAt(x))
      return false;
  }
  return true;
}

//Reads bytes into the buffer until a space is reached
int readWord(byte buffer[]) {

  //Read all the leading whitespace characters
  int next;
  while ((next = readByte()) != -1 && !isAlphaNumeric(next));

  if (next == -1)
    return 0;
  //Read from the serial stream until the end of the command is reached
  byte index;
  if (next == ' ')
    index = 0;
  else {
    buffer[0] = next;
    index  = 1;
  }
  while ((next = readByte()) != -1 && isAlphaNumeric(next)) {
    buffer[index] = (byte)next;
    index++;
  }
  return index;
}

//Gets the next byte from the serial stream. Should be used to read all data. If no byte is found within the timeout, then -1 is returned.
int readByte() {

  switch (comMode) {
    case USB:
      {
        char buf[1];
        if (Serial.readBytes(buf, 1) == 0)
          return -1;
        else {
          return (byte)buf[0];
        }
        break;
      }
    case ETHERNET:
      {

        //Wait at most TIME_OUT for a client to connect.
        long endTime = millis() + TIME_OUT;
        EthernetClient client;
        while (!(client = server.available()) && endTime > millis())
          ;

        //If a client connected
        if (client) {
          int next = client.read();
          if (next == -1)
            return -1;
          else
            return next;
        } else
          return -1;
        break;
      }
    case UDP:
      {
        //Wait at most TIME_OUT for a client to connect.
        long endTime = millis() + TIME_OUT;
        int result;
        while ((result = udp.read()) == -1 && endTime > millis())
          ;

        return result;
        break;
      }
  }
}

//Sends a string back to the connected device
void print(String str) {
  switch (comMode) {
    case USB:
      {
        Serial.print(str);
        break;
      }
    case ETHERNET:
      {
        tcpReturn.print(str);
      }
    case UDP:
      {
        udpReturn.print(str);
      }
  }
}
//Simply a print followed by a line break
void println(String str) {
  print(str);
  print("\n");
}

//Parses an int from the currently active input. If there is an error, 0x7fff is returned.
int parseInt() {

  int result = 0;
  int sign = 1;

  //Read all the leading whitespace characters
  int next;
  while ((next = readByte()) != -1 && !isAlphaNumeric(next) && next != '-');

  //If there is a negative sign, record it and advance next
  if (next == '-') {
    sign = -1;
    next = readByte();
  }

  if (next == -1 || isAlpha(next))
    return PARSE_INT_ERROR;
  else
    result = next - '0';

  while ((next = readByte()) != -1 && isNumeric(next)) {
    result *= 10;
    result += (next - '0');
  }
  return result * sign;
}

//Parses a move from the serial stream and stores the move into the move matrix
boolean parseMove(byte servoNum, byte moveNum) {

  //Read in the move type, a three character string
  byte moveBuffer[3];
  if (readWord(moveBuffer) == 0)
    return false;

  //Decode the move type
  byte moveType = decode(moveBuffer, MOVE_TYPES, NUM_MOVE_TYPES);
  if (moveType == BAD_COMMAND)
    return false;

  //Enter the move type into the move matrix
  moveMatrix[moveNum][servoNum][MOVE_TYPE_LOC] = moveType;

  //Determine the number of parameters
  byte numParams;
  if (moveType == POS)
    numParams = 2;
  else
    numParams = 5;

  //Load the parameters into the move matrix
  for (byte x = 1; x < 1 + numParams; x++) {
    int val = parseInt();
    if (val == PARSE_INT_ERROR) {
      return false;
    }
    moveMatrix[moveNum][servoNum][x] = val;
  }
  return true;
}

//Updates the move for a given servo and move num. Returns true if the move is complete and false otherwise.
boolean updateMove(byte servoNum, byte moveNum) {

  //Get the move type
  byte moveType = moveMatrix[moveNum][servoNum][MOVE_TYPE_LOC];

  //If it is a position move
  if (moveType == POS) {
    if (startTime + moveMatrix[moveNum][servoNum][2] < millis())
      return true;
    else
      servos[servoNum].write(moveMatrix[moveNum][servoNum][1]);
  }
  else {
    if (moveData[servoNum] == NO_DATA) {
      int reading = readSensor(lightSensorPorts[servoNum]);
      if (reading >= moveMatrix[moveNum][servoNum][2] && reading <= moveMatrix[moveNum][servoNum][3]) {
        moveData[servoNum] = millis();
      } else if ((moveMatrix[moveNum][servoNum][1] > 0 && servos[servoNum].read() > moveMatrix[moveNum][servoNum][4]) || (moveMatrix[moveNum][servoNum][1] <= 0 && servos[servoNum].read() < moveMatrix[moveNum][servoNum][4])) {
        moveData[servoNum] = millis();
      } else {
        double elapsedTime = (millis() - startTime) / 1000.0;
        int curTarget = round(startPos[servoNum] + elapsedTime * moveMatrix[moveNum][servoNum][1]);
        servos[servoNum].write(curTarget);
      }
    }
    else {
      if (moveData[servoNum] + moveMatrix[moveNum][servoNum][5] < millis()) {
        int reading = readSensor(lightSensorPorts[servoNum]);
        if (reading >= moveMatrix[moveNum][servoNum][2] && reading <= moveMatrix[moveNum][servoNum][3])
          println("finger-" + String(servoNum) + "-successful-" + String(readSensor(forceSensorPorts[servoNum])));
        else
          println("finger-" + String(servoNum) + "-failed-" + String(readSensor(forceSensorPorts[servoNum])));
        return true;
      }
    }
  }
  return false;
}

//Returns true if the character represented by the specified byte is a number or a letter
boolean isAlphaNumeric(byte a) {
  return isAlpha(a) || isNumeric(a);
}

boolean isAlpha(byte a) {
  return (a >= (byte)'A' && a <= (byte)'Z') || (a >= (byte)'a' && a <= (byte)'z');
}

boolean isNumeric(byte a) {
  return (a >= (byte)'0' && a <= (byte)'9');
}

//Returns the command name with -received on the end
String getReceivedString(byte commandNum) {
  return COMMANDS[commandNum] + "-" + RECEIVED;
}

//Returns the command name with -end on the end
String getEndString(byte commandNum) {
  return COMMANDS[commandNum] + "-" + END;
}

//Reads a sensor through the specified pin on the multiplexer
int readSensor(byte pin) {

  //Set the value of the select pins where S0 is the LSB and S3 is the MSB
  for (byte x = 0; x < NUM_SELECT_PINS; x++) {
    if (pin % 2 == 0)
      digitalWrite(SELECT_PINS[x], LOW);
    else
      digitalWrite(SELECT_PINS[x], HIGH);
    pin /= 2;
  }
  delay(1);
  return analogRead(IO_PIN);
}

//Gets the amount of free RAM
int freeRam () {
  extern int __heap_start, *__brkval;
  int v;
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval);
}
