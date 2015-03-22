package com.mad.lib.display.pager;

import java.security.InvalidParameterException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mad.lib.R;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.lib.utils.ErrorHandler;

/**
 * This class is responsible for displaying popups on the screen.
 * 
 * It supports page switching. Accepts clicks and swift gestures. and can
 * display texts and images.
 * 
 * @author Nati
 * 
 */
public class PopupInformationDisplayer
{
	private static final int	ARROW_REPEAT_COUNT			= 3;
	private static final int	DURATION_ARROW_ANIMATION	=
																	1000;

	public enum EDisplayState
	{
		Shown,
		Dismissed,
		Hidden
	}

	/**
	 * Pager Adapter for managing the display of the StageInformationInfo.
	 * 
	 * @author Nati
	 * 
	 */
	private class InformationPagerAdapter extends PagerAdapter
	{
		private final class OnTouchListenerImplementation
				implements OnTouchListener
		{
			private final ViewPager	viewAsViewPager;
			PointF					pntDownClickLocation	=
																	new PointF();

			private OnTouchListenerImplementation(ViewPager viewAsViewPager)
			{
				this.viewAsViewPager = viewAsViewPager;
			}

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{

				// Save the location that the user started pressing to know
				// if he lift his finger at the same place.
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					pntDownClickLocation.x = event.getX();
					pntDownClickLocation.y = event.getY();
					return true;
				}
				if (event.getAction() != MotionEvent.ACTION_UP)
				{
					return false;
				}

				// This happens only if the user lift his finger here.
				float sfTouchX = event.getX();
				float sfViewWidth = v.getWidth();

				if ((Math.abs(sfTouchX - pntDownClickLocation.x) < CLICK_TRESHHOLD)
						&& (Math.abs(event.getY()
								- pntDownClickLocation.y) < CLICK_TRESHHOLD))
				{
					if (sfTouchX > ((1 - TOUCH_SPACE) * sfViewWidth))
					{
						viewAsViewPager
								.setCurrentItem(viewAsViewPager
										.getCurrentItem() + 1,
										true);
						return false;
					}
					else if (sfTouchX < (TOUCH_SPACE * sfViewWidth))
					{
						viewAsViewPager
								.setCurrentItem(viewAsViewPager
										.getCurrentItem() - 1,
										true);
						return false;
					}
				}
				return false;

			}
		}

		/**
		 * The information to display.
		 */
		private final PagerPageCollection	m_StageInfo;

		/**
		 * A click listener for the "Play" button press
		 */
		private final OnClickListener		m_OnLetMeClickListener;

		private final OnClickListener		m_OnShowMeHowListener;
		private static final float			TOUCH_SPACE		=
																	0.3f;

		/**
		 * The max pixels you can move away from the touch down to touch up
		 * position to consider it a click.
		 */
		private static final int			CLICK_TRESHHOLD	= 5;

