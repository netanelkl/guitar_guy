#ifndef VAMPMANAGER_H
#define VAMPMANAGER_H
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

#include <vamp-hostsdk/PluginHostAdapter.h>
#include <vamp-hostsdk/PluginInputDomainAdapter.h>
#include <vamp-hostsdk/PluginLoader.h>

#include <iostream>
#include <fstream>
#include <set>
//#include <sndfile.h>

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

enum Verbosity
{
	PluginIds, PluginOutputIds, PluginInformation, PluginInformationDetailed
};

class WindowsHostManager
{
public:
	void usage(const char *name);
	void printFeatures(int, int, int, Plugin::FeatureSet, bool frames);
	void transformInput(float *, size_t);
	void fft(unsigned int, bool, double *, double *, double *, double *);
	void printPluginPath(bool verbose);
	void printPluginCategoryList();
	void enumeratePlugins(Verbosity);
	void listPluginsInLibrary(string soname);
	int runPlugin(string myname, string soname, string id, string output,
			int outputNo, string inputFile, string outfilename, bool frames);
	int fileToFloatBuffer(word* pFileDataOffset, float* pDataBuffer, word wBlockSize);
	int shellMain(int argc, char **argv);
	static string header(string text, int level);
	word toFloatBuffer(word* pFileDataOffset, float* pDataBuffer,
			word wSamples) const;

private:
	CWave wavFileLoader;
	Plugin* m_CurrentPlugin;
	RealTime adjustment;
	dword m_dwBlockSize;
	dword m_dwStepSize;
	float m_sfSampleRate;
	bool m_fUseFrames;
	word m_wOutputNumber;
	float* m_pDataStartPosition;
	dword m_dwDataSize;
	// This will be initialized when the plugin's craeted to the block size.
	float* m_pFifoBlock;
	float** m_pPluginBuffer;
};

#endif
