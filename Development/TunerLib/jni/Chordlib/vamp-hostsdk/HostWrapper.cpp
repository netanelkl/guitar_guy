#include "HostWrapper.h"
#include <vamp-hostsdk/PluginWrapper.h>
#include <vamp-hostsdk/PluginInputDomainAdapter.h>
#include <boost/lexical_cast.hpp>
#include <fstream>
#include <algorithm>

using namespace std;
using namespace Vamp::HostExt;
using namespace Vamp;
_VAMP_SDK_HOSTSPACE_BEGIN(PluginWrapper.h)
	HostWrapper::HostWrapper() :
			m_CurrentPlugin(NULL), m_dwTotalStepCounter(0), m_dwBlockSize(0), m_dwDataSize(
					0), m_pFifoBlock(NULL), m_pPluginBuffer(NULL), m_dwStepSize(
					0), m_sfSampleRate(
					0), m_fUseFrames(false), m_wOutputNumber(0), m_pDataStartPosition(
					NULL)
	{
	}

	void HostWrapper::printFeatures(int frame, int sr, int output,
			Plugin::FeatureSet features, bool useFrames)
	{

		for (unsigned int i = 0; i < features[output].size(); ++i)
		{
			std::stringstream ssfeature;

			if (useFrames)
			{

				int displayFrame = frame;

				if (features[output][i].hasTimestamp)
				{
					displayFrame = RealTime::realTime2Frame(
							features[output][i].timestamp, sr);
				}

				ssfeature << displayFrame;

				if (features[output][i].hasDuration)
				{
					displayFrame = RealTime::realTime2Frame(
							features[output][i].duration, sr);
					ssfeature << "," << displayFrame;
				}

				ssfeature << ":";

			}
			else
			{

				RealTime rt = RealTime::frame2RealTime(frame, sr);

				if (features[output][i].hasTimestamp)
				{
					rt = features[output][i].timestamp;
				}

				ssfeature << rt.toString();

				if (features[output][i].hasDuration)
				{
					rt = features[output][i].duration;
					ssfeature << "," << rt.toString();
				}

				ssfeature << ":";
			}

			for (unsigned int j = 0; j < features[output][i].values.size(); ++j)
			{
				ssfeature << " " << features[output][i].values[j];
			}
			ssfeature << " " << features[output][i].label;

			COUT(ssfeature.str());
		}
	}

	void HostWrapper::reset()
	{
		m_dwTotalStepCounter = 0;
		m_rtCurrentTime = RealTime::frame2RealTime(0, m_sfSampleRate);
		m_CurrentPlugin->reset();
	}

	bool HostWrapper::loadPlugin(string myname, string soname, string id,
			string output, int outputNo, bool useFrames, float sfSampleRate)
	{
		Plugin* plugin = NULL;
		plugin = createPlugin(myname, soname, id, sfSampleRate);
		if (!plugin)
		{
			CERR(
					myname << "1: ERROR: Failed to load plugin \"" << id << "\" from library \"" << soname << "\"" << endl);
//		if (out)
//		{
//			out->close();
//			delete out;
//		}
			return false;
		}
		DELETE_POINTER(m_CurrentPlugin);
		m_CurrentPlugin = plugin;

		COUT(
				"Running plugin: \"" << plugin->getIdentifier() << "\"..." << endl);

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

		m_dwBlockSize = plugin->getPreferredBlockSize();
		m_dwStepSize = plugin->getPreferredStepSize();
		m_fUseFrames = useFrames;
		m_sfSampleRate = sfSampleRate;
		if (m_dwBlockSize == 0)
		{
			m_dwBlockSize = 1024;
		}
		if (m_dwStepSize == 0)
		{
			if (plugin->getInputDomain() == Plugin::FrequencyDomain)
			{
				m_dwStepSize = m_dwBlockSize / 2;
			}
			else
			{
				m_dwStepSize = m_dwBlockSize;
			}
		}
		else if (m_dwStepSize > m_dwBlockSize)
		{
			CERR(
					"WARNING: stepSize " << m_dwStepSize << " > blockSize " << m_dwBlockSize << ", resetting blockSize to ");
			if (plugin->getInputDomain() == Plugin::FrequencyDomain)
			{
				m_dwBlockSize = m_dwStepSize * 2;
			}
			else
			{
				m_dwBlockSize = m_dwStepSize;
			}
			CERR(m_dwBlockSize << endl);
		}

		m_pFifoBlock = new float[m_dwBlockSize];
		m_pPluginBuffer = new float*[1];
		m_pPluginBuffer[0] = new float[m_dwBlockSize + 2];

		COUT(
				"Using block size = " << m_dwBlockSize << ", step size = " << m_dwStepSize << endl);

		// The channel queries here are for informational purposes only --
		// a PluginChannelAdapter is being used automatically behind the
		// scenes, and it will take case of any channel mismatch

		int minch = plugin->getMinChannelCount();
		int maxch = plugin->getMaxChannelCount();

		Plugin::OutputList outputs = plugin->getOutputDescriptors();
		Plugin::OutputDescriptor od;

		int returnValue = 1;
		int progress = 0;

		PluginWrapper *wrapper = 0;
		m_rtAdjustment = RealTime::zeroTime;

		if (outputs.empty())
		{
			CERR("ERROR: Plugin has no outputs!" << endl);
			DELETE_POINTER(plugin);
			return false;
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
				CERR(
						"ERROR: Non-existent output \"" << output << "\" requested" << endl);
				DELETE_POINTER(plugin);
				return false;
			}

		}
		else
		{

			if (int(outputs.size()) <= outputNo)
			{
				CERR(
						"ERROR: Output " << outputNo << " requested, but plugin has only " << outputs.size() << " output(s)" << endl);
				DELETE_POINTER(plugin);
				return false;
			}
		}
		m_wOutputNumber = outputNo;
		od = outputs[outputNo];

		if (!plugin->initialise(1, m_dwStepSize, m_dwBlockSize))
		{
			CERR(
					"ERROR: Plugin initialise (channels = " << 1 << ", stepSize = " << m_dwStepSize << ", blockSize = " << m_dwBlockSize << ") failed." << endl);
			DELETE_POINTER(plugin);
			return false;
		}

		wrapper = dynamic_cast<PluginWrapper *>(plugin);
		if (wrapper)
		{
			// See documentation for
			// PluginInputDomainAdapter::getTimestampAdjustment
			PluginInputDomainAdapter *ida = wrapper->getWrapper<
					PluginInputDomainAdapter>();
			if (ida)
				m_rtAdjustment = ida->getTimestampAdjustment();
		}

		return true;
	}

	word HostWrapper::toFloatBuffer(word* pCurrentDataPosition,
			float* pDataBuffer,
			word wWantedSamples, bool& o_fIsBufferEmpty) const
			{
		o_fIsBufferEmpty = false;
		byte* pCurrentDataPositionAsBytes = (byte*) pCurrentDataPosition;
		byte* pContentBaseAddressAsBytes = (byte*) m_pDataStartPosition;
		if ((pCurrentDataPositionAsBytes < pContentBaseAddressAsBytes)
				|| (pCurrentDataPositionAsBytes
						>= (pContentBaseAddressAsBytes + m_dwDataSize)))
		{
			o_fIsBufferEmpty = true;
			return 0;
		}

		if (((pCurrentDataPositionAsBytes + (wWantedSamples * sizeof(word)))
				> (pContentBaseAddressAsBytes + m_dwDataSize)))
		{
			wWantedSamples = (word) ((dword) (pContentBaseAddressAsBytes
					+ m_dwDataSize) - (dword) pCurrentDataPositionAsBytes)
					/ sizeof(word);
		}
		else if (((pCurrentDataPositionAsBytes + (wWantedSamples * sizeof(word)))
				== (pContentBaseAddressAsBytes + m_dwDataSize)))
		{
			o_fIsBufferEmpty = true;
		}

		for (int i = 0; i < wWantedSamples; i++)
		{
			pDataBuffer[i] = ((float) (((double) pCurrentDataPosition[i] - 0x7fff)
					/ 0x7fff));
		}

		return wWantedSamples;
	}

	bool HostWrapper::runPlugin(word* filebuf, dword dwLength)
	{
		word* pCurrentSample = filebuf;
		int overlapSize = m_dwBlockSize - m_dwStepSize;
		int finalStepsRemaining = max<dword>(1,
				(m_dwBlockSize / m_dwStepSize) - 1); // at end of file, this many part-silent frames needed after we hit EOF
		bool fDataBufferReadComplete;
		m_dwDataSize = dwLength;
		m_pDataStartPosition = (word*) filebuf;
		float sfSampleRate = m_sfSampleRate;
		// Here we iterate over the frames, avoiding asking the numframes in case it's streaming input.
		do
		{
			// Get the current step and progress the total counter.
			dword dwCurrentStep = m_dwTotalStepCounter++;

			dword dwCount, dwOriginalCount;

			if ((m_dwBlockSize == m_dwStepSize) || (dwCurrentStep == 0))
			{
				// read a full fresh block
				if ((dwOriginalCount = toFloatBuffer(pCurrentSample,
						m_pFifoBlock,
						m_dwBlockSize, fDataBufferReadComplete)) < 0)
				{
					CERR("ERROR: sf_readf_float failed: " << endl);
					break;
				}

				if (fDataBufferReadComplete)
				{
					--finalStepsRemaining;
					break;
				}

				dwCount = dwOriginalCount;
			}
			else
			{
				//  otherwise shunt the existing data down and read the remainder.
				memmove(m_pFifoBlock, m_pFifoBlock + (m_dwStepSize),
						overlapSize * sizeof(float));
				if ((dwOriginalCount = toFloatBuffer(pCurrentSample,
						m_pFifoBlock + (overlapSize), m_dwStepSize,
						fDataBufferReadComplete)) < 0)
				{
					CERR("ERROR: sf_readf_float failed: " << endl);
					break;
				}

				if (fDataBufferReadComplete)
				{
					--finalStepsRemaining;
				}
				dwCount = dwOriginalCount + overlapSize;
			}

			// This copies the buffer to a 'masked' one.
			// Later on test if this is necessary.
			memcpy(m_pPluginBuffer[0], m_pFifoBlock, dwCount * sizeof(float));
			memset(m_pPluginBuffer[0] + dwCount, 0,
					(m_dwBlockSize - dwCount) * sizeof(float));

			m_rtCurrentTime = RealTime::frame2RealTime(
					m_dwTotalStepCounter * m_dwStepSize, sfSampleRate);

			m_CurrentPlugin->process(m_pPluginBuffer, m_rtCurrentTime);

			pCurrentSample += m_dwStepSize;

		} while (!fDataBufferReadComplete);

		return true;
	}

	Plugin::FeatureSet HostWrapper::extractRemainingFeatures()
	{
		Plugin::FeatureSet featureSet = m_CurrentPlugin->getRemainingFeatures();
#ifdef DEBUG_PRINT_HOSTWRAPPER_PRINT_FEATURES
		printFeatures(
				RealTime::realTime2Frame(m_rtCurrentTime + m_rtAdjustment,
						m_sfSampleRate), m_sfSampleRate, m_wOutputNumber,
				featureSet, m_fUseFrames);
#endif
		m_CurrentPlugin->reset();
		reset();

		return featureSet;
	}

_VAMP_SDK_HOSTSPACE_END(PluginWrapper.h)
