#ifndef ANDROID_OS

#include "WinHostWrapper.h"

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

#define HOST_VERSION "1.5"

bool	WinHostWrapper::loadAudioFile(string wavname)
{
	if (!wavFileLoader.Load((char*) wavname.c_str()))
	{

		CERR(wavname << ": ERROR: Failed to open input file \"" << endl);
		return false;
	}

	return true;
}


Vamp::Plugin* WinHostWrapper::createPlugin(string myname,string soname,string id, float sfSampleRate)
{
	PluginLoader *loader = PluginLoader::getInstance();

	PluginLoader::PluginKey key = loader->composePluginKey(soname, id);


	Plugin *plugin = loader->loadPlugin(key, sfSampleRate,
			PluginLoader::ADAPT_ALL_SAFE);

	return plugin;
}

void WinHostWrapper::usage(const char *name)
{
	COUT("\n" << name
			<< ": A command-line host for Vamp audio analysis plugins.\n\n"
					"Centre for Digital Music, Queen Mary, University of London.\n"
					"Copyright 2006-2009 Chris Cannam and QMUL.\n"
					"Freely redistributable; published under a BSD-style license.\n\n"
					"Usage:\n\n"
					"    -- Load plugin id \"plugin\" from \"pluginlibrary\" and run it on the\n"
					"       audio data in \"file.wav\", retrieving the named \"output\", or output\n"
					"       number \"outputno\" (the first output by default) and dumping it to\n"
					"       standard output, or to \"out.txt\" if the -o option is given.\n\n"
					"       \"pluginlibrary\" should be a library name, not a file path; the\n"
					"       standard Vamp library search path will be used to locate it.  If\n"
					"       a file path is supplied, the directory part(s) will be ignored.\n\n"
					"       If the -s option is given, results will be labelled with the audio\n"
					"       sample frame at which they occur. Otherwise, they will be labelled\n"
					"       with time in seconds.\n\n"
					"  " << name << " -l\n"
					"  " << name
			<< " --list\n\n"
					"    -- List the plugin libraries and Vamp plugins in the library search path\n"
					"       in a verbose human-readable format.\n\n"
					"  " << name
			<< " --list-full\n\n"
					"    -- List all data reported by all the Vamp plugins in the library search\n"
					"       path in a very verbose human-readable format.\n\n"
					"  " << name
			<< " --list-ids\n\n"
					"    -- List the plugins in the search path in a terse machine-readable format,\n"
					"       in the form vamp:soname:identifier.\n\n"
					"  " << name
			<< " --list-outputs\n\n"
					"    -- List the outputs for plugins in the search path in a machine-readable\n"
					"       format, in the form vamp:soname:identifier:output.\n\n"
					"  " << name
			<< " --list-by-category\n\n"
					"    -- List the plugins as a plugin index by category, in a machine-readable\n"
					"       format.  The format may change in future releases.\n\n"
					"  " << name << " -p\n\n"
					"    -- Print out the Vamp library search path.\n\n"
					"  " << name << " -v\n\n"
					"    -- Display version information only.\n" << endl);
	exit(2);
}

