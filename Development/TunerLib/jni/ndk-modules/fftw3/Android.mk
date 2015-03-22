LOCAL_PATH := $(call my-dir)

# include libandprof.a in the build
include $(CLEAR_VARS)
LOCAL_MODULE := f
NDK_LOG:= 1
LOCAL_SRC_FILES := ../../../obj/local/$(TARGET_ARCH_ABI)/libf.a
LOCAL_EXPORT_C_INCLUDES := $(FFTW_PATH)/api
include $(PREBUILT_STATIC_LIBRARY)
