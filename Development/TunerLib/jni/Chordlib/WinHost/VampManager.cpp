/* -*- c-basic-offset: 4 indent-tabs-mode: nil -*-  vi:set ts=8 sts=4 sw=4: */

/*
 Vamp

 An API for audio analysis and feature extraction plugins.

 Centre for Digital Music, Queen Mary, University of London.
 Copyright 2006 Chris Cannam, copyright 2007-2008 QMUL.

 Permission is hereby granted, free of charge, to any person
 obtaining a copy of this software and associated documentation
 files (the "Software"), to deal in the Software without
 restriction, including without limitation the rights to use, copy,
 modify, merge, publish, distribute, sublicense, and/or sell copies
 of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR
 ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Except as contained in this notice, the names of the Centre for
 Digital Music; Queen Mary, University of London; and Chris Cannam
 shall not be used in advertising or otherwise to promote the sale,
 use or other dealings in this Software without prior written
 authorization.
 */

/*
 * This "simple" Vamp plugin host is no longer as simple as it was; it
 * now has a lot of options and includes a lot of code to handle the
 * various useful listing modes it supports.
 *
 * However, the runPlugin function still contains a reasonable
 * implementation of a fairly generic Vamp plugin host capable of
 * evaluating a given output on a given plugin for a sound file read
 * via libsndfile.
 */
#include <General/FXGGeneral.h>
#include "VampManager.h"

void WindowsHostManager::usage(const char *name)
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
					"    -- Display version information only.\n" );
	exit(2);
}

int WindowsHostManager::shellMain(int argc, char **argv)
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

	COUT(endl << name << ": Running...\n");

	COUT("Reading file: \"" << wavname << "\", writing to ");
	if (outfilename == "")
	{
		COUT("standard output\n");
	}
	else
	{
		COUT("\"" << outfilename << "\"\n");
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

	if (!wavFileLoader.Load((char*) wavname.c_str()))
	{

		return 1;
	}

	return runPlugin(name, soname, plugid, output, outputNo, wavname,
			outfilename, useFrames);
}

word WindowsHostManager::toFloatBuffer(word* pCurrentDataPosition, float* pDataBuffer,
		word wWantedSamples) const
{
	byte* pCurrentDataPositionAsBytes = (byte*) pCurrentDataPosition;
	byte* pContentBaseAddressAsBytes = (byte*) m_pDataStartPosition;
	if ((pCurrentDataPositionAsBytes < pContentBaseAddressAsBytes)
			|| (pCurrentDataPositionAsBytes
					>= (pContentBaseAddressAsBytes + m_dwDataSize)))
	{
		return 0;
	}

	if (((pCurrentDataPositionAsBytes + wWantedSamples)
			> (pContentBaseAddressAsBytes + m_dwDataSize)))
	{
		wWantedSamples = (word) ((dword) (pContentBaseAddressAsBytes
				+ m_dwDataSize) - (dword) pCurrentDataPositionAsBytes)
				/ sizeof(word);
	}

	for (int i = 0; i < wWantedSamples; i++)
	{
		pDataBuffer[i] = ((float) (((double) pCurrentDataPositionAsBytes[i]
				- 0x7fff) / 0x7fff));
	}

	return wWantedSamples;
}

