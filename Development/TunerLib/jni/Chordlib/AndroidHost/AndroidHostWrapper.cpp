#ifdef ANDROID_OS
#include <vamp-hostsdk/PluginLoader.h>
#include "AndroidHostWrapper.h"
#include <chordino/Chordino.h>
#include <chordino/Tuning.h>
#include <chordino/FrequencyFinder.h>

using namespace std;

#define HOST_VERSION "1.5"

Vamp::Plugin* AndroidHostWrapper::createPlugin(string myname, string soname,
		string id, float sfSampleRate)
{
#ifdef DEBUG_PRINT_ANDROID_HOST_WRAPPER
	YELL();
#endif

	int nAdapterFlags = PluginLoader::ADAPT_ALL_SAFE;
	Plugin* pPlugin = NULL;
	if (myname == "chordino")
	{
#ifdef DEBUG_PRINT_ANDROID_HOST_WRAPPER
		YELL();
#endif
		pPlugin = new Chordino(sfSampleRate);
	}
	else if (myname == "Tuning")
	{
		pPlugin = new Tuning(sfSampleRate);
		nAdapterFlags = PluginLoader::ADAPT_CHANNEL_COUNT;
	}
	else if (myname == FrequencyFinder::GetName())
	{
		pPlugin = new FrequencyFinder(sfSampleRate);
	}
	if (!pPlugin)
	{
		RETURN_NULL()
	}
#ifdef DEBUG_PRINT_ANDROID_HOST_WRAPPER
	YELL_ADDRESS(pPlugin);
#endif

	PluginLoader* pPluginLoader = PluginLoader::getInstance();
	if (!pPluginLoader)
	{
		RETURN_NULL()
		;
	}

	pPlugin = pPluginLoader->wrapPlugin(pPlugin, nAdapterFlags);
	return pPlugin;

}

#endif