int WinHostWrapper::shellMain(int argc, char **argv)
{
	char *scooter = argv[0];
	char *name = 0;
	while (scooter && *scooter)
	{
		if (*scooter == '/' || *scooter == '\\')
			name = ++scooter;
		else
			++scooter;
	}
	if (!name || !*name)
		name = argv[0];

	if (argc < 2)
		usage(name);

	if (argc == 2)
	{

		if (!strcmp(argv[1], "-v"))
		{

			COUT( "Simple Vamp plugin host version: " << HOST_VERSION << endl
					<< "Vamp API version: " << VAMP_API_VERSION << endl
					<< "Vamp SDK version: " << VAMP_SDK_VERSION << endl);
			return 0;

		}
		else if (!strcmp(argv[1], "-l") || !strcmp(argv[1], "--list"))
		{

			printPluginPath(true);
			enumeratePlugins(PluginInformation);
			return 0;

		}
		else if (!strcmp(argv[1], "--list-full"))
		{

			enumeratePlugins(PluginInformationDetailed);
			return 0;

		}
		else if (!strcmp(argv[1], "-p"))
		{

			printPluginPath(false);
			return 0;

		}
		else if (!strcmp(argv[1], "--list-ids"))
		{

			enumeratePlugins(PluginIds);
			return 0;

		}
		else if (!strcmp(argv[1], "--list-outputs"))
		{

			enumeratePlugins(PluginOutputIds);
			return 0;

		}
		else if (!strcmp(argv[1], "--list-by-category"))
		{

			printPluginCategoryList();
			return 0;

		}
		else
			usage(name);
	}

	if (argc < 3)
		usage(name);

	bool useFrames = false;

	int base = 1;
	if (!strcmp(argv[1], "-s"))
	{
		useFrames = true;
		base = 2;
	}

	string soname = argv[base];
	string wavname = argv[base + 1];
	string plugid = "";
	string output = "";
	int outputNo = -1;
	string outfilename;

	if (argc >= base + 3)
	{

		int idx = base + 2;

		if (isdigit(*argv[idx]))
		{
			outputNo = atoi(argv[idx++]);
		}

		if (argc == idx + 2)
		{
			if (!strcmp(argv[idx], "-o"))
			{
				outfilename = argv[idx + 1];
			}
			else
				usage(name);
		}
		else if (argc != idx)
		{
			(usage(name));
		}
	}

	COUT(endl << name << ": Running..." << endl);

	COUT("Reading file: \"" << wavname << "\", writing to ");
	if (outfilename == "")
	{
		COUT("standard output" << endl);
	}
	else
	{
		COUT("\"" << outfilename << "\"" << endl);
	}

	string::size_type sep = soname.find(':');

	if (sep != string::npos)
	{
		plugid = soname.substr(sep + 1);
		soname = soname.substr(0, sep);

		sep = plugid.find(':');
		if (sep != string::npos)
		{
			output = plugid.substr(sep + 1);
			plugid = plugid.substr(0, sep);
		}
	}

	if (plugid == "")
	{
		usage(name);
	}

	if (output != "" && outputNo != -1)
	{
		usage(name);
	}

	if (output == "" && outputNo == -1)
	{
		outputNo = 0;
	}

	string str(name);
	loadAudioFile(wavname);

	loadPlugin(str, soname, plugid, output, outputNo,
			useFrames, 44100);

	runPlugin((word*)wavFileLoader.GetData(),wavFileLoader.GetSize());

	printFeatures(
			RealTime::realTime2Frame(m_rtCurrentTime + m_rtAdjustment,
					m_sfSampleRate), m_sfSampleRate, m_wOutputNumber,
			m_CurrentPlugin->getRemainingFeatures(), m_fUseFrames);

	return 1;
}

WinHostWrapper::~WinHostWrapper()
{

}

void WinHostWrapper::printPluginPath(bool verbose)
{
	if (verbose)
	{
		COUT( "\nVamp plugin search path: ");
	}

	vector<string> path = PluginHostAdapter::getPluginPath();
	for (size_t i = 0; i < path.size(); ++i)
	{
		if (verbose)
		{
			COUT( "[" << path[i] << "]");
		}
		else
		{
			COUT( path[i] << endl);
		}
	}

	if (verbose)
		COUT( endl);
}

string WinHostWrapper::header(string text, int level)
{
	string out = '\n' + text + '\n';
	for (size_t i = 0; i < text.length(); ++i)
	{
		out += (level == 1 ? '=' : level == 2 ? '-' : '~');
	}
	out += '\n';
	return out;
}

