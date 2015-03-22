	LOCAL_PATH		:= $(call my-dir)
	include $(CLEAR_VARS)
	
	FILE_LIST := $(wildcard $(LOCAL_PATH)/api/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/dft/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/dft/scalar/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/dft/scalar/codelets/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/kernel/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/rdft/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/rdft/scalar/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/rdft/scalar/r2cf/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/rdft/scalar/r2cb/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/rdft/scalar/r2r/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/reodft/*.c)
	LOCAL_SRC_FILES := $(FILE_LIST:$(LOCAL_PATH)/%=%)
	
	LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/api 
	
   	LOCAL_C_INCLUDES :=  \
   		$(LOCAL_PATH)/simd-support \
        $(LOCAL_PATH)/api \
        $(LOCAL_PATH)/dft \
        $(LOCAL_PATH)/dft/simd \
        $(LOCAL_PATH)/dft/scalar \
        $(LOCAL_PATH)/dft/scalar/codelets \
        $(LOCAL_PATH)/kernel \
        $(LOCAL_PATH)/rdft \
        $(LOCAL_PATH)/rdft/scalar \
        $(LOCAL_PATH)/rdft/scalar/r2cb \
        $(LOCAL_PATH)/rdft/scalar/r2cf \
        $(LOCAL_PATH)/rdft/scalar/r2r \
        $(LOCAL_PATH)/rdft/simd \
        $(LOCAL_PATH)/reodft
 	
   	LOCAL_MODULE    := f
   	NDK_LOG :=0
   	LOCAL_SHORT_COMMANDS := true
   	LOCAL_CFLAGS := -DBOOST_EXCEPTION_DISABLE -DHAVE_FFTW3 -DANDROID_OS -D_STLP_NO_EXCEPTIONS -DOS_ANDROID -D_STLP_USE_SIMPLE_NODE_ALLOC   -fdata-sections -ffunction-sections
   	ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
   	LOCAL_CFLAGS += -mfloat-abi=softfp -mfpu=neon
	FILE_LIST += $(wildcard $(LOCAL_PATH)/rdft/simd/neon/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/dft/simd/neon/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/simd-support/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/rdft/simd/common/*.c)
	FILE_LIST += $(wildcard $(LOCAL_PATH)/dft/simd/common/*.c)
	else
   	endif
   	#LOCAL_LDFLAGS := -Wl,--gc-sections -Wl,--dynamic-list=$(LOCAL_PATH)/JNIInterface/symbols.h
   	LOCAL_ARM_MODE := arm
   	include $(BUILD_STATIC_LIBRARY)
   	
