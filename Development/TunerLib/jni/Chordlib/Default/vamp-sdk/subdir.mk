################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 

CPP_SRCS += \
$(VAMP_SDK_DIR)/FFT.cpp \
$(VAMP_SDK_DIR)/FFTImpl.cpp \
$(VAMP_SDK_DIR)/PluginAdapter.cpp \
$(VAMP_SDK_DIR)/RealTime.cpp \

OBJS += \
$(OBJ_DIR)/$(VAMP_SDK)/FFT.o \
$(OBJ_DIR)/$(VAMP_SDK)/FFTImpl.o \
$(OBJ_DIR)/$(VAMP_SDK)/PluginAdapter.o \
$(OBJ_DIR)/$(VAMP_SDK)/RealTime.o \

CPP_DEPS += \
$(OBJ_DIR)/$(VAMP_SDK)/FFT.d \
$(OBJ_DIR)/$(VAMP_SDK)/FFTImpl.d \
$(OBJ_DIR)/$(VAMP_SDK)/PluginAdapter.d \
$(OBJ_DIR)/$(VAMP_SDK)/RealTime.d \

# Each subdirectory must supply rules for building sources it contributes
$(OBJ_DIR)/$(VAMP_SDK)/%.o: $(VAMP_SDK_DIR)/%.cpp
	@echo 'Building file: $<'
	@g++ $(CPP_FLAGS) -I"../vamp-hostsdk" -I"../vamp-sdk"

$(OBJ_DIR)/$(VAMP_SDK)/%.o: $(VAMP_SDK_DIR)/%.c
	@echo 'Building file: $<'
	@gcc $(GCC_FLAGS)
	@echo 'Finished building: $<'


