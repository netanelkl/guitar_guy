################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 

CPP_SRCS += \
$(CHORDINO_DIR)/Chordino.cpp \
$(CHORDINO_DIR)/NNLSBase.cpp \
$(CHORDINO_DIR)/NNLSChroma.cpp \
$(CHORDINO_DIR)/Tuning.cpp \
$(CHORDINO_DIR)/chromamethods.cpp \
$(CHORDINO_DIR)/plugins.cpp \
$(CHORDINO_DIR)/viterbi.cpp \

C_SRCS += \
$(CHORDINO_DIR)/nnls.c 

OBJS += \
$(OBJ_DIR)/$(CHORDINO)/Chordino.o \
$(OBJ_DIR)/$(CHORDINO)/NNLSBase.o \
$(OBJ_DIR)/$(CHORDINO)/NNLSChroma.o \
$(OBJ_DIR)/$(CHORDINO)/Tuning.o \
$(OBJ_DIR)/$(CHORDINO)/chromamethods.o \
$(OBJ_DIR)/$(CHORDINO)/nnls.o \
$(OBJ_DIR)/$(CHORDINO)/plugins.o \
$(OBJ_DIR)/$(CHORDINO)/viterbi.o \

C_DEPS += \
$(OBJ_DIR)\nnls.d 

CPP_DEPS += \
$(OBJ_DIR)/$(CHORDINO)/Chordino.d \
$(OBJ_DIR)/$(CHORDINO)/NNLSBase.d \
$(OBJ_DIR)/$(CHORDINO)/NNLSChroma.d \
$(OBJ_DIR)/$(CHORDINO)/Tuning.d \
$(OBJ_DIR)/$(CHORDINO)/chromamethods.d \
$(OBJ_DIR)/$(CHORDINO)/plugins.d \
$(OBJ_DIR)/$(CHORDINO)/viterbi.d 

# Each subdirectory must supply rules for building sources it contributes
$(OBJ_DIR)/$(CHORDINO)/%.o: $(CHORDINO_DIR)/%.cpp
	@echo 'Building file: $<'
	@g++ $(CPP_FLAGS) -I"../vamp-hostsdk" -I"../vamp-sdk"

$(OBJ_DIR)/$(CHORDINO)/%.o: $(CHORDINO_DIR)/%.c
	@echo 'Building file: $<'
	@gcc $(GCC_FLAGS)
	@echo 'Finished building: $<'


