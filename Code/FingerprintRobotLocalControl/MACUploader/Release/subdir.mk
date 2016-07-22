################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../.ino.cpp 

INO_SRCS += \
../MACUploader.ino 

INO_DEPS += \
./MACUploader.ino.d 

CPP_DEPS += \
./.ino.cpp.d 

LINK_OBJ += \
./.ino.cpp.o 


# Each subdirectory must supply rules for building sources it contributes
.ino.cpp.o: ../.ino.cpp
	@echo 'Building file: $<'
	@echo 'Starting C++ compile'
	"/home/jacob/arduino-1.6.5/hardware/tools/avr/bin/avr-g++" -c -g -Os -fno-exceptions -ffunction-sections -fdata-sections -fno-threadsafe-statics -MMD -mmcu=atmega328p -DF_CPU=16000000L -DARDUINO=10605 -DARDUINO_AVR_UNO -DARDUINO_ARCH_AVR     -I"/home/jacob/arduino-1.6.5/hardware/arduino/avr/cores/arduino" -I"/home/jacob/arduino-1.6.5/hardware/arduino/avr/variants/standard" -I"/home/jacob/arduino-1.6.5/hardware/arduino/avr/libraries/EEPROM" -I"/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/lib/EthernetNonBlocking" -I"/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/lib/EthernetNonBlocking/utility" -I"/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/lib/FingerprintRobotCom" -I"/home/jacob/arduino-1.6.5/hardware/arduino/avr/libraries/SPI" -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -D__IN_ECLIPSE__=1 -x c++ "$<"  -o  "$@"   -Wall
	@echo 'Finished building: $<'
	@echo ' '

MACUploader.o: ../MACUploader.ino
	@echo 'Building file: $<'
	@echo 'Starting C++ compile'
	"/home/jacob/arduino-1.6.5/hardware/tools/avr/bin/avr-g++" -c -g -Os -fno-exceptions -ffunction-sections -fdata-sections -fno-threadsafe-statics -MMD -mmcu=atmega328p -DF_CPU=16000000L -DARDUINO=10605 -DARDUINO_AVR_UNO -DARDUINO_ARCH_AVR     -I"/home/jacob/arduino-1.6.5/hardware/arduino/avr/cores/arduino" -I"/home/jacob/arduino-1.6.5/hardware/arduino/avr/variants/standard" -I"/home/jacob/arduino-1.6.5/hardware/arduino/avr/libraries/EEPROM" -I"/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/lib/EthernetNonBlocking" -I"/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/lib/EthernetNonBlocking/utility" -I"/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/lib/FingerprintRobotCom" -I"/home/jacob/arduino-1.6.5/hardware/arduino/avr/libraries/SPI" -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -D__IN_ECLIPSE__=1 -x c++ "$<"  -o  "$@"   -Wall
	@echo 'Finished building: $<'
	@echo ' '


