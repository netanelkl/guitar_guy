package com.mad.guitarteacher.display.views;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mad.guitarteacher.display.graphics.GraphicsDrawableBase;

public class GameSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback
{

	private class UpdatingTask extends TimerTask
	{
		@Override
		public void run()
		{
			if (m_Drawer != null)
			{
				if (m_Drawer.update())
				{
					postInvalidate();
				}
			}
		}
	}

	private SurfaceHolder			holder;
	private GraphicsDrawableBase	m_Drawer		= null;

	private final Timer				m_UpdatingTimer	=
															new Timer();

	public GameSurfaceView(final Context context)
	{
		this(context, null, 0);

	}

	public GameSurfaceView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public GameSurfaceView(	Context context,
							AttributeSet attrs,
							int defStyle)
	{
		super(context, attrs, defStyle);
		holder = getHolder();
		holder.addCallback(this);
		m_UpdatingTimer.scheduleAtFixedRate(new UpdatingTask(),
				0, 20);
	}

	public void setDrawingManager(GraphicsDrawableBase drawer)
	{
		m_Drawer = drawer;
	}

	public void destroy()
	{
		m_UpdatingTimer.cancel();
		holder = null;
		m_Drawer = null;
	}

	@Override
	protected void onDraw(final Canvas canvas)
	{
		super.onDraw(canvas);
		try
		{
			if (m_Drawer != null)
			{
				m_Drawer.draw(canvas);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (m_Drawer != null)
			{
				m_Drawer.handleTouched(getContext(), (int) event
						.getX(), (int) event.getY());
			}
		}
		return true;
	}

	@Override
	public void surfaceChanged(	final SurfaceHolder holder,
								final int format,
								final int width,
								final int height)
	{
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder)
	{
		setWillNotDraw(false);
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder)
	{
	}

}
