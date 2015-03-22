package com.mad.guitarteacher.activities.views;

import java.util.ArrayList;

import android.view.View;
import android.widget.TextView;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.practice.ExerciseStageOption;
import com.mad.guitarteacher.practice.IReadOnlyExerciseStage;

/**
 * An adapter to show the options of a stage. The meaning of options is after
 * you pick a stage, you have options for the stage (stage = Major chords,
 * Option = D chord)
 * 
 * @author Nati
 * 
 */
public class OptionsBaseAdapter extends
		ItemAnimatableBaseAdapter
{
	/**
	 * The array list containing the ExerciseStageOptions.
	 */
	ArrayList<ExerciseStageOption>	m_Options;

	/**
	 * Ctor to initialize basing on an exercising stage.
	 * 
	 * @param stage
	 *            The ExerciseStage to extract the options from.
	 */
	public OptionsBaseAdapter(final IReadOnlyExerciseStage stage)
	{
		super(R.layout.exercisepicker_exercises_list_item);
		m_Options =
				new ArrayList<ExerciseStageOption>(stage
						.getOptions());
	}

	@Override
	protected void createView(	final int nIndex,
								final View viewParentView,
								final int nDrawingWidth,
								final int nDrawingHeight)
	{
		ExerciseStageOption stage = m_Options.get(nIndex);

		String strResImage = stage.getParam();
		TextView tv =
				(TextView) viewParentView
						.findViewById(R.id.name);
		// View bgImg = viewParentView.findViewById(R.id.image_bg);
		tv.setTextSize(nDrawingHeight * 0.1f);
		tv.setMaxWidth(nDrawingWidth);
		tv.setMaxHeight(nDrawingHeight);
		tv.setText(strResImage);
	}

	@Override
	public int getCount()
	{
		return m_Options.size();
	}

	@Override
	public Object getItem(final int position)
	{
		return m_Options.get(position);
	}
}
