#ifndef WAVE_H
#define WAVE_H

#include <General/FXGGeneral.h>

#pragma pack(1)
typedef struct __WAVEDESCR
{
	byte riff[4];
	dword size;
	byte wave[4];

} _WAVEDESCR, *_LPWAVEDESCR;

typedef struct __WAVEFORMAT
{
	byte id[4];
	dword size;
	short format;
	short channels;
	dword sampleRate;
	dword byteRate;
	short blockAlign;
	short bitsPerSample;

} _WAVEFORMAT, *_LPWAVEFORMAT;
#pragma pack()


class CWave
{
public:
	CWave(void);
	virtual ~CWave(void);

public:
	// Public methods
	bool Load(const char* lpszFilePath);
	bool Save(char* lpszFilePath);
	bool Play();
	bool Stop();
	bool Pause();
//	bool Mix(CWave& wave);
	bool IsValid()				{return (m_lpData != NULL);}
	bool IsPlaying()			{return (!m_bStopped && !m_bPaused);}
	bool IsStopped()			{return m_bStopped;}
	bool IsPaused()				{return m_bPaused;}
	byte* GetData()			{return m_lpData;}
	dword GetSize()				{return m_dwSize;}
	short GetChannels()			{return m_Format.channels;}
	dword GetSampleRate()		{return m_Format.sampleRate;}
	short GetBitsPerSample()	{return m_Format.bitsPerSample;}
	word ToFloatBuffer(word* pFileDataOffset, float* pDataBuffer, word wBlockSize);


private:
	// Pribate methods
	bool Open(short channels, dword sampleRate, short bitsPerSample);
	bool Close();

private:
	// Private members
	_WAVEDESCR m_Descriptor;
	_WAVEFORMAT m_Format;
	byte* m_lpData;
	dword m_dwSize;
	bool m_bStopped;
	bool m_bPaused;
};

#endif
