#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_PWMServoDriver.h"

// Create the motor shield object with the default I2C address
Adafruit_MotorShield AFMS = Adafruit_MotorShield();
// Or, create it with a different I2C address (say for stacking)
// Adafruit_MotorShield AFMS = Adafruit_MotorShield(0x61);

// Select which 'port' M1, M2, M3 or M4. In this case, M1
//Adafruit_DCMotor *motor1 = AFMS.getMotor(1);
Adafruit_DCMotor *motor2 = AFMS.getMotor(2);
// You can also make another motor on port M2
//Adafruit_DCMotor *myOtherMotor = AFMS.getMotor(2);

void setup() {
  AFMS.begin();  // create with the default frequency 1.6KHz
  //AFMS.begin(1000);  // OR with a different frequency, say 1KHz

  // Set the speed to start, from 0 (off) to 255 (max speed)
  //  motor1->setSpeed(100);
  motor2->setSpeed(200);
  //  motor1->run(FORWARD);
  //  motor2->run(FORWARD);
  //  delay(4000);
  Serial.begin(9600);
  Serial.println("Start!");
  motor2->run(FORWARD);
  delay(4000);
}

void loop() {

  Serial.println("BACK");
  //  motor1->run(BACKWARD);
  motor2->run(BACKWARD);
  while (analogRead(A0) < 200);
  //  motor1->run(FORWARD);
  motor2->run(FORWARD);
  delay(5000);
//  while (analogRead(A0) < 200);
}
