int sensorPin = A0;    // select the input pin for the potentiometer

void setup() {
  //pinMode(sensorPin, INPUT);
  Serial.begin(9600);
}

void loop() {

  // read the value from the sensor:
  int sensorValue = analogRead(sensorPin);

  String color;
  if(sensorValue<500)
    color = "off";
  else if (sensorValue>570)
    color = "red";
  else
    color="green";
  Serial.println(color+"\t\t"+String(sensorValue)); 
}


