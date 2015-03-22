#ifndef __LOGGER_H__
#define __LOGGER_H__

#include <sstream>
#include <string>
#include <cstdio>
#include <cstdarg>

#ifdef ANDROID_OS
#include <android/log.h>
#endif

enum ELogLevel
{
	eLogLevelError,
	eLogLevelWarning,
	eLogLevelInfo,
	eLogLevelDebug,
	eLogLevelVerbose
};

template<typename T>
class CLogger
{
public:
	CLogger();
	virtual ~CLogger();
	 template<typename TPrintable>
	 CLogger& operator<<(TPrintable const& toPrint)
	    { osMsg << toPrint; }
	 void flush();
	 void clear();
	std::ostringstream& Get(const std::string& strTag, ELogLevel level =
			eLogLevelDebug);
public:
	static ELogLevel& ReportingLevel();
protected:
	std::ostringstream osMsg;
	std::ostringstream osTag;
private:
	CLogger(const CLogger&);
	CLogger& operator =(const CLogger&);
};

template<typename T>
CLogger<T>::CLogger()
{

}

template<typename T>
std::ostringstream& CLogger<T>::Get(const std::string& strTag, ELogLevel level)
{
	osTag << strTag;
	return osMsg;
}

template<typename T>
CLogger<T>::~CLogger()
{
	flush();
}
template<typename T>
void CLogger<T>::flush()
{
	if(!osMsg.str().empty())
	{
	osMsg << std::endl;
	T::Output(ReportingLevel(), osTag.str(), osMsg.str());
	}
	osMsg.str( std::string() );
	osMsg.clear();
}

template<typename T>
void CLogger<T>::clear()
{
	osMsg.str( std::string() );
	osMsg.clear();
}

template<typename T>
ELogLevel& CLogger<T>::ReportingLevel()
{
	static ELogLevel reportingLevel = eLogLevelVerbose;
	return reportingLevel;
}

class Output2FILE
{
public:
	static FILE*& Stream(ELogLevel eLogLevel);
	static void Output(ELogLevel eLogLevel, const std::string& strTag,
			const std::string& msg);
};

inline FILE*& Output2FILE::Stream(ELogLevel eLogLevel)
{
	static FILE* pStream = eLogLevel == eLogLevelError ? stderr: stdout;
	return pStream;
}

inline void Output2FILE::Output(ELogLevel eLogLevel, const std::string& strTag,
		const std::string& strMsg)
{
#ifdef ANDROID_OS
	__android_log_print(eLogLevel,string_format("%s", strTag.c_str()).c_str(), string_format("%s", strMsg.c_str()).c_str());
#else

	FILE* pStream = Stream(eLogLevel);

	if (!pStream)
	return;

	fprintf(pStream, "[%s]: %s", strTag.c_str(), strMsg.c_str());
	fflush(pStream);
#endif
}

class CFILELog: public CLogger<Output2FILE>
{
};


#ifndef FILELOG_MAX_LEVEL
#define FILELOG_MAX_LEVEL eLogLevelVerbose
#endif

#define FILE_LOG_STREAM(Tag, level)	CFILELog().Get(Tag, level)
#define CFILELOG_NAME(name)		cLog##name
#define FILE_LOG_CREATOR(name, Tag, level)	CFILELog CFILELOG_NAME(name);		\
						std::ostream& name = cLog##name.Get(Tag, level);

using namespace std;

// Stringifiers
#define STRINGIFY(s) #s
#define XSTR(s) STRINGIFY(s)

// Current filename
#define CURR_FILENAME				(strrchr(__FILE__ "(" XSTR(__LINE__) ")",'/') + 1)
#define CURR_FILENAME_ERR_FORMAT	(__FILE__ ":" XSTR(__LINE__) ":")

#define TOKENPASTE(x, y) x ## y
#define TOKENPASTE2(x, y) TOKENPASTE(x, y)
#define UNIQUE TOKENPASTE2(Unique_, __LINE__)
#define LOG_CREATOR(logLevel)	FILE_LOG_STREAM(CURR_FILENAME, logLevel)
#define LOG_PRINTF(logLevel, x...)	FILE_LOG_STREAM(CURR_FILENAME, logLevel) << string_format(x)
#define LOGV(x...) 	LOG_PRINTF(eLogLevelVerbose,x)
#define LOGD(x...)	LOG_PRINTF(eLogLevelDebug, 	x)
#define LOGI(x...) 	LOG_PRINTF(eLogLevelInfo, 	x)
#define LOGW(x...) 	LOG_PRINTF(eLogLevelWarning,x)
#define LOGE(x...) 	LOG_PRINTF(eLogLevelError, 	x)
#define CERR(x)		LOG_CREATOR(eLogLevelError) 			<< x
#define COUT(x)		LOG_CREATOR(eLogLevelDebug) 			<< x

#define FLOAT_TO_STRING(x)  (string_format("%10.1f", x[_dwCount]))
#define OBJECT_TO_STRING(x)  (x)
#define CALLER(x)		x
#define LOG_BUFFER(x, bufSize)																					\
		LOG_BUFFER_GENERIC(x, bufSize, CALLER(FLOAT_TO_STRING))

#define LOG_BUFFER_OBJECTS(x, bufSize)																					\
		LOG_BUFFER_GENERIC(x, bufSize, CALLER(OBJECT_TO_STRING))

#define LOG_BUFFER_GENERIC(x, bufSize, toString)																					\
			FILE_LOG_CREATOR(_lhBufferPrinter, __FILE__, eLogLevelDebug);										\
			for(dword _dwCount = 0;_dwCount < bufSize;_dwCount++)												\
			{ 																									\
				_lhBufferPrinter << toString(x[_dwCount]) << ", ";										\
				if (_dwCount % 10 == 9) {_lhBufferPrinter << std::endl;CFILELOG_NAME(_lhBufferPrinter).flush();}	\
			}

#endif
