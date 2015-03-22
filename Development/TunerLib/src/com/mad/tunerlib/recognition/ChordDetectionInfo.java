package com.mad.tunerlib.recognition;

import java.util.ArrayList;

public class ChordDetectionInfo extends DetectionInfo

{
	ArrayList<SingleChordDetectionInfo>	m_arReceivedChords	=
																	new ArrayList<SingleChordDetectionInfo>();

	public void add(SingleChordDetectionInfo currentInfo)
	{
		m_arReceivedChords.add(currentInfo);
	}

	public int size()
	{
		return m_arReceivedChords.size();
	}

	public ArrayList<SingleChordDetectionInfo> getChords()
	{
		return m_arReceivedChords;
	}
}
