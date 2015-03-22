package com.mad.guitarteacher.practice.Acts;

import com.mad.guitarteacher.activities.IPlayingDisplayer;
import com.mad.lib.display.pager.OnDisplayInformationDone;
import com.mad.lib.display.pager.PagerPageCollection;

public class DisplayInformationAct extends ActBase implements
		OnDisplayInformationDone
{
	private final PagerPageCollection	m_StageInfo;

	public DisplayInformationAct(final PagerPageCollection stageInfo)
	{
		m_StageInfo = stageInfo;
	}

	@Override
	public void beginPlayingActImpl(final IPlayingDisplayer displayer)
	{
		displayer.displayInformation(m_StageInfo, this);

	}

	@Override
	public void onDisplayInformationDone(int nCause)
	{
		endPlayingAct(nCause == SIMULATION_BUTTON);
	}

}
