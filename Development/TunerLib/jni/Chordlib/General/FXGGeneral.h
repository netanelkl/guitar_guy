#ifndef FXGGENERAL_H
#define FXGGENERAL_H

#include <string>
std::string string_format(const std::string &fmt, ...);
#include <cstring>
#include <sstream>
#include "Logger.h"

//#define PROFILER_ENABLED
//#define DEBUG_PRINT_HOSTWRAPPER_PRINT_FEATURES
//#define DEBUG_PRINT_PRINT_STEP_FEATURES
//#define DEBUG_PRINT_ANDROID_HOST_WRAPPER
#define DEBUG_TUNING
#define DELETE_POINTER(x)	if(x != NULL) {delete x;} x = NULL
#define DELETE_ARRAY_POINTER(x)	if(x != NULL) {delete[] x;} x = NULL
#define DEBUG_MEMORY_LEAK_CHECK
typedef unsigned long dword;
typedef unsigned char byte;
typedef unsigned short word;


extern "C"
{
extern std::stringstream g_strCerr;
extern std::stringstream g_strCout;
long now_ms(void);
}




#define RETURN_IF_FALSE(cond) 				\
	if(!(cond))								\
		RETURN_FALSE();


#define RETURN_FALSE()						\
	{ 										\
		LOGD("Returned false");				\
		return false;						\
	}

#define RETURN_NULL() 						\
	{ 										\
		LOGD("Returned null");				\
		return NULL;						\
	}

#define YELL(...) 		COUT("YELL") //LOGD(#__VA_ARGS__, "Reached Line(%s) (%d)",CURR_FILENAME, __LINE__)
#define YELL_NUM(x)	LOGD(#x "(%u)", x)
#define YELL_ADDRESS(x)	LOGD(#x "(0x%08X)", x)
#define VAR_TELLER(x) 	COUT(#x << "(" << x << ")")
#endif
