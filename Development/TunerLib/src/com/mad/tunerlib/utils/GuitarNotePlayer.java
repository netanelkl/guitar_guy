package com.mad.tunerlib.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.mad.lib.chordGeneration.EString;
import com.mad.lib.musicalBase.FretFingerPairBase;
import com.mad.lib.musicalBase.IHandPositioning;
import com.mad.lib.musicalBase.Notes;
import com.mad.lib.musicalBase.OctavedNote;
import com.mad.lib.utils.ErrorHandler;
import com.mad.lib.utils.IGuitarNotePlayer;
import com.mad.tunerlib.R;
import com.mad.tunerlib.musicalBase.FretFingerPair;

public class GuitarNotePlayer implements IGuitarNotePlayer
{
	static int[]			s_arAudioFiles					=
																	new int[] {
			R.raw.note0, R.raw.note1, R.raw.note2, R.raw.note3,
			R.raw.note4, R.raw.note5, R.raw.note6, R.raw.note7,
			R.raw.note8, R.raw.note9, R.raw.note10,
			R.raw.note11, R.raw.note12, R.raw.note13,
			R.raw.note14, R.raw.note15, R.raw.note16,
			R.raw.note17, R.raw.note18, R.raw.note19,
			R.raw.note20, R.raw.note21, R.raw.note22,
			R.raw.note23, R.raw.note24, R.raw.note25,
			R.raw.note26, R.raw.note27, R.raw.note28,
			R.raw.note29, R.raw.note30, R.raw.note31,
			R.raw.note32, R.raw.note33, R.raw.note34,
			R.raw.note35, R.raw.note36, R.raw.note37,
			R.raw.note38, R.raw.note39, R.raw.note40,
			R.raw.note41, R.raw.note42, R.raw.note43,
			R.raw.note44, R.raw.note45, R.raw.note46,
			R.raw.note47, R.raw.note48, R.raw.note49,
			R.raw.note50, R.raw.note51								};

	/********************************************
	 * Members declerations
	 ********************************************/
	private final SoundPool	m_cPlayer;
	int[]					m_arNoteSoundIds				=
																	new int[Notes.NUM_GUITAR_NOTES];
	int[]					m_arOpenStringsAbsoluteIndexs	=
																	new int[EString.NumberOfStrings];

	/********************************************
	 * Functions
	 ********************************************/
	public GuitarNotePlayer(Context context)
	{
		// Create the new sound pool.
		m_cPlayer =
				new SoundPool(	s_arAudioFiles.length,
								AudioManager.STREAM_MUSIC,
								0);

		for (int nNoteAbsoluteIndex = 0; nNoteAbsoluteIndex < Notes.NUM_GUITAR_NOTES; nNoteAbsoluteIndex++)
		{
			try
			{
				// Load each note sound to the player.
				m_arNoteSoundIds[nNoteAbsoluteIndex] =
						m_cPlayer
								.load(context,
										s_arAudioFiles[nNoteAbsoluteIndex],
										1);
			}
			catch (Throwable e)
			{
				ErrorHandler.HandleError(e);
			}
		}

		// Initialize the open strings absolute indexs array.
		OctavedNote[] arOpenStringNotes =
				{ new OctavedNote(Notes.E, 0),
						new OctavedNote(Notes.A, 0),
						new OctavedNote(Notes.D, 1),
						new OctavedNote(Notes.G, 1),
						new OctavedNote(Notes.B, 1),
						new OctavedNote(Notes.E, 2), };

		// Iterate over the open strings and get the absolute index of each open
		// string.
		for (int i = 0; i < EString.NumberOfStrings; i++)
		{
			m_arOpenStringsAbsoluteIndexs[i] =
					arOpenStringNotes[i].getAbsoluteIndex();
		}
	}

	@Override
	public void playOpenString(int nString)
	{
		if (nString < EString.NumberOfStrings)
		{
			playNote(m_arOpenStringsAbsoluteIndexs[nString]);
		}
	}

	@Override
	public void playNote(int nAbsoluteIndex)
	{
		m_cPlayer.play(m_arNoteSoundIds[nAbsoluteIndex], 1f, 1f,
				0, 0, 1f);
	}

	@Override
	public void playIHandPositioning(IHandPositioning handPositioning)
	{
		// Play each absolute index in the hand positioning.
		for (FretFingerPairBase fretFingerPair : handPositioning
				.getFingerPositions())
		{
			FretFingerPair pair =
					(FretFingerPair) fretFingerPair;
			playNote(pair.getAbsoluteNeckIndex());
		}
	}
}
