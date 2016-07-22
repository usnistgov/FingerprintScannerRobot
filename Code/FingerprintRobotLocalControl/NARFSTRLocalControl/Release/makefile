################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include subdir.mk
-include arduino/core/subdir.mk
-include Libraries/VNH5019Driver/subdir.mk
-include Libraries/Servo/src/sam/subdir.mk
-include Libraries/Servo/src/avr/subdir.mk
-include Libraries/SPI/subdir.mk
-include Libraries/RGBConverter/subdir.mk
-include Libraries/FingerprintRobotCom/subdir.mk
-include Libraries/EthernetNonBlocking/utility/subdir.mk
-include Libraries/EthernetNonBlocking/subdir.mk
-include Libraries/Arduino-RGB-Tools/RGBTools/examples/subdir.mk
-include Libraries/Arduino-RGB-Tools/RGBTools/subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C++_DEPS)),)
-include $(C++_DEPS)
endif
ifneq ($(strip $(ASM_DEPS)),)
-include $(ASM_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
ifneq ($(strip $(CC_DEPS)),)
-include $(CC_DEPS)
endif
ifneq ($(strip $(INO_DEPS)),)
-include $(INO_DEPS)
endif
ifneq ($(strip $(CPP_DEPS)),)
-include $(CPP_DEPS)
endif
ifneq ($(strip $(CXX_DEPS)),)
-include $(CXX_DEPS)
endif
ifneq ($(strip $(C_UPPER_DEPS)),)
-include $(C_UPPER_DEPS)
endif
ifneq ($(strip $(S_UPPER_DEPS)),)
-include $(S_UPPER_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 
AR += \
arduino.ar \

ELF += \
NARFSTRLocalControl.elf \

EEP += \
NARFSTRLocalControl.eep \

FLASH_IMAGE += \
NARFSTRLocalControl.hex \


# All Target
all: NARFSTRLocalControl

# Tool invocations
NARFSTRLocalControl: $(FLASH_IMAGE)
	@echo 'Building target: $@'
	@echo 'Printing size:'
	"/home/jacob/arduino-1.6.5/hardware/tools/avr/bin/avr-size" -A "/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/NARFSTRLocalControl/Release/NARFSTRLocalControl.elf"
	@echo 'Finished building target: $@'
	@echo ' '

arduino.ar: $(AR_OBJ)
	@echo 'Starting archiver'
	"/home/jacob/arduino-1.6.5/hardware/tools/avr/bin/avr-ar" rcs  "/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/NARFSTRLocalControl/Release/arduino.ar"   $(AR_OBJ)
	@echo 'Finished building: $@'
	@echo ' '

NARFSTRLocalControl.elf: $(LINK_OBJ) $(AR)
	@echo 'Starting combiner'
	"/home/jacob/arduino-1.6.5/hardware/tools/avr/bin/avr-gcc" -Os -Wl,--gc-sections -mmcu=atmega328p  -o "/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/NARFSTRLocalControl/Release/NARFSTRLocalControl.elf"   $(LINK_OBJ) $(AR)  "/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/NARFSTRLocalControl/Release/arduino.ar" "-L/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/NARFSTRLocalControl/Release" -lm
	@echo 'Finished building: $@'
	@echo ' '

NARFSTRLocalControl.eep: $(ELF)
	@echo 'Create eeprom image'
	"/home/jacob/arduino-1.6.5/hardware/tools/avr/bin/avr-objcopy" -O ihex -j .eeprom --set-section-flags=.eeprom=alloc,load --no-change-warnings --change-section-lma .eeprom=0  "/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/NARFSTRLocalControl/Release/NARFSTRLocalControl.elf" "/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/NARFSTRLocalControl/Release/NARFSTRLocalControl.eep"
	@echo 'Finished building: $@'
	@echo ' '

NARFSTRLocalControl.hex: $(EEP)
	@echo 'Create Flash image (ihex format)'
	"/home/jacob/arduino-1.6.5/hardware/tools/avr/bin/avr-objcopy" -O ihex -R .eeprom  "/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/NARFSTRLocalControl/Release/NARFSTRLocalControl.elf" "/home/jacob/git/nafstr/Code/FingerprintRobotLocalControl/NARFSTRLocalControl/Release/NARFSTRLocalControl.hex"
	@echo 'Finished building: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(C_DEPS)$(AR)$(FLASH_IMAGE)$(EEP)$(CXX_DEPS)$(S_UPPER_DEPS)$(ELF)$(C++_DEPS)$(ASM_DEPS)$(CC_DEPS)$(INO_DEPS)$(AR_OBJ)$(CPP_DEPS)$(LINK_OBJ)$(C_UPPER_DEPS)$(SIZEDUMMY) NARFSTRLocalControl
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets
