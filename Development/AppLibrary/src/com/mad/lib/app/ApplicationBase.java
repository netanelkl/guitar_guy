package com.mad.lib.app;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;

@ReportsCrashes(formKey = "",
		formUri = "https://guitarteacher.cloudant.com/acra-guitarteacher/_design/acra-storage/_update/report",
		reportType = org.acra.sender.HttpSender.Type.JSON,
		httpMethod = org.acra.sender.HttpSender.Method.PUT,
		formUriBasicAuthLogin = "utionsentenciredoolgaspe",
		formUriBasicAuthPassword = "7NMQRFRNVXHxO2VoCRqXweWJ",
		// Your usual ACRA configuration
		mode = ReportingInteractionMode.TOAST)
public class ApplicationBase extends Application
{
	public boolean isSupportingLogin()
	{
		return false;
	}

	@Override
	public void onCreate()
	{
		// The following line triggers the initialization of ACRA
		super.onCreate();
		// ACRA.init(this);
	}

	public boolean initServiceProvider(Context context)
	{
		return true;
	}
}
