package com.mad.metronomeapp.activities;

import android.os.Bundle;

import com.mad.lib.activities.WelcomeActivityBase;
import com.mad.metronomelib.activities.MetronomeActivity;

public class MetronomeWelcomeActivity extends
		WelcomeActivityBase
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState,
				MetronomeActivity.class);
	}
}
