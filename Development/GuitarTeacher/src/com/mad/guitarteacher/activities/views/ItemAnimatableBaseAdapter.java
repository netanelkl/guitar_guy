package com.mad.guitarteacher.activities.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.activities.ExercisePickerActivity.OnListGoneListener;
import com.mad.guitarteacher.practice.IReadOnlyExerciseStage;
import com.mad.guitarteacher.utils.Definitions;
import com.mad.lib.display.graphics.GraphicsPoint;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * Abstract class for allowing appearance/disappearance of the elements in the
 * (horizontal) list.
 * 
 * @author Nati
 * 
 */
public abstract class ItemAnimatableBaseAdapter extends
		BaseAdapter
{
	/**
	 * These three are proportion related information to now where we should
	 * draw the contents of the child.
	 */
	protected static final int		DRAWING_HEIGHT		= 307;
	protected static final int		DRAWING_WIDTH		= 420;
	private static final int		ORIGINAL_WIDTH		= 600;
	private Runnable				m_onHidingStarted	= null;
	/**
	 * The current state of the list.
	 * 
	 * Options are: (1) Showing: List is shown or in the process of showing. (2)
	 * Hiding: List is hidden or in the process of hiding.
	 * 
	 * This will help us determine which animations to start.
	 */
	protected boolean				m_fShowing			= true;

	/**
	 * The listener to call when all items are gone.
	 */
	protected OnListGoneListener	m_ListGoneListener;

	/**
	 * Used to know when all if the list items have disappeared.
	 */
	private int						m_nGoneCounter		= 0;

	/**
	 * Hold the layout xml of an item.
	 */
	private final int				m_nResIDListItem;

	/**
	 * Items are disappearing in a wave fashion: Meaning they start from one
	 * element and disappear with the ones near and so forth. This will be
	 * determined by the item clicked.
	 */
	private int						m_nWaveBase			= -1;

	/**
	 * Create a new ItemAnimatableBaseAdapter.
	 * 
	 * @param nListItemResID
	 *            The layout xml id for a single list item..
	 */
	public ItemAnimatableBaseAdapter(final int nListItemResID)
	{
		m_nResIDListItem = nListItemResID;
	}

	/**
	 * Animate the current view to show/hide.
	 * 
	 * @param nItemIndex
	 *            The item index.
	 * @param view
	 *            The view of the item to animate.
	 */
	private void animate(	final Context context,
							final int nItemIndex,
							final View view)
	{
		int animId = R.anim.appear_linear;
		if (!m_fShowing)
		{
			animId = R.anim.disappear_linear;
		}

		Animation appearAnimation =
				AnimationUtils.loadAnimation(context, animId);

		// Add a delay for each item. Including the first one.
		appearAnimation
				.setAnimationListener(new AnimationListener()
				{

					@Override
					public void onAnimationEnd(final Animation animation)
					{
						if (!m_fShowing)
						{
							view.setVisibility(View.INVISIBLE);
							m_nGoneCounter++;
							if (m_nGoneCounter == 0)
							{
								onListGone();
							}
							// imgUnique.setVisibility(View.GONE);
						}
					}

					@Override
					public void onAnimationRepeat(final Animation animation)
					{
					}

					@Override
					public void onAnimationStart(final Animation animation)
					{

					}
				});

		int nWaveBase = m_nWaveBase >= 0 ? m_nWaveBase : 0;
		appearAnimation.setStartOffset((int) (appearAnimation
				.getDuration()
				* (Math.abs(nWaveBase - nItemIndex)) * 0.5f));

		view.startAnimation(appearAnimation);

		if (!m_fShowing)
		{
			m_nGoneCounter--;
		}
	}

	/**
	 * Manipulates a view in the process of its creation.
	 * 
	 * @param nItemIndex
	 *            The index of the item to add in the adapter.
	 * @param view
	 *            The added view.
	 * @param pntScale
	 *            The screen scale, relative to 1680*1050.
	 * @param nDrawingWidth
	 *            The width given to draw inner content.
	 * @param nDrawingHeight
	 *            The height given to draw inner content.
	 */
	protected abstract void createView(	final int nItemIndex,
										final View view,
										int nDrawingWidth,
										int nDrawingHeight);

	@Override
	public Object getItem(final int position)
	{
		return null;
	}

	@Override
	public long getItemId(final int position)
	{
		return 0;
	}

	boolean isDisabled(int position)
	{
		return false;
	}

	Bitmap	bmpBGOriginal;

	@Override
	public View getView(final int index,
						final View convertView,
						final ViewGroup parent)
	{
		Context context = parent.getContext();
		View retVal = convertView;

		// Get the scale of the screen
		PointF pntScale = GraphicsPoint.getScalePoint();

		if (convertView == null)
		{
			retVal =
					LayoutInflater.from(context).inflate(
							m_nResIDListItem, null);

			ViewGroup viewRoot =
					(ViewGroup) retVal
							.findViewById(R.id.list_item);
			LayoutParams params = viewRoot.getLayoutParams();
			if (params == null)
			{
				params = new RelativeLayout.LayoutParams(0, 0);
			}
			params.width = (int) (ORIGINAL_WIDTH / pntScale.x);
			params.height = LayoutParams.MATCH_PARENT;
			viewRoot.setLayoutParams(params);
			bmpBGOriginal =
					DisplayHelper
							.getScaledBitmap(
									viewRoot.getResources(),
									R.drawable.exercisepicker_btn_field_rec,
									true);

		}

		// Create the bg image which is always there, and the click
		// handlers.
		final ImageView imgBG =
				(ImageView) retVal.findViewById(R.id.image_bg);
		if (!isDisabled(index))
		{
			imgBG.setImageBitmap(bmpBGOriginal);
			imgBG.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(final View v)
				{
					v.setOnClickListener(null);
					itemClicked(index);
				}
			});
		}
		else
		{
			// Canvas canvas = new Canvas(mutableBitmap);
			// We are in disabled mode. Convert the bitmap to grayscale.
			Bitmap grayScaled =
					DisplayHelper.toGrayscale(bmpBGOriginal);
			imgBG.setImageBitmap(grayScaled);

			imgBG.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					handleDisabledClicked(index, imgBG
							.getContext());
				}
			});
		}
		createView(index, retVal,
				(int) (DRAWING_WIDTH / pntScale.x),
				(int) (DRAWING_HEIGHT / pntScale.y));

		animate(context, index, retVal);
		return retVal;
	}

	/**
	 * Hides the list.
	 * 
	 * Once called, the items will disappear.
	 */
	public void hide()
	{
		if (m_onHidingStarted != null)
		{
			m_onHidingStarted.run();
		}
		// We are changing the state to hiding and causing the redraw of all
		// elements.
		m_fShowing = false;
		m_nWaveBase = OnListGoneListener.NONE_SELECTED;
		notifyDataSetChanged();
	}

	/**
	 * Handles the event of an item clicked. Basically invoking the hiding of
	 * the list.
	 * 
	 * @param nItemIndex
	 *            The index of the item to disappear.
	 */
	protected void itemClicked(final int nItemIndex)
	{
		// Make sure we didnt start the process of hiding.
		if (m_fShowing)
		{
			if (m_onHidingStarted != null)
			{
				m_onHidingStarted.run();
			}
			m_fShowing = false;
			m_nWaveBase = nItemIndex;
			notifyDataSetChanged();
		}
	}

	/**
	 * Occurs when all of the list is gone.
	 * 
	 * It makes sure to trigger the listener of this adapter.
	 */
	public void onListGone()
	{
		if (m_ListGoneListener != null)
		{
			m_ListGoneListener.onListGone(m_nWaveBase);
		}
	}

	/**
	 * Set an event listener for onListGone event.
	 * 
	 * @param onGoneListener
	 *            The listener to call.
	 */
	public void setOnGoneListener(final OnListGoneListener onGoneListener)
	{
		m_ListGoneListener = onGoneListener;
	}

	public void setOnHidingStartedListener(final Runnable onHidingStartedListener)
	{
		m_onHidingStarted = onHidingStartedListener;
	}

	/**
	 * Handle the case that the disable button was clicked.
	 * 
	 * @param context
	 */
	protected void handleDisabledClicked(	int nIndex,
											Context context)
	{

	}

	protected void showDisabledReason(	Context context,
										IReadOnlyExerciseStage stage)
	{
		String strToastMessage =
				context.getString(R.string.stage_disabled_string);

		IReadOnlyExerciseStage stageDependency =
				stage.getDependencyInformation().getDependency()
						.getComposingStage();
		int nStars =
				Definitions.Scores.getStars(stage
						.getDependencyInformation()
						.getDependencyThresholdValue());
		strToastMessage =
				strToastMessage.replace("[STARS]",
						Integer.toString(nStars)).replace(
						"[LEVEL]", stageDependency.getName());
		int duration = Toast.LENGTH_SHORT;

		Toast toast =
				Toast.makeText(context, strToastMessage,
						duration);
		toast.show();
	}
}
