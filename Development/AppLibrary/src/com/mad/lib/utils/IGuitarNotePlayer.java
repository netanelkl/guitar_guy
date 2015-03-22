package com.mad.lib.utils;

import com.mad.lib.musicalBase.IHandPositioning;

public interface IGuitarNotePlayer
{
	public void playOpenString(int nString);

	public void playNote(int nAbsoluteIndex);

	public void playIHandPositioning(IHandPositioning handPositioning);
}
