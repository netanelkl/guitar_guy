package com.mad.lib.display.pager;

import java.util.ArrayList;

/**
 * Class to provide user information.
 * 
 * @author Tom
 * 
 */
public class PagerPageCollection
{
	protected ArrayList<PagerPage>	m_Pages	=
													new ArrayList<PagerPage>();

	private boolean					m_fIsPrePlay;

	public PagerPageCollection()
	{
		m_Pages = new ArrayList<PagerPage>();
		m_fIsPrePlay = true;
	}

	public boolean getIsPrePlay()
	{
		return m_fIsPrePlay;
	}

	public void setIsPrePlay(boolean fIsPrePlay)
	{
		m_fIsPrePlay = fIsPrePlay;
	}

	public ArrayList<PagerPage> getPages()
	{
		return m_Pages;
	}

	public void addPage(PagerPage page)
	{
		m_Pages.add(page);
	}

	public void addPage(String title, String content)
	{
		PagerPage infoPage = new PagerPage(title, content);
		addPage(infoPage);
	}
}
