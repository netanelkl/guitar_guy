package com.mad.guitarteacher.display.graphics.popups;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.mad.guitarteacher.display.graphics.PauseMenuButton;
import com.mad.guitarteacher.display.graphics.animation.AnimatedDrawableCollection;
import com.mad.guitarteacher.display.graphics.animation.IShowHideDrawable;
import com.mad.guitarteacher.display.graphics.animation.OnHiddenListener;
import com.mad.guitarteacher.display.graphics.animation.OnShownListener;
import com.mad.guitarteacher.display.graphics.animation.stars.EmptyStar;
import com.mad.guitarteacher.display.graphics.animation.stars.FullStar;
import com.mad.guitarteacher.display.graphics.animation.stars.Star;
import com.mad.guitarteacher.practice.ExerciseDoneReport;

public class ExerciseDonePopup extends PausingMenu
{
	private static final float	STAR_TOP_IN_DRAWING_SPACE	=
																	0.4f;
	private static final int	TOTAL_STARS					= 3;
	private static int[]		s_arButtons					= {
			PauseMenuButton.BUTTON_TYPE_NEXT_STAGE,
			PauseMenuButton.BUTTON_TYPE_RESTART,
			PauseMenuButton.BUTTON_TYPE_EXERCISES,
			PauseMenuButton.BUTTON_TYPE_HOME				};

	public static void staticInit(final Context context)
	{
		FullStar.staticInit(context);
		EmptyStar.initialize(context);
	}

	private final AnimatedDrawableCollection	m_arStars	=
																	new AnimatedDrawableCollection();

	public ExerciseDonePopup(	final Context context,
								final float sfCenterLeft,
								final float sfCenterTop)
	{
		super(context, sfCenterLeft, sfCenterTop, s_arButtons);
	}

	@Override
	public void draw(final Canvas canvas)
	{
		super.draw(canvas);
		m_arStars.draw(canvas);
	}

	public void show(	final ExerciseDoneReport report,
						final boolean fShowStars)
	{
		if (fShowStars)
		{
			int nStars = report.getStars();
			int nStarIndex = 0;
			float sfTotalWidth =
					m_rectDrawingSpace.width() * 0.75f;
			float sfSinglePadding =
					(sfTotalWidth - (TOTAL_STARS * FullStar
							.getStarWidth()))
							/ (TOTAL_STARS - 1);

			PointF pntCurrentStar = new PointF();
			for (nStarIndex = 0; nStarIndex < TOTAL_STARS; nStarIndex++)
			{
				float sfCurrentStarX =
						(m_rectDrawingSpace.centerX() - (sfTotalWidth / 2));
				if (nStarIndex > 0)
				{
					sfCurrentStarX +=
							nStarIndex
									* (FullStar.getStarWidth() + sfSinglePadding);
				}

				float sfStarTop =
						((m_rectDrawingSpace.top + (STAR_TOP_IN_DRAWING_SPACE * m_rectDrawingSpace
								.height())))
								- (FullStar.getStarHeight() / 2);
				pntCurrentStar.set(sfCurrentStarX, sfStarTop);

				Star star;
				if (nStarIndex < nStars)
				{
					star = new FullStar(pntCurrentStar);
				}
				else
				{
					star = new EmptyStar(pntCurrentStar);
				}
				m_arStars.add(star);
				star.show();
			}
		}
		super.show(new OnShownListener()
		{

			@Override
			public void onShown(IShowHideDrawable drawable)
			{
				if (fShowStars)
				{
					m_arStars.show(null);
				}

			}
		});
	}

	@Override
	public void hide(	boolean fViolent,
						final OnHiddenListener listener)
	{
		m_arStars.hide(false, new OnHiddenListener()
		{

			@Override
			public void onHidden(IShowHideDrawable drawable)
			{
				ExerciseDonePopup.super.hide(false,
						new OnHiddenListener()
						{

							@Override
							public void onHidden(IShowHideDrawable drawable)
							{
								listener.onHidden(drawable);
							}
						});
			}
		});
	}
}