		/**
		 * Constructor .
		 * 
		 * @param context
		 * @param stageInformationInfo
		 * @param onBtnPlayClicked
		 */
		public InformationPagerAdapter(	final Context context,
										final PagerPageCollection stageInformationInfo)
		{
			m_StageInfo = stageInformationInfo;
			m_OnLetMeClickListener = new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					btnLetMePlayClicked();
				}
			};
			m_OnShowMeHowListener = new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					btnShowMeHowClicked();
				}
			};
		}

		@Override
		public void destroyItem(final View view,
								final int arg1,
								final Object object)
		{
			((ViewPager) view).removeView((View) object);
		}

		@Override
		public int getCount()
		{
			if (m_StageInfo.getIsPrePlay())
			{
				return m_StageInfo.getPages().size() + 1;
			}
			else
			{
				return m_StageInfo.getPages().size();
			}
		}

		@Override
		public Object instantiateItem(	final View view,
										final int position)
		{
			final ViewPager viewAsViewPager = (ViewPager) view;
			boolean fRegularPage =
					position < m_StageInfo.getPages().size();

			int nResId;
			if (fRegularPage)
			{
				nResId = R.layout.game_popup_page_layout;
			}
			else if (m_stageInfo.getIsPrePlay())
			{
				nResId = R.layout.game_popup_page_layout_preplay;
			}
			else
			{
				ErrorHandler
						.HandleError(new InvalidParameterException());
				return view;
			}

			View currentView =
					LayoutInflater.from(view.getContext())
							.inflate(nResId, null);
			currentView
					.setOnTouchListener(new OnTouchListenerImplementation(viewAsViewPager));

			// Fix padding.
			DisplayHelper.scalePadding(currentView);

			// If we just passed the final page, show the play button.
			if (fRegularPage)
			{
				PagerPage page =
						m_StageInfo.getPages().get(position);

				TextView tvTitle =
						(TextView) currentView
								.findViewById(R.id.title);
				tvTitle.setText(page.getTitle());

				// If this is the last page. Show the close button.
				if (position == (m_stageInfo.getPages().size() - 1))
				{
					ImageView btnClose =
							(ImageView) currentView
									.findViewById(R.id.close);
					btnClose.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							dismiss(OnDisplayInformationDone.CLOSE_BUTTON);
						}
					});
					btnClose.setImageBitmap(DisplayHelper
							.getScaledBitmap(
									btnClose.getContext()
											.getResources(),
									R.drawable.close, true));
					btnClose.setVisibility(View.VISIBLE);
					DisplayHelper.scaleMargin(btnClose);
				}

				String strMessage = page.getMessage();
				if (strMessage != null)
				{
					TextView tvMessage =
							(TextView) currentView
									.findViewById(R.id.message);
					tvMessage.setText(page.getMessage());
				}

				if (page.getResId() != 0)
				{
					initPageImage(currentView, R.id.image, page
							.getResId(), null);
				}

			}
			else
			{
				initPageImage(currentView, R.id.let_me_play,
						R.drawable.game_popup_btn_play,
						m_OnLetMeClickListener);
				initPageImage(currentView, R.id.show_me_how,
						R.drawable.game_popup_btn_show_me_how,
						m_OnShowMeHowListener);
			}

			((ViewPager) view).addView(currentView);
			return currentView;
		}

		/**
		 * @param currentView
		 */
		private void initPageImage(	View currentView,
									int nResViewId,
									int nResDrawable,
									OnClickListener onClickListener)
		{
			ImageView imgButton =
					(ImageView) currentView
							.findViewById(nResViewId);
			imgButton
					.setImageBitmap(DisplayHelper
							.getScaledBitmap(
									currentView.getContext()
											.getResources(),
									nResDrawable, true));
			DisplayHelper.scaleMargin(imgButton);
			imgButton.setOnClickListener(onClickListener);
		}

		@Override
		public boolean isViewFromObject(final View arg0,
										final Object arg1)
		{
			return arg0 == arg1;

		}

		@Override
		public Parcelable saveState()
		{
			return null;
		}

	}

	private EDisplayState				m_eDisplayState	=
																EDisplayState.Dismissed;
	private OnDisplayInformationDone	m_handlerDisplayInformationDone;
	private PopupWindow					m_PopupWindow	= null;
	private PagerPageCollection			m_stageInfo		= null;
	private final View					m_RootView;
	private final SlowViewPager			m_viewViewPager;
	private final TextView				m_viewTitle;
	private final View					m_popUpRoot;
	private boolean						m_fIsAboutShown	= false;
	private final ImageView				m_viewArrowLeft;
	private final ImageView				m_viewArrowRight;
	private InformationPagerAdapter		m_adapterPager;
	private AlphaAnimation				m_animArrow;

	public boolean isShown()
	{
		return m_fIsAboutShown;
	}

	public PopupInformationDisplayer(	final Activity context,
										final View rootView)
	{
		Point screenDisplay = new Point();
		context.getWindowManager().getDefaultDisplay().getSize(
				screenDisplay);

		LayoutInflater inflater =
				(LayoutInflater) context.getBaseContext()
						.getSystemService(
								Context.LAYOUT_INFLATER_SERVICE);

		View viewPopup =
				inflater.inflate(R.layout.game_popup_layout,
						null);

		m_popUpRoot = viewPopup;

		DisplayHelper.scalePadding(viewPopup);
		DisplayHelper.scaleView(viewPopup, true);
		m_PopupWindow =
				new PopupWindow(viewPopup,
								(screenDisplay.x),
								(screenDisplay.y));

		ImageView viewImage =
				(ImageView) viewPopup.findViewById(R.id.image);

		DisplayHelper.scaleImageView(viewImage, false);
		DisplayHelper.scaleMargin(viewPopup
				.findViewById(R.id.pop_up_border_content));
		View popupRoot = m_PopupWindow.getContentView();

		// Set the views as members.
		m_viewTitle =
				(TextView) popupRoot.findViewById(R.id.title);
		// m_viewLeftBtn = popupRoot.findViewById(R.id.popup_btn_left);
		// m_viewRightBtn = popupRoot.findViewById(R.id.popup_btn_right);
		// m_viewContinueBtn = popupRoot.findViewById(R.id.popup_btn_continue);
		DisplayHelper.scaleMargin(m_viewTitle);
		DisplayHelper.scaleFontSize(m_viewTitle);
		m_viewViewPager =
				(SlowViewPager) popupRoot
						.findViewById(R.id.pager);
		DisplayHelper.scalePadding(m_viewViewPager);

		// Left arrow.
		m_viewArrowLeft =
				(ImageView) popupRoot
						.findViewById(R.id.arrow_left);
		DisplayHelper.scaleMargin(m_viewArrowLeft);
		DisplayHelper.scaleImageView(m_viewArrowLeft);

		// Right arrow
		m_viewArrowRight =
				(ImageView) popupRoot
						.findViewById(R.id.arrow_right);
		DisplayHelper.scaleMargin(m_viewArrowRight);
		DisplayHelper.scaleImageView(m_viewArrowRight);

		// Arrow animation.
		if (m_animArrow == null)
		{
			m_animArrow = new AlphaAnimation(0, 0.05f);
			m_animArrow.setRepeatCount(ARROW_REPEAT_COUNT);
			m_animArrow.setRepeatMode(Animation.REVERSE);
			m_animArrow.setDuration(DURATION_ARROW_ANIMATION);
			m_animArrow.setFillAfter(true);
		}
		final TextView tvHeader = m_viewTitle;
		tvHeader.setText("Page 1");

		m_viewViewPager
				.setOnPageChangeListener(new OnPageChangeListener()
				{

					@Override
					public void onPageScrolled(	final int arg0,
												final float arg1,
												final int arg2)
					{
					}

					@Override
					public void onPageScrollStateChanged(final int arg0)
					{
					}

					@Override
					public void onPageSelected(final int nPage)
					{
						String text;
						if (m_stageInfo.getIsPrePlay()
								&& ((m_viewViewPager
										.getAdapter().getCount() - 1) == nPage))
						{
							tvHeader.setVisibility(View.INVISIBLE);
						}
						else
						{
							onRegularPageDisplayed();
						}
					}

				});

		m_RootView = rootView;
	}

	private void onRegularPageDisplayed()
	{
		int nPage = m_viewViewPager.getCurrentItem();
		String text;
		int nCount = m_stageInfo.getPages().size();

		m_viewArrowLeft.clearAnimation();
		m_viewArrowRight.clearAnimation();
		m_viewArrowLeft.setVisibility(View.INVISIBLE);
		m_viewArrowRight.setVisibility(View.INVISIBLE);
		if (nPage < (nCount - 1))
		{
			m_viewArrowLeft.setVisibility(View.VISIBLE);
			m_viewArrowLeft.startAnimation(m_animArrow);
		}
		if ((nPage > 0) && (nCount > 0))
		{
			m_viewArrowRight.setVisibility(View.VISIBLE);
			m_viewArrowRight.startAnimation(m_animArrow);
		}

		text = "Page " + (nPage + 1);
		m_viewTitle.setText(text);
		m_viewTitle.setVisibility(View.VISIBLE);
	}

	/**
	 * @param view
	 */
	protected void dimmBackground()
	{
		AlphaAnimation alphaAnimation =
				new AlphaAnimation(0, 0.5F);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillAfter(true);
		View rectView =
				m_popUpRoot.findViewById(R.id.myRectangleView);
		rectView.startAnimation(alphaAnimation);
	}

	public void btnLetMePlayClicked()
	{
		dismiss(OnDisplayInformationDone.PLAY_BUTTON);
	}

	public void btnShowMeHowClicked()
	{
		dismiss(OnDisplayInformationDone.SIMULATION_BUTTON);
	}

	/**
	 * 
	 */
	private void loadPopup()
	{
		m_RootView.post(new Runnable()
		{
			@Override
			public void run()
			{
				m_viewArrowLeft.setVisibility(View.INVISIBLE);
				m_viewArrowRight.setVisibility(View.INVISIBLE);
				m_PopupWindow.showAtLocation(m_RootView,
						Gravity.CENTER, 0, 0);
				onRegularPageDisplayed();
			}
		});
		m_eDisplayState = EDisplayState.Shown;
	}

	/*******************************************************
	 * ACTIVITY_METHODS
	 ********************************************************/

	public void refreshState()
	{
		if (m_eDisplayState == EDisplayState.Shown)
		{
			m_PopupWindow.dismiss();
			loadPopup();
		}
	}

	public void setPagerInfo(final PagerPageCollection stageInfo)
	{
		m_stageInfo = stageInfo;
		m_adapterPager =
				new InformationPagerAdapter(m_RootView
						.getContext(), m_stageInfo);
		m_viewViewPager.setAdapter(m_adapterPager);
	}

	public void setOnDisplayDoneListener(final OnDisplayInformationDone onDisplayDone)
	{
		m_handlerDisplayInformationDone = onDisplayDone;
	}

	public void show()
	{
		if (m_stageInfo != null)
		{
			dimmBackground();

			loadPopup();
			m_fIsAboutShown = true;
		}
	}

	public EDisplayState getDisplayState()
	{
		return m_eDisplayState;
	}

	public void dismiss(int nCause)
	{
		m_eDisplayState = EDisplayState.Dismissed;
		m_fIsAboutShown = false;
		m_PopupWindow.dismiss();
		if (m_handlerDisplayInformationDone != null)
		{
			m_handlerDisplayInformationDone
					.onDisplayInformationDone(nCause);
		}
	}

	public void hide()
	{
		m_eDisplayState = EDisplayState.Hidden;
		m_fIsAboutShown = false;
		m_PopupWindow.dismiss();
	}

}
