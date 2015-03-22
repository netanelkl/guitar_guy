################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 


CPP_SRCS += \
$(WIN_DIR)/wave.cpp \
$(WIN_DIR)/WinHostWrapper.cpp \
$(WIN_DIR)/WinStreamHostWrapper.cpp \
$(WIN_DIR)/main.cpp \
$(WIN_DIR)/VampManager.cpp \

OBJS += \
$(OBJ_DIR)/Wave.o \
$(OBJ_DIR)/WinHostWrapper.o \
$(OBJ_DIR)/WinStreamHostWrapper.o \
$(OBJ_DIR)/main.o \
$(OBJ_DIR)/VampManager.o 

CPP_DEPS += \
$(OBJ_DIR)/Wave.d \
$(OBJ_DIR)/WinHostWrapper.d \
$(OBJ_DIR)/WinStreamHostWrapper.d \
$(OBJ_DIR)/main.d \
$(OBJ_DIR)/VampManager.d 

# Each subdirectory must supply rules for building sources it contributes
$(OBJ_DIR)/%.o: $(WIN_DIR)/%.cpp
	@echo 'Building file: $<'
	@g++ $(CPP_FLAGS) -I"../vamp-hostsdk"