void WinHostWrapper::enumeratePlugins(Verbosity verbosity)
{
	PluginLoader *loader = PluginLoader::getInstance();

	if (verbosity == PluginInformation)
	{
		COUT( "\nVamp plugin libraries found in search path:" << endl);
	}

	vector<PluginLoader::PluginKey> plugins = loader->listPlugins();
	typedef multimap<string, PluginLoader::PluginKey> LibraryMap;
	LibraryMap libraryMap;

	for (size_t i = 0; i < plugins.size(); ++i)
	{
		string path = loader->getLibraryPathForPlugin(plugins[i]);
		libraryMap.insert(LibraryMap::value_type(path, plugins[i]));
	}

	string prevPath = "";
	int index = 0;

	for (LibraryMap::iterator i = libraryMap.begin(); i != libraryMap.end();
			++i)
	{

		string path = i->first;
		PluginLoader::PluginKey key = i->second;

		if (path != prevPath)
		{
			prevPath = path;
			index = 0;
			if (verbosity == PluginInformation)
			{
				COUT( "\n  " << path << ":" << endl);
			}
			else if (verbosity == PluginInformationDetailed)
			{
				string::size_type ki = i->second.find(':');
				string text = "Library \"" + i->second.substr(0, ki) + "\"";
				COUT( "\n" << header(text, 1));
			}
		}

		Plugin *plugin = loader->loadPlugin(key, 48000);
		if (plugin)
		{

			char c = char('A' + index);
			if (c > 'Z')
				c = char('a' + (index - 26));

			PluginLoader::PluginCategoryHierarchy category =
					loader->getPluginCategory(key);
			string catstr;
			if (!category.empty())
			{
				for (size_t ci = 0; ci < category.size(); ++ci)
				{
					if (ci > 0)
						catstr += " > ";
					catstr += category[ci];
				}
			}

			if (verbosity == PluginInformation)
			{

				COUT( "    [" << c << "] [v" << plugin->getVampApiVersion()
						<< "] " << plugin->getName() << ", \""
						<< plugin->getIdentifier() << "\"" << " ["
						<< plugin->getMaker() << "]" << endl);

				if (catstr != "")
				{
					COUT( "       > " << catstr << endl);
				}

				if (plugin->getDescription() != "")
				{
					COUT( "        - " << plugin->getDescription() << endl);
				}

			}
			else if (verbosity == PluginInformationDetailed)
			{

				COUT( header(plugin->getName(), 2));
				COUT( " - Identifier:         " << key << endl);
				COUT( " - Plugin Version:     " << plugin->getPluginVersion()
						<< endl);
				COUT( " - Vamp API Version:   " << plugin->getVampApiVersion()
						<< endl);
				COUT( " - Maker:              \"" << plugin->getMaker()
						<< "\"" << endl);
				COUT( " - Copyright:          \"" << plugin->getCopyright()
						<< "\"" << endl);
				COUT( " - Description:        \"" << plugin->getDescription()
						<< "\"" << endl);
				COUT( " - Input Domain:       "
						<< (plugin->getInputDomain()
								== Vamp::Plugin::TimeDomain ?
								"Time Domain" : "Frequency Domain") << endl);
				COUT( " - Default Step Size:  "
						<< plugin->getPreferredStepSize() << endl);
				COUT( " - Default Block Size: "
						<< plugin->getPreferredBlockSize() << endl);
				COUT( " - Minimum Channels:   "
						<< plugin->getMinChannelCount() << endl);
				COUT( " - Maximum Channels:   "
						<< plugin->getMaxChannelCount() << endl);

			}
			else if (verbosity == PluginIds)
			{
				COUT( "vamp:" << key << endl);
			}

			Plugin::OutputList outputs = plugin->getOutputDescriptors();

			if (verbosity == PluginInformationDetailed)
			{

				Plugin::ParameterList params =
						plugin->getParameterDescriptors();
				for (size_t j = 0; j < params.size(); ++j)
				{
					Plugin::ParameterDescriptor &pd(params[j]);
					COUT( "\nParameter " << j + 1 << ": \"" << pd.name << "\""
							<< endl);
					COUT( " - Identifier:         " << pd.identifier << endl);
					COUT( " - Description:        \"" << pd.description
							<< "\"" << endl);
					if (pd.unit != "")
					{
						COUT( " - Unit:               " << pd.unit << endl);
					}
					COUT( " - Range:              ");
					COUT( pd.minValue << " -> " << pd.maxValue << endl);
					COUT( " - Default:            ");
					COUT( pd.defaultValue << endl);
					if (pd.isQuantized)
					{
						COUT( " - Quantize Step:      " << pd.quantizeStep
								<< endl);
					}
					if (!pd.valueNames.empty())
					{
						COUT( " - Value Names:        ");
						for (size_t k = 0; k < pd.valueNames.size(); ++k)
						{
							if (k > 0)
								COUT( ", ");
							COUT( "\"" << pd.valueNames[k] << "\"");
						}
						COUT( endl);
					}
				}

				if (outputs.empty())
				{
					COUT( "\n** Note: This plugin reports no outputs!"
							<< endl);
				}
				for (size_t j = 0; j < outputs.size(); ++j)
				{
					Plugin::OutputDescriptor &od(outputs[j]);
					COUT( "\nOutput " << j + 1 << ": \"" << od.name << "\""
							<< endl);
					COUT( " - Identifier:         " << od.identifier << endl);
					COUT( " - Description:        \"" << od.description
							<< "\"" << endl);
					if (od.unit != "")
					{
						COUT( " - Unit:               " << od.unit << endl);
					}
					if (od.hasFixedBinCount)
					{
						COUT( " - Default Bin Count:  " << od.binCount
								<< endl);
					}
					if (!od.binNames.empty())
					{
						bool have = false;
						for (size_t k = 0; k < od.binNames.size(); ++k)
						{
							if (od.binNames[k] != "")
							{
								have = true;
								break;
							}
						}
						if (have)
						{
							COUT( " - Bin Names:          ");
							for (size_t k = 0; k < od.binNames.size(); ++k)
							{
								if (k > 0)
									COUT( ", ");
								COUT( "\"" << od.binNames[k] << "\"");
							}
							COUT( endl);
						}
					}
					if (od.hasKnownExtents)
					{
						COUT( " - Default Extents:    ");
						COUT( od.minValue << " -> " << od.maxValue << endl);
					}
					if (od.isQuantized)
					{
						COUT( " - Quantize Step:      " << od.quantizeStep
								<< endl);
					}
					COUT( " - Sample Type:        "
							<< (od.sampleType
										== Plugin::OutputDescriptor::OneSamplePerStep ?
									"One Sample Per Step" :
								od.sampleType
										== Plugin::OutputDescriptor::FixedSampleRate ?
										"Fixed Sample Rate" :
										"Variable Sample Rate") << endl);
					if (od.sampleType
							!= Plugin::OutputDescriptor::OneSamplePerStep)
					{
						COUT( " - Default Rate:       " << od.sampleRate
								<< endl);
					}
					COUT( " - Has Duration:       "
							<< (od.hasDuration ? "Yes" : "No") << endl);
				}
			}

			if (outputs.size() > 1 || verbosity == PluginOutputIds)
			{
				for (size_t j = 0; j < outputs.size(); ++j)
				{
					if (verbosity == PluginInformation)
					{
						COUT( "         (" << j << ") " << outputs[j].name
								<< ", \"" << outputs[j].identifier << "\""
								<< endl);
						if (outputs[j].description != "")
						{
							COUT( "             - " << outputs[j].description
									<< endl);
						}
					}
					else if (verbosity == PluginOutputIds)
					{
						COUT( "vamp:" << key << ":" << outputs[j].identifier
								<< endl);
					}
				}
			}

			++index;

			delete plugin;
		}
	}

	if (verbosity == PluginInformation
			|| verbosity == PluginInformationDetailed)
	{
		COUT( endl);
	}
}

void WinHostWrapper::printPluginCategoryList()
{
	PluginLoader *loader = PluginLoader::getInstance();

	vector<PluginLoader::PluginKey> plugins = loader->listPlugins();

	set<string> printedcats;

	for (size_t i = 0; i < plugins.size(); ++i)
	{

		PluginLoader::PluginKey key = plugins[i];

		PluginLoader::PluginCategoryHierarchy category =
				loader->getPluginCategory(key);

		Plugin *plugin = loader->loadPlugin(key, 48000);
		if (!plugin)
			continue;

		string catstr = "";

		if (category.empty())
			catstr = '|';
		else
		{
			for (size_t j = 0; j < category.size(); ++j)
			{
				catstr += category[j];
				catstr += '|';
				if (printedcats.find(catstr) == printedcats.end())
				{
					COUT( catstr << std::endl);
					printedcats.insert(catstr);
				}
			}
		}

		COUT( catstr << key << ":::" << plugin->getName() << ":::"
				<< plugin->getMaker() << ":::" << plugin->getDescription()
				<< std::endl);
	}
}


#endif
