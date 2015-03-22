package com.mad.guitarteacher.activities.views;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.mad.guitarteacher.R;
import com.mad.guitarteacher.practice.ExerciseField;
import com.mad.guitarteacher.practice.IReadOnlyExerciseField;
import com.mad.guitarteacher.practice.IReadOnlyExerciseStage;
import com.mad.lib.display.utils.DisplayHelper;

/**
 * Responsible for showing all of the field s.
 * 
 * @author Nati
 * 
 */
public class FieldsBaseAdapter extends ItemAnimatableBaseAdapter
{
	/**
	 * A reference to the list where all the fields data are in.
	 */
	private final ArrayList<IReadOnlyExerciseField>	m_Fields;

	/**
	 * Default Ctor.
	 * 
	 * @param fields
	 *            A reference to an array including an information about each of
	 *            the fields.
	 */
	public FieldsBaseAdapter(final ArrayList<IReadOnlyExerciseField> fields)
	{
		super(R.layout.exercisepicker_fields_list_item);
		this.m_Fields = fields;
	}

	@Override
	boolean isDisabled(int position)
	{
		return !m_Fields.get(position).isAvailable();
	}

	@Override
	protected void createView(	final int position,
								final View retVal,
								final int nDrawingWidth,
								final int nDrawingHeight)
	{
		IReadOnlyExerciseField field = m_Fields.get(position);

		String strResImage = field.getIcon();

		// Get the inner image view object from the XML.
		final ImageView imgUnique =
				(ImageView) retVal.findViewById(R.id.image);
		if (strResImage != "")
		{
			// Get the correct bitmap and scale it to fit.
			int resId =
					retVal.getResources()
							.getIdentifier(
									strResImage,
									"drawable",
									retVal.getContext()
											.getPackageName());
			Bitmap bmpField =
					DisplayHelper.getScaledBitmap(retVal
							.getResources(), resId, true);
			if (!field.isAvailable())
			{
				bmpField = DisplayHelper.toGrayscale(bmpField);
			}
			imgUnique.setImageBitmap(bmpField);
		}
	}

	@Override
	public int getCount()
	{
		return m_Fields.size();
	}

	@Override
	protected void handleDisabledClicked(	int nIndex,
											Context context)
	{

		ExerciseField fieldCurrent =
				(ExerciseField) m_Fields.get(nIndex);

		IReadOnlyExerciseStage stage =
				fieldCurrent.getStages().get(0);

		showDisabledReason(context, stage);
	}
}
