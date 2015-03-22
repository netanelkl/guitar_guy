package com.mad.lib.utils;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class TimeSpan implements Serializable
{
	/**
	 * 
	 */
	static final int	MILLIS_IN_SECOND	= 1000;
	static final int	SECONDS_IN_MINUTE	= 60;
	static final int	MINUTES_IN_HOUR		= 60;

	long				m_lTimeSpan;

	public TimeSpan()
	{
		m_lTimeSpan = 0;
	}

	public TimeSpan(long lStartTime, long lEndTime)
	{
		setSpan(lStartTime, lEndTime);
	}

	void setSpan(Date startTime, Date endTime)
	{
		m_lTimeSpan = endTime.getTime() - startTime.getTime();
	}

	void setSpan(long lStartTime, long lEndTime)
	{
		m_lTimeSpan = lEndTime - lStartTime;
	}

	public int getSeconds()
	{
		return (int) (m_lTimeSpan / MILLIS_IN_SECOND)
				% SECONDS_IN_MINUTE;
	}

	public int getMinutes()
	{
		return (int) (m_lTimeSpan / (MILLIS_IN_SECOND * SECONDS_IN_MINUTE))
				% MINUTES_IN_HOUR;
	}

	public double getTotalSeconds()
	{
		return (int) ((1.0 * m_lTimeSpan) / MILLIS_IN_SECOND);
	}

	public boolean add(TimeSpan other)
	{
		if (other == null)
		{
			return false;
		}

		m_lTimeSpan += other.m_lTimeSpan;

		return true;
	}
}
