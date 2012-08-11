LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS	:= -DNDEBUG -DKEGS_LITTLE_ENDIAN -O2 -I.
LOCAL_MODULE    := kegs
LOCAL_SRC_FILES := adb.c clock.c config_generic.c config_kegs.c dis.c engine_c.c scc.c iwm.c \
	joystick_dummy.c moremem.c paddles.c sim65816.c smartport.c \
	sound.c sound_driver.c video.c scc_socket_driver.c scc_windriver.c \
	scc_macdriver.c android_driver.c android_sound_driver.c
LOCAL_LDLIBS    := -lm -llog -ljnigraphics -lOpenSLES

include $(BUILD_SHARED_LIBRARY)
