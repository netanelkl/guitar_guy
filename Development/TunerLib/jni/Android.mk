	LOCAL_PATH		:= $(call my-dir)
	TOP_DIR := $(call my-dir)
   	FFTW_PATH := $(LOCAL_PATH)/fftw3/project/jni
   	
   	BUILD_FFTW := 0
   	
   	ifeq ($(BUILD_FFTW),1)
		include $(FFTW_PATH)/Android.mk
	else
		include $(NDK_MODULE_PATH)/fftw3/Android.mk 
	endif
   	# For using the building the fftw3 lib, uncomment this 
	
	
	
	# Chordlib
	include $(CLEAR_VARS) 
	LOCAL_PATH		:= $(TOP_DIR)
	FILE_LIST := $(wildcard $(LOCAL_PATH)/JNIInterface/*.cpp)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/Chordlib/AndroidHost/*.cpp)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/Chordlib/chordino/*.cpp)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/Chordlib/chordino/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/Chordlib/General/*.cpp)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/Chordlib/vamp-hostsdk/*.cpp)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/Chordlib/vamp-sdk/*.cpp)
	LOCAL_SRC_FILES := $(FILE_LIST:$(LOCAL_PATH)/%=%)
   	LOCAL_C_INCLUDES := 				\
   		$(LOCAL_PATH)/Chordlib 			\
   		$(LOCAL_PATH)/../../ 			\
   		$(LOCAL_PATH)/Chordlib/General	\
   		$(FFTW_PATH)					\
        $(FFTW_PATH)/api 				\
 	
 	LOCAL_SHARED_LIBRARIES := f
 	LOCAL_SHORT_COMMANDS := false
   	LOCAL_MODULE    := Chordlib
   	LOCAL_LDLIBS    := -llog
   	ifeq ($(TARGET_ARCH),x86)
   	else
	   	LOCAL_ARM_MODE := arm
   	endif
   	NDK_LOG := 0
   	LOCAL_CFLAGS := -DBOOST_EXCEPTION_DISABLE -DHAVE_FFTW3 -DANDROID_OS -D_STLP_NO_EXCEPTIONS -DOS_ANDROID -D_STLP_USE_SIMPLE_NODE_ALLOC
   	ifeq ($(NDK-DEBUG),0)
   		LOCAL_CFLAGS += -fdata-sections -ffunction-sections -g
   		LOCAL_LDFLAGS := -Wl,--gc-sections -Wl,--dynamic-list=$(LOCAL_PATH)/JNIInterface/symbols.h
   	endif
   	include $(BUILD_SHARED_LIBRARY)
