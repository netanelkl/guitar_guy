################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 


CPP_SRCS += \
$(GENERAL_DIR)/FXGGeneral.cpp \

OBJS += \
$(OBJ_DIR)/FXGGeneral.o \

CPP_DEPS += \
$(OBJ_DIR)/FXGGeneral.d 

# Each subdirectory must supply rules for building sources it contributes
$(OBJ_DIR)/%.o: $(GENERAL_DIR)/%.cpp
	@echo 'Building file: $<'
	@g++ $(CPP_FLAGS) -I"../vamp-hostsdk"