int WindowsHostManager::runPlugin(string myname, string soname, string id, string output,
		int outputNo, string wavname, string outfilename, bool useFrames)
{
	PluginLoader *loader = PluginLoader::getInstance();

	PluginLoader::PluginKey key = loader->composePluginKey(soname, id);



	word* pCurrentSample = ((word*)wavFileLoader.GetData());

	Plugin *plugin = loader->loadPlugin(key, wavFileLoader.GetSampleRate(),
			PluginLoader::ADAPT_ALL_SAFE);
	if (!plugin)
	{
		CERR( myname << ": ERROR: Failed to load plugin \"" << id
				<< "\" from library \"" << soname << "\"" );

		return 1;
	}
	m_CurrentPlugin = plugin;

	CERR( "Running plugin: \"" << plugin->getIdentifier() << "\"..." );

	// Note that the following would be much simpler if we used a
	// PluginBufferingAdapter as well -- i.e. if we had passed
	// PluginLoader::ADAPT_ALL to loader->loadPlugin() above, instead
	// of ADAPT_ALL_SAFE.  Then we could simply specify our own block
	// size, keep the step size equal to the block size, and ignore
	// the plugin's bleatings.  However, there are some issues with
	// using a PluginBufferingAdapter that make the results sometimes
	// technically different from (if effectively the same as) the
	// un-adapted plugin, so we aren't doing that here.  See the
	// PluginBufferingAdapter documentation for details.

	int blockSize = plugin->getPreferredBlockSize();
	int stepSize = plugin->getPreferredStepSize();
	m_dwBlockSize = plugin->getPreferredBlockSize();
	m_dwStepSize = plugin->getPreferredStepSize();
	m_fUseFrames = useFrames;
	m_sfSampleRate = (float)wavFileLoader.GetSampleRate();
	if (blockSize == 0)
	{
		blockSize = 1024;
	}
	if (stepSize == 0)
	{
		if (plugin->getInputDomain() == Plugin::FrequencyDomain)
		{
			stepSize = blockSize / 2;
		}
		else
		{
			stepSize = blockSize;
		}
	}
	else if (stepSize > blockSize)
	{
		CERR( "WARNING: stepSize " << stepSize << " > blockSize " << blockSize
				<< ", resetting blockSize to ");
		if (plugin->getInputDomain() == Plugin::FrequencyDomain)
		{
			blockSize = stepSize * 2;
		}
		else
		{
			blockSize = stepSize;
		}
		CERR( blockSize );
	}
	int overlapSize = blockSize - stepSize;
	word currentStep = 0;
	int finalStepsRemaining = max(1, (blockSize / stepSize) - 1); // at end of file, this many part-silent frames needed after we hit EOF

	int channels = wavFileLoader.GetChannels();

	float *filebuf = new float[blockSize * channels];
	float **plugbuf = new float*[channels];
	m_pFifoBlock = filebuf;
	m_pPluginBuffer = plugbuf;
	m_pDataStartPosition = filebuf;
	m_dwDataSize = wavFileLoader.GetSize();
	for (int c = 0; c < channels; ++c)
		plugbuf[c] = new float[blockSize + 2];

	COUT( "Using block size = " << blockSize << ", step size = " << stepSize
			);

	// The channel queries here are for informational purposes only --
	// a PluginChannelAdapter is being used automatically behind the
	// scenes, and it will take case of any channel mismatch

	int minch = plugin->getMinChannelCount();
	int maxch = plugin->getMaxChannelCount();
	COUT( "Plugin accepts " << minch << " -> " << maxch << " channel(s)"
			);
	COUT( "Sound file has " << channels << " (will mix/augment if necessary)"
			);

	Plugin::OutputList outputs = plugin->getOutputDescriptors();
	Plugin::OutputDescriptor od;

	int returnValue = 1;
	int progress = 0;

	RealTime rt;
	PluginWrapper *wrapper = 0;
	RealTime adjustment = RealTime::zeroTime;

	if (outputs.empty())
	{
		CERR( "ERROR: Plugin has no outputs!" );
		goto done;
	}

	if (outputNo < 0)
	{

		for (size_t oi = 0; oi < outputs.size(); ++oi)
		{
			if (outputs[oi].identifier == output)
			{
				outputNo = oi;
				break;
			}
		}

		if (outputNo < 0)
		{
			CERR( "ERROR: Non-existent output \"" << output << "\" requested"
					);
			goto done;
		}

	}
	else
	{

		if (int(outputs.size()) <= outputNo)
		{
			CERR( "ERROR: Output " << outputNo
					<< " requested, but plugin has only " << outputs.size()
					<< " output(s)" );
			goto done;
		}
	}

	od = outputs[outputNo];
	CERR( "Output is: \"" << od.identifier << "\"" );

	if (!plugin->initialise(channels, stepSize, blockSize))
	{
		CERR( "ERROR: Plugin initialise (channels = " << channels
				<< ", stepSize = " << stepSize << ", blockSize = " << blockSize
				<< ") failed." );
		goto done;
	}

	wrapper = dynamic_cast<PluginWrapper *>(plugin);
	if (wrapper)
	{
		// See documentation for
		// PluginInputDomainAdapter::getTimestampAdjustment
		PluginInputDomainAdapter *ida = wrapper->getWrapper<
				PluginInputDomainAdapter>();
		if (ida)
			adjustment = ida->getTimestampAdjustment();
	}

	// First step.
	//memset(m_pFifoBlock, 0, m_dwStepSize);

	// Here we iterate over the frames, avoiding asking the numframes in case it's streaming input.
	do
	{

		int count;

		if ((blockSize == stepSize) || (currentStep == 0))
		{
			// read a full fresh block
			if ((count = wavFileLoader.ToFloatBuffer(pCurrentSample, m_pFifoBlock, m_dwBlockSize)) < 0)
			{
				LOGE( "ERROR: sf_readf_float failed: " );
				break;
			}

			if (count != blockSize)
				--finalStepsRemaining;
		}
		else
		{
			//  otherwise shunt the existing data down and read the remainder.
			memmove(m_pFifoBlock, m_pFifoBlock + (m_dwStepSize),
					overlapSize * sizeof(float));
			if ((count = wavFileLoader.ToFloatBuffer(pCurrentSample,
					m_pFifoBlock + (overlapSize * channels), m_dwStepSize)) < 0)
			{
				LOGE( "ERROR: sf_readf_float failed: " );
				break;
			}
			if (count != stepSize)
				--finalStepsRemaining;
			count += overlapSize;
		}


		// This copies the buffer to a 'masked' one.
		// Later on test if this is necessary.
		memcpy(m_pPluginBuffer[0], m_pFifoBlock, count * sizeof(float));
		memset(m_pPluginBuffer[0] + count, 0, (blockSize - count) * sizeof(float));


		rt = RealTime::frame2RealTime(currentStep * stepSize,
				wavFileLoader.GetSampleRate());

//		 printFeatures(
//				RealTime::realTime2Frame(rt + adjustment, wavFileLoader.GetSampleRate()),
//				wavFileLoader.GetSampleRate(), outputNo, plugin->process(plugbuf, rt),
//				useFrames);
//		printFeatures(RealTime::realTime2Frame(rt + adjustment, wavFileLoader.GetSampleRate()),
//				wavFileLoader.GetSampleRate(), outputNo, plugin->getRemainingFeatures(), out,
//				useFrames);
//		if (sfinfo.frames > 0)
//		{
//			int pp = progress;
//			progress = lrintf(
//					(float(currentStep * stepSize) / sfinfo.frames) * 100.f);
//			if (progress != pp && out)
//			{
//				LOGE( "\r" << progress << "%";
//			}
//		}

		pCurrentSample += stepSize;
		++currentStep;

	} while (finalStepsRemaining > 0);

		CERR( "\rDone" );

	rt = RealTime::frame2RealTime(currentStep * stepSize, wavFileLoader.GetSampleRate());

//	printFeatures(RealTime::realTime2Frame(rt + adjustment, wavFileLoader.GetSampleRate()),
//			wavFileLoader.GetSampleRate(), outputNo, plugin->getRemainingFeatures(),
//			useFrames);

	returnValue = 0;

	done: delete plugin;

	return returnValue;
}

