#ifndef HOST_WRAPPER_H
#define HOST_WRAPPER_H

#include <General/FXGGeneral.h>
#include <vamp-hostsdk/Plugin.h>
#include <vamp-hostsdk/PluginWrapper.h>
#include <cstring>
using namespace std;

using namespace Vamp::HostExt;
using namespace Vamp;

_VAMP_SDK_HOSTSPACE_BEGIN(HostWrapper.h)
#define STEPS_BEFORE_PARSING 60
class HostWrapper
{
public:

	HostWrapper();

	void printFeatures(int frame, int sr, int output,
			Plugin::FeatureSet features, bool useFrames);

	bool loadPlugin(string myname, string soname, string id, string output,
			int outputNo, bool useFrames, float sfSampleRate);

	/**
	 * Runs the plugin.
	 *
	 * Running the steps within blocks for as long as the data.
	 *
	 *	@param fileBug The sample buffer.
	 *	#param dwLength The length in bytes.
	 */
	bool runPlugin(word* filebuf, dword dwLength);

	void reset();

	word toFloatBuffer(word* pFileDataOffset, float* pDataBuffer,
			word wSamples, bool& o_fIsBufferEmpty) const;

	Plugin::FeatureSet extractRemainingFeatures();

	virtual ~HostWrapper()
	{
		DELETE_POINTER(m_CurrentPlugin);
		DELETE_ARRAY_POINTER(m_pFifoBlock);
		if(m_pPluginBuffer != NULL)
		{
			DELETE_ARRAY_POINTER(m_pPluginBuffer[0]);
		}
		DELETE_ARRAY_POINTER(m_pPluginBuffer);
	}
protected:

	virtual Plugin* createPlugin(string myname, string soname, string id,
			float sampleRate) = 0;

	Plugin* m_CurrentPlugin;
	RealTime m_rtAdjustment;
	RealTime m_rtCurrentTime;
	dword m_dwBlockSize;
	dword m_dwStepSize;
	float m_sfSampleRate;
	bool m_fUseFrames;
	word m_wOutputNumber;
	word* m_pDataStartPosition;
	dword m_dwDataSize;
	dword m_dwTotalStepCounter;
	// This will be initialized when the plugin's craeted to the block size.
	float* m_pFifoBlock;
	float** m_pPluginBuffer;

};

_VAMP_SDK_HOSTSPACE_END(PluginWrapper.h)

#endif
