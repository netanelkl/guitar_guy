package com.mad.tunerapp.activities;

import android.os.Bundle;

import com.mad.lib.activities.WelcomeActivityBase;
import com.mad.tunerlib.activities.TunerActivity;

public class TunerWelcomeActivity extends WelcomeActivityBase
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState, TunerActivity.class);
	}
}
