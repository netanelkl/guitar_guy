package com.mad.lib.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.app.Activity;
import android.util.Log;

public class ErrorHandler
{

	public static void HandleError(	Exception ex,
									final Activity activity)
	{
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		ex.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();

		/*
		 * if (!ex.getClass().equals(ServiceException.class)) {
		 * 
		 * GlobalData.getInstance().getToastManager().notifyException(null);
		 * 
		 * IFindMeService findMeService =
		 * GlobalData.getInstance().getRESTService(); if (findMeService != null)
		 * { findMeService.reportError(ex.getMessage() + stacktrace); } }
		 */
		Log.e("fxErrors", ex.getMessage() + stacktrace);
	}

	public static void HandleError(String msg)
	{
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		new Throwable().printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();

		// GlobalData.getInstance().getRESTService().reportError(msg +
		// stacktrace);
		Log.e("fxErrors", msg + stacktrace);
	}

	public static void HandleError(Throwable e)
	{
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		/*
		 * if (!(e instanceof ServiceException)) {
		 * GlobalData.getInstance().getRESTService().reportError(e.getMessage()
		 * + stacktrace); }
		 */
		Log.e("fxErrors", e.getMessage() + stacktrace);
	}
}
