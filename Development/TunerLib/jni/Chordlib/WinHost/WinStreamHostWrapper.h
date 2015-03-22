#ifndef WINSTREAMHOSTWRAPPER_H
#define WINSTREAMHOSTWRAPPER_H

#include <vamp-hostsdk/HostWrapper.h>
#include <vamp-hostsdk/PluginHostAdapter.h>
#include <vamp-hostsdk/PluginInputDomainAdapter.h>
#include <vamp-hostsdk/PluginLoader.h>

#include <iostream>
#include <fstream>
#include <set>

#include <cstring>
#include <cstdlib>

#include <cmath>

#include "Wave.h"
using namespace std;


using Vamp::Plugin;
using Vamp::PluginHostAdapter;
using Vamp::RealTime;
using Vamp::HostExt::PluginLoader;
using Vamp::HostExt::PluginWrapper;
using Vamp::HostExt::PluginInputDomainAdapter;


enum Verbosity
{
	PluginIds, PluginOutputIds, PluginInformation, PluginInformationDetailed
};


class WinStreamHostWrapper : public HostWrapper
{
public:
	WinStreamHostWrapper() {}
	int 	shellMain(int argc, char **argv);
	virtual		~WinStreamHostWrapper();
protected:
	virtual Vamp::Plugin* 	createPlugin(string myname,string soname,string id, float sfSampleRate);
private:
	static 	void 	usage(const char *name);
	static 	void 	printPluginPath(bool verbose);
	static 	string 	header(string text, int level);
	static 	void 	enumeratePlugins(Verbosity verbosity);
	static	void 	printPluginCategoryList();

			bool	loadAudioFile(string wavname);


	CWave wavFileLoader;
};

#endif
