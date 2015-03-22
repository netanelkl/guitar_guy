package com.mad.guitarteacher.display.graphics.popups;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.display.graphics.DisplayState;
import com.mad.guitarteacher.display.graphics.GraphicsDrawableBase;
import com.mad.guitarteacher.display.graphics.PauseMenuButton;
import com.mad.guitarteacher.display.graphics.TextDrawable;
import com.mad.guitarteacher.display.graphics.animation.AnimatedDrawableCollection;
import com.mad.guitarteacher.display.graphics.animation.FloatLinearAnimator;
import com.mad.guitarteacher.display.graphics.animation.IShowHideDrawable;
import com.mad.guitarteacher.display.graphics.animation.OnHiddenListener;
import com.mad.guitarteacher.display.graphics.animation.OnShownListener;
import com.mad.guitarteacher.display.graphics.animation.OvershootBitmapDrawable;

public class PausingMenu extends GraphicsDrawableBase implements
		IShowHideDrawable
{
	private class DarkBG extends FloatLinearAnimator
	{
		Paint	m_Paint	= new Paint();

		public DarkBG()
		{
			super(	0,
					0.5f,
					OvershootBitmapDrawable.ANIMATION_LENGTH);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void draw(final Canvas canvas)
		{
			if (m_DisplayState != DisplayState.Hidden)
			{
				int nColor =
						((int) (m_sfFloatLinearValue * 256)) << 24;
				m_Paint.setColor(nColor);
				canvas.drawRect(new Rect(	0,
											0,
											canvas.getWidth(),
											canvas.getHeight()),
						m_Paint);
			}
		}

	}

	private static final float					BORDER_PADDING			=
																				0.045f;							// 105
	private static final float					BUTTON_WIDTH			=
																				0.1777f;
	// private static final float BUTTONS_MARGIN = 0.0308f;
	private static final float					BUTTONS_TOP				=
																				0.5498f;							// 436
	private static final float					TEXT_HEIGHT				=
																				0.0557f;
	private static final float					TEXT_TOP				=
																				0.1f;
	protected final AnimatedDrawableCollection	m_arBorderCollection	=
																				new AnimatedDrawableCollection();
	// Manages tinting the background.
	private final ArrayList<PauseMenuButton>	m_arButtons				=
																				new ArrayList<PauseMenuButton>();
	protected final AnimatedDrawableCollection	m_arButtonsCollection	=
																				new AnimatedDrawableCollection();
	protected Rect								m_rectDrawingSpace		=
																				new Rect();
	private boolean								m_fIsButtonClicked		=
																				false;

	private final TextDrawable					m_textTitle;

	// private final SemiTransparentRectDrawable m_DrawingSpace;

	public PausingMenu(	final Context context,
						final float sfCenterLeft,
						final float sfCenterTop,
						final int[] arButtonTypesToShow)
	{
		// Create the border. Note that we get the original center point because
		// that's what the object expects.
		OvershootBitmapDrawable border =
				new OvershootBitmapDrawable(context,
											R.drawable.game_pause_menu_border,
											new PointF(	sfCenterLeft,
														sfCenterTop),
											true);

		// Add the bg and the border.
		m_arBorderCollection.add(new DarkBG());
		m_arBorderCollection.add(border);

		m_rectDrawingSpace.left =
				(int) ((sfCenterLeft - (border.getWidth() * (0.5 - BORDER_PADDING))));
		m_rectDrawingSpace.right =
				(int) ((sfCenterLeft + (border.getWidth() * (0.5 - BORDER_PADDING))));
		m_rectDrawingSpace.top =
				(int) ((sfCenterTop - (border.getHeight() * (0.5 - BORDER_PADDING))));
		m_rectDrawingSpace.bottom =
				(int) ((sfCenterTop + (border.getHeight() * (0.5 - BORDER_PADDING))));
		// m_DrawingSpace =
		// new SemiTransparentRectDrawable(m_rectDrawingSpace);

		int nTextTop =
				m_rectDrawingSpace.top
						+ (int) (TEXT_TOP * m_rectDrawingSpace
								.height());
		m_arButtonsCollection
				.add(m_textTitle =
						new TextDrawable(new Rect(	m_rectDrawingSpace.left,
													nTextTop,
													m_rectDrawingSpace.right,
													nTextTop
															+ (int) (TEXT_HEIGHT * m_rectDrawingSpace
																	.height()))));

		// Find the position for all of the buttons.
		float sfMargin =
				(1 - (BUTTON_WIDTH * arButtonTypesToShow.length))
						/ (arButtonTypesToShow.length * 2);

		for (int i = 0; i < arButtonTypesToShow.length; i++)
		{
			// Set the place to draw the button.
			// Add the padding.
			float sfX = (0);
			sfX +=
					(i * BUTTON_WIDTH)
							+ ((1 + (2 * i)) * (sfMargin));
			sfX *= m_rectDrawingSpace.width();
			PauseMenuButton pauseButton =
					new PauseMenuButton(context,
										arButtonTypesToShow[i],
										new PointF(	((m_rectDrawingSpace.left + sfX)),
													((m_rectDrawingSpace.top + (BUTTONS_TOP * border
															.getHeight())))));
			m_arButtons.add(pauseButton);
			m_arButtonsCollection.add(pauseButton);
		}
	}

	public void setTitle(String strTitle)
	{
		m_textTitle.setText(strTitle);
	}

	@Override
	public void draw(final Canvas canvas)
	{
		super.draw(canvas);
		m_arBorderCollection.draw(canvas);
		m_arButtonsCollection.draw(canvas);
		// m_DrawingSpace.draw(canvas);
	}

	@Override
	public DisplayState getDisplayState()
	{
		return m_arBorderCollection.getDisplayState();
	}

	public int handleTouchedWithIndex(	final Context context,
										final int nTouchX,
										final int nTouchY)
	{
		if (m_fIsButtonClicked)
		{
			return PauseMenuButton.BUTTON_TYPE_NONE;
		}
		for (PauseMenuButton pauseButton : m_arButtons)
		{
			if (pauseButton.handleTouched(context, nTouchX,
					nTouchY))
			{
				m_fInvalidate = true;
				m_fIsButtonClicked = true;
				return pauseButton.getButtonType();
			}
		}
		return PauseMenuButton.BUTTON_TYPE_NONE;
	}

	@Override
	public void hide(	final boolean fViolent,
						final OnHiddenListener listener)
	{
		m_arButtonsCollection.hide(fViolent,
				new OnHiddenListener()
				{
					@Override
					public void onHidden(IShowHideDrawable drawable)
					{
						m_arBorderCollection.hide(fViolent,
								listener);
					}
				});
	}

	@Override
	public void show(final OnShownListener listener)
	{
		m_fIsButtonClicked = false;
		m_arBorderCollection.show(new OnShownListener()
		{
			@Override
			public void onShown(IShowHideDrawable drawable)
			{
				m_arButtonsCollection.show(null);
			}
		});

	}

	@Override
	public boolean update()
	{
		// TODO Auto-generated method stub
		return m_arBorderCollection.update()
				|| m_arButtonsCollection.update();
	}
}
