package com.mad.guitarteacher.practice;

import java.util.ArrayList;

public interface IModifiableExerciseField
{
	public ArrayList<ExerciseStage> getStages();

	public void setCurrentStage(int nCurrentStage);
}
