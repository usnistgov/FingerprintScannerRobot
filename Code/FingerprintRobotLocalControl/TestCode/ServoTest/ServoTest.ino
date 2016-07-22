#include <Servo.h> 

//The position used to initialize the servos
#define INIT_SERVO_POS 1

//The time it takes a servo to move from start pos to end pos, in ms
#define MOVE_TIME 600

//The time the servos will push against the fingerprint scanner
#define WAIT_TIME 1000

//The ports for each servo's control line.
byte ports[] = {
  3,5,6,9};

//The array of all the servos. To get servo i, go to servos[i-1]; servo two is located at servos[1].
Servo servos[4];

//Contains the starting positions for the servos
byte startPos[4] = {
  90,90,100,100};

//Contains the ending positions for the servos
byte endPos[4] = {
  0,0,180,180};

//Represents all fingers up
byte allUp[4] = {
  0,0,0,0};

//Represents all fingers down
byte allDown[4] = {
  1,1,1,1};

void setup() 
{ 
  for(int x=0;x<4;x++){

    //Attach each servo
    servos[x].attach(ports[x]);

    //Send each servo the initialization postion. This seems to make them work better; sometimes they will do nothing unless they receive this.
    servos[x].write(INIT_SERVO_POS);
  }

  //Begin serial communications
  Serial.begin(9600);

  //Put all the servos in the start posotion
  writeFingerState(allUp);
} 


void loop() 
{
  int input;
  do{
    input = Serial.parseInt();
  }
  while(input==0);

  //Convert the input into a byte array representing the requested move
  //An input of 1011 should become the array {1,0,1,1}, which means to move servos 1, 3 and 4.
  byte motion[4];
  for(int x=3;x>=0;x--){
    motion[x] = input%10;
    input/=10;
  }

  //Output the parsed move
  Serial.println("Move: "+getMotionString(motion));

  //Move
  writeFingerState(motion);
  delay(WAIT_TIME);
  writeFingerState(allUp);
}

void writeFingerState(byte state[4]){
  for(int x=0;x<4;x++){
    if(state[x]==1)
      servos[x].write(endPos[x]);
    else
      servos[x].write(startPos[x]);
  }
  delay(MOVE_TIME);
}

String getMotionString(byte motion[]) {
  return "{ "+String(motion[0])+", "+String(motion[1])+", "+String(motion[2])+", "+String(motion[3])+" }";
}







