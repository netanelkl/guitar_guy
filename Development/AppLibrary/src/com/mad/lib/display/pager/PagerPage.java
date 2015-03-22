package com.mad.lib.display.pager;

public class PagerPage
{
	private final String	m_strTitle;

	private String			m_strMessage;

	private final int		m_nResId;

	public PagerPage(final String strTitle, final int nResId)
	{
		m_strTitle = strTitle;
		m_nResId = nResId;
	}

	public PagerPage(	final String strTitle,
						final String strMessage)
	{
		m_nResId = 0;
		m_strTitle = strTitle;
		m_strMessage = strMessage;
	}

	public String getMessage()
	{
		return m_strMessage;
	}

	public int getResId()
	{
		return m_nResId;
	}

	public String getTitle()
	{
		return m_strTitle;
	}
}