void WindowsHostManager::printFeatures(int frame, int sr, int output, Plugin::FeatureSet features, bool useFrames)
{

	for (unsigned int i = 0; i < features[output].size(); ++i)
	{

		if (useFrames)
		{

			int displayFrame = frame;

			if (features[output][i].hasTimestamp)
			{
				displayFrame = RealTime::realTime2Frame(
						features[output][i].timestamp, sr);
			}

			COUT( displayFrame);

			if (features[output][i].hasDuration)
			{
				displayFrame = RealTime::realTime2Frame(
						features[output][i].duration, sr);
				COUT("," << displayFrame);
			}

			COUT( ":");

		}
		else
		{

			RealTime rt = RealTime::frame2RealTime(frame, sr);

			if (features[output][i].hasTimestamp)
			{
				rt = features[output][i].timestamp;
			}

			COUT(rt.toString());

			if (features[output][i].hasDuration)
			{
				rt = features[output][i].duration;
				COUT("," << rt.toString());
			}

			COUT(":");
		}
		for (unsigned int j = 0; j < features[output][i].values.size(); ++j)
		{
			COUT(" " << features[output][i].values[j]);
		}
		COUT(" " << features[output][i].label);
		COUT(endl);
	}
}

void WindowsHostManager::printPluginPath(bool verbose)
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

string WindowsHostManager::header(string text, int level)
{
	string out = '\n' + text + '\n';
	for (size_t i = 0; i < text.length(); ++i)
	{
		out += (level == 1 ? '=' : level == 2 ? '-' : '~');
	}
	out += '\n';
	return out;
}

void WindowsHostManager::enumeratePlugins(Verbosity verbosity)
{
	PluginLoader *loader = PluginLoader::getInstance();

	if (verbosity == PluginInformation)
	{
		COUT( "\nVamp plugin libraries found in search path:\n");
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
				COUT( "\n  " << path << ":\n");
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
						<< plugin->getMaker() << "]\n");

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
						<< "\"\n");
				COUT( " - Copyright:          \"" << plugin->getCopyright()
						<< "\"\n");
				COUT( " - Description:        \"" << plugin->getDescription()
						<< "\"\n");
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
							<< "\"\n");
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
							<< "\"\n");
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

void WindowsHostManager::printPluginCategoryList()
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

