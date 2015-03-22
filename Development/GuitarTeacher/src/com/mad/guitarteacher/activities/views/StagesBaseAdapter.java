package com.mad.guitarteacher.activities.views;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.guitarteacher.R;
import com.mad.lib.display.utils.DisplayHelper;
import com.mad.guitarteacher.practice.IReadOnlyExerciseField;
import com.mad.guitarteacher.practice.IReadOnlyExerciseStage;
import com.mad.guitarteacher.utils.Definitions;
import com.mad.lib.utils.ErrorHandler;

/**
 * This class is meant to implement retrieving the views for the Stages.
 * 
 * @author Nati
 * 
 */
public class StagesBaseAdapter extends ItemAnimatableBaseAdapter
{
	/**
	 * The current field for which we show the stages.
	 */
	private final IReadOnlyExerciseField			m_CurrentField;

	private final ArrayList<IReadOnlyExerciseStage>	m_arStages;

	@Override
	boolean isDisabled(int position)
	{
		return !m_arStages.get(position)
				.getDependencyInformation().isAvailable();
	}

	/**
	 * Ctor contating the field whose' stages we want to show.
	 * 
	 * @param field
	 */
	public StagesBaseAdapter(final IReadOnlyExerciseField field)
	{
		super(R.layout.exercisepicker_exercises_list_item);
		m_CurrentField = field;
		m_arStages = field.getInfo().getStages();
	}

	@Override
	protected void createView(	final int index,
								final View retVal,
								final int nDrawingWidth,
								final int nDrawingHeight)
	{
		IReadOnlyExerciseStage stage = m_arStages.get(index);

		// Set the text for the stage.
		String strStageName = stage.getName();

		TextView tv = (TextView) retVal.findViewById(R.id.name);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(float) ((nDrawingWidth) * 0.1));
		tv.setMaxWidth(nDrawingWidth);
		tv.setMaxHeight((nDrawingHeight));
		tv.setText(strStageName);

		// Set the stars / lock.
		final ImageView imgStars =
				(ImageView) retVal.findViewById(R.id.stars);
		int nResId;
		if (!isDisabled(index))
		{

			nResId = getStageStarsDrawableId(stage);
		}
		else
		{
			nResId = R.drawable.exercisepicker_lock;
		}
		imgStars.setImageBitmap(DisplayHelper.getScaledBitmap(
				imgStars.getResources(), nResId, true));
	}

	/**
	 * @param stage
	 */
	private int getStageStarsDrawableId(IReadOnlyExerciseStage stage)
	{
		int nStars =
				Definitions.Scores.getStars(stage.getMaxScore());
		int nId = 0;
		switch (nStars)
		{
			case 0:
			{
				nId = R.drawable.exercisepicker_stars_0;
				break;
			}
			case 1:
			{
				nId = R.drawable.exercisepicker_stars_1;
				break;
			}
			case 2:
			{
				nId = R.drawable.exercisepicker_stars_2;
				break;
			}
			case 3:
			{
				nId = R.drawable.exercisepicker_stars_3;
				break;
			}
			default:
			{
				ErrorHandler
						.HandleError(new InvalidParameterException());
				return 0;
			}
		}

		return nId;
	}

	@Override
	protected void handleDisabledClicked(	int nIndex,
											Context context)
	{
		IReadOnlyExerciseStage stageCurrent =
				m_arStages.get(nIndex);

		showDisabledReason(context, stageCurrent);
	}

	@Override
	public int getCount()
	{
		return m_arStages.size();
	}

	public IReadOnlyExerciseField getCurrentField()
	{
		return m_CurrentField;
	}
}
