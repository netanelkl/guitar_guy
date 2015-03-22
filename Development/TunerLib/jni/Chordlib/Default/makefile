################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := del /S
VAMP_SDK_LIBS_DIR = ../../../../VampSdk
PLUGIN_LIBRARY_NAME = nnls-chroma
PLUGIN_EXT_LIB = .dll
PLUGIN_EXT_EXE = .exe 
CHORDINO = chordino
WIN		 = WinHost
VAMP_HOSTSDK = Vamp-HostSdk
VAMP_SDK = Vamp-Sdk
GENERAL = General
CHORDINO_DIR = ../$(CHORDINO)
OBJ_DIR_STRIPPED = obj
OBJ_DIR = ./$(OBJ_DIR_STRIPPED)
WIN_DIR		 = ../$(WIN)
GENERAL_DIR	   = ../$(GENERAL)
VAMP_HOSTSDK_DIR = ../$(VAMP_HOSTSDK)
VAMP_SDK_DIR = ../$(VAMP_SDK)
PLUGIN_DIR = ./lib
PLUGIN_DIR_EXE = ./bin
PLUING_LIB_FILENAME = $(PLUGIN_LIBRARY_NAME)$(PLUGIN_EXT_LIB)
PLUING_EXE_FILENAME = $(PLUGIN_LIBRARY_NAME)$(PLUGIN_EXT_EXE)
PLUING_VAMP_EXE_FILENAME = $(PLUGIN_LIBRARY_NAME)_VAMP$(PLUGIN_EXT_EXE)

PLUGIN_LIB = $(PLUGIN_DIR)/$(PLUING_LIB_FILENAME)
PLUGIN_EXE = $(PLUGIN_DIR_EXE)/$(PLUING_EXE_FILENAME)
PLUGIN_EXE_VAMP = $(PLUGIN_DIR_EXE)/$(PLUING_VAMP_EXE_FILENAME)
LDFLAGS_LIB = -shared 
OPT_FLAGS = -O0 -g
#VAMP_VERSION_FLAG = -DVAMPMANAGER 
COMMON_INCLUDES = -I"../" -I"../General/" -I"../../../../"
GENERAL_COMPILER_FLAGS = -DHAVE_FFTW3 -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
GCC_FLAGS = -c $(OPT_FLAGS)  $(VAMP_VERSION_FLAG) $(COMMON_INCLUDES) $(GENERAL_COMPILER_FLAGS) 
CPP_FLAGS =  -fno-exceptions $(GCC_FLAGS)
LDFLAGS = -fno-exceptions $(OPT_FLAGS) -static-libgcc -static-libstdc++ -Wl,-soname=$(PLUGIN) -Wl,--version-script=vamp-plugin.map 

# All of the sources participating in the build are defined here
-include sources.mk
-include $(CHORDINO)/subdir.mk
-include $(WIN)/subdir.mk
-include $(VAMP_HOSTSDK)/subdir.mk
-include $(GENERAL)/subdir.mk
-include $(VAMP_SDK)/subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C++_DEPS)),)
-include $(C++_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
ifneq ($(strip $(CC_DEPS)),)
-include $(CC_DEPS)
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
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: main_build

# Tool invocations

clear_bin:$(OBJS)
#-$(RM) lib\$(PLUGIN_LIBRARY_NAME)$(PLUGIN_EXT_LIB)
#-$(RM) bin\$(PLUGIN_LIBRARY_NAME)$(PLUGIN_EXT_EXE)
	
pre-build : clear_bin

main_build : pre-build
	@$(MAKE) --no-print-directory both_targets
	
both_targets:  $(PLUGIN_LIB) $(PLUGIN_EXE) #$(PLUGIN_EXE_VAMP)

$(PLUGIN_LIB): $(OBJS)
	@echo 'Building target: $@'
	   @$(CXX) -o $@ $^ $(LDFLAGS_LIB) $(LDFLAGS)
	@echo ' '

$(PLUGIN_EXE): $(OBJS)
	@echo 'Building target: $@'
	   	@$(CXX) -o $@ $^ $(LDFLAGS)
	@echo ' '
	
#$(PLUGIN_EXE_VAMP): $(OBJS)
#	@echo 'Building target: $@'
#	   	$(CXX) -o $@ $^ $(LDFLAGS)
#	@echo ' '


# Other Targets
clean:
	-$(RM) lib\$(PLUGIN_LIB)
	-$(RM) bin\$(PLUGIN_EXE)
	-$(RM) bin\$(PLUGIN_EXE_VAMP)
	-$(RM) $(OBJ_DIR_STRIPPED)\*.o
	-$(RM) $(OBJ_DIR_STRIPPED)\chordino\*.o
	-$(RM) $(OBJ_DIR_STRIPPED)\vamp-hostsdk\*.o
	-$(RM) $(OBJ_DIR_STRIPPED)\vamp-sdk\*.o
	-$(RM) $(OBJ_DIR_STRIPPED)\*.d
	-$(RM) $(OBJ_DIR_STRIPPED)\chordino\*.d
	-$(RM) $(OBJ_DIR_STRIPPED)\vamp-hostsdk\*.d
	-$(RM) $(OBJ_DIR_STRIPPED)\vamp-sdk\*.d
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets
