APP_STL                 := gnustl_static
#APP_CPPFLAGS 			+= -fexceptions
APP_CFLAGS 				+= -Wno-error=format-security 
APP_CPPFLAGS 			+= -Wno-error=format-security 
APP_CPPFLAGS 			+= -frtti 
APP_OPTIM        		:= release
APP_ABI          		:= x86 armeabi armeabi-v7a
APP_MODULES      		:= Chordlib
NDK_TOOLDCHAIN_VERSION:=4.8