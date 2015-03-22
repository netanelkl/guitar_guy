################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 

CPP_SRCS += \
$(VAMP_HOSTSDK_DIR)/HostWrapper.cpp \
$(VAMP_HOSTSDK_DIR)/PluginBufferingAdapter.cpp \
$(VAMP_HOSTSDK_DIR)/PluginChannelAdapter.cpp \
$(VAMP_HOSTSDK_DIR)/PluginHostAdapter.cpp \
$(VAMP_HOSTSDK_DIR)/PluginInputDomainAdapter.cpp \
$(VAMP_HOSTSDK_DIR)/PluginLoader.cpp \
$(VAMP_HOSTSDK_DIR)/PluginSummarisingAdapter.cpp \
$(VAMP_HOSTSDK_DIR)/PluginWrapper.cpp \
$(VAMP_HOSTSDK_DIR)/RealTime.cpp \

OBJS += \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/HostWrapper.o \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginBufferingAdapter.o \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginChannelAdapter.o \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginHostAdapter.o \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginInputDomainAdapter.o \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginLoader.o \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginSummarisingAdapter.o \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginWrapper.o \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/RealTime.o \

CPP_DEPS += \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/HostWrapper.d \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginBufferingAdapter.d \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginChannelAdapter.d \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginHostAdapter.d \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginInputDomainAdapter.d \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginLoader.d \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginSummarisingAdapter.d \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/PluginWrapper.d \
$(OBJ_DIR)/$(VAMP_HOSTSDK)/RealTime.d \

# Each subdirectory must supply rules for building sources it contributes
$(OBJ_DIR)/$(VAMP_HOSTSDK)/%.o: $(VAMP_HOSTSDK_DIR)/%.cpp
	@echo 'Building file: $<'
	@g++ $(CPP_FLAGS) -I"../vamp-hostsdk" -I"../vamp-sdk"

$(OBJ_DIR)/$(VAMP_HOSTSDK)/%.o: $(VAMP_HOSTSDK_DIR)/%.c
	@echo 'Building file: $<'
	@gcc $(GCC_FLAGS)
	@echo 'Finished building: $<'


