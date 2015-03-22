#ifndef ANDROID_OS
#include "Wave.h"
#include <cmath>
#include <algorithm>
#include <cstdio>
CWave::CWave(void)
{
	// Init members
	memset(&m_Descriptor, 0, sizeof(_WAVEDESCR));
	memset(&m_Format, 0, sizeof(_WAVEFORMAT));
	m_lpData = NULL;
	m_dwSize = 0;
	m_bPaused = false;
	m_bStopped = true;
}

word CWave::ToFloatBuffer(word* pFileDataOffset, float* pDataBuffer,
		word wSamples)
{
	if (((byte*) pFileDataOffset < m_lpData)
			|| ((byte*) pFileDataOffset >= (m_lpData + m_dwSize)))
	{
		return 0;
	}

	if (((byte*) (pFileDataOffset + wSamples) > (m_lpData + m_dwSize)))
	{
		wSamples = (word) ((dword) (m_lpData + m_dwSize)
				- (dword) pFileDataOffset) / sizeof(word);
	}

	for (int i = 0; i < wSamples; i++)
	{
		pDataBuffer[i] = ((float) (((double) pFileDataOffset[i] - 0x7fff)
				/ 0x7fff));
	}

	return wSamples;
}

CWave::~CWave(void)
{
	// Close output device
	Close();
}

bool CWave::Close()
{
	bool bResult = true;

	// Check for valid sound data
	if (IsValid())
	{
		// Clear sound data buffer
		free(m_lpData);
		m_lpData = NULL;
		m_dwSize = 0;
	}

	return bResult;
}

bool CWave::Load(const char* lpszFilePath)
{
	bool bResult = false;

	// Close output device
	Close();

	// Load .WAV file
	FILE* file = fopen(lpszFilePath, "rb");
	if (file != NULL)
	{
		// Read .WAV descriptor
		fread(&m_Descriptor, sizeof(_WAVEDESCR), 1, file);

		// Check for valid .WAV file
		if (strncmp((const char*) m_Descriptor.wave, "WAVE", 4) == 0)
		{
			// Read .WAV format
			fread(&m_Format, sizeof(_WAVEFORMAT), 1, file);

			// Check for valid .WAV file
			if ((strncmp((const char*) m_Format.id, "fmt", 3) == 0)
					&& (m_Format.format == 1))
			{
				// Read next chunk
				byte id[4];
				dword size;
				fread(id, sizeof(byte), 4, file);
				fread(&size, sizeof(dword), 1, file);
				dword offset = ftell(file);

				// Read .WAV data
				byte* lpTemp = (byte*) malloc(m_Descriptor.size * sizeof(byte));
				while (offset < m_Descriptor.size)
				{
					// Check for .WAV data chunk
					if (strncmp((const char*) id, "data", 4) == 0)
					{
						if (m_lpData == NULL)
							m_lpData = (byte*) malloc(size * sizeof(byte));
						else
						{
							byte* pData = (byte*) realloc(m_lpData,
									(m_dwSize + size) * sizeof(byte));
							if (pData == NULL)
							{
								free(m_lpData);
								free(lpTemp);
								return false;
							}
							else
							{
								m_lpData = pData;
							}
						}

						fread(m_lpData + m_dwSize, sizeof(byte), size, file);
						m_dwSize += size;
						bResult = true;

					}
					else
						fread(lpTemp, sizeof(byte), size, file);

					// Read next chunk
					fread(id, sizeof(byte), 4, file);
					fread(&size, sizeof(dword), 1, file);
					offset = ftell(file);
				}
				free(lpTemp);

			}
		}

		// Close .WAV file
		fclose(file);
	}

	return bResult;
}

bool CWave::Save(char* lpszFilePath)
{
	bool bResult = false;

	// Save .WAV file
	FILE* file = fopen(lpszFilePath, "wb");
	if (file != NULL)
	{
		// Save .WAV descriptor
		m_Descriptor.size = m_dwSize;
		fwrite(&m_Descriptor, sizeof(_WAVEDESCR), 1, file);

		// Save .WAV format
		fwrite(&m_Format, sizeof(_WAVEFORMAT), 1, file);

		// Write .WAV data
		byte id[4] =
		{ 'd', 'a', 't', 'a' };
		fwrite(id, sizeof(byte), 4, file);
		fwrite(&m_dwSize, sizeof(dword), 1, file);
		fwrite(m_lpData, sizeof(byte), m_dwSize, file);
		bResult = true;

		// Close .WAV file
		fclose(file);
	}

	return bResult;
}

//bool CWave::Mix(CWave& wave)
//{
//	bool bResult = false;
//
//	// Check for valid sound data
//	if ((IsValid() && m_bStopped) && (wave.IsValid() && wave.IsStopped()))
//	{
//		// Check for valid sound format
//		if ((m_Format.channels == wave.GetChannels()) && (m_Format.sampleRate == wave.GetSampleRate()) && (m_Format.bitsPerSample == wave.GetBitsPerSample()))
//		{
//			// Mix .WAVs
//			long sampleSize = min(m_dwSize, wave.GetSize()) / (m_Format.bitsPerSample >> 3);
//			switch (m_Format.bitsPerSample)
//			{
//				case 8:
//					{
//						byte* lpSrcData = wave.GetData();
//						byte* lpDstData = m_lpData;
//						float gain = log10(20.0f);
//						for (long i=0; i<sampleSize; i++)
//						{
//							*lpDstData = (byte)(((*lpSrcData+*lpDstData)>>1)*gain);
//							lpSrcData++;
//							lpDstData++;
//						}
//					}
//					break;
//
//				case 16:
//					{
//						word* lpSrcData = (word*)wave.GetData();
//						word* lpDstData = (word*)m_lpData;
//						for (long i=0; i<sampleSize; i++)
//						{
//							float sample1 = (*lpSrcData - 32768) / 32768.0f;
//							float sample2 = (*lpDstData - 32768) / 32768.0f;
//							if (fabs(sample1*sample2) > 0.25f)
//								*lpDstData = (word)(*lpSrcData + *lpDstData);
//							else
//								*lpDstData = fabs(sample1) < fabs(sample2) ? *lpSrcData : *lpDstData;
//							lpSrcData++;
//							lpDstData++;
//						}
//					}
//					break;
//			}
//			bResult = true;
//		}
//	}
//
//	return bResult;
//}

#endif
