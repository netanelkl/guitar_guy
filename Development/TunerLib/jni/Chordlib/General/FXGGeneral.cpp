#include "FXGGeneral.h"
#include <time.h>

// from android samples
/* return current time in milliseconds */
long now_ms(void) {

    struct timespec res;
    clock_gettime(CLOCK_REALTIME, &res);
    return 1000 * res.tv_sec +  res.tv_nsec / 1e6;

}

std::stringstream g_strCerr;
std::stringstream g_strCout;


std::string string_format(const std::string &fmt, ...)
{
	int size = 100;
	std::string str;
	va_list ap;
	while (1)
	{
		str.resize(size);
		va_start(ap, fmt);
		int n = vsnprintf((char *) str.c_str(), size, fmt.c_str(), ap);
		va_end(ap);
		if (n > -1 && n < size)
		{
			str.resize(n);
			return str;
		}
		if (n > -1)
			size = n + 1;
		else
			size *= 2;
	}
	return str;
}
