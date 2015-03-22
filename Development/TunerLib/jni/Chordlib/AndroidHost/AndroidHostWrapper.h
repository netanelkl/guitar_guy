#ifndef ANDROIDHOSTWRAPPER_H
#define ANDROIDHOSTWRAPPER_H

#include <vamp-hostsdk/HostWrapper.h>

class AndroidHostWrapper : public HostWrapper
{
public:
	~AndroidHostWrapper() {
		}
protected:
	Vamp::Plugin* 	createPlugin(string myname,string soname,string id, float sfSampleRate);


};

#endif
