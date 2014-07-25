package com.nelson.chatinterface.honeycombcontrollers;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

@SuppressLint("NewApi")
public class CustomSlidingAnimationHoneycomb extends Animation {
	private final int targetX;
	private final int currentX;
	private final View view;

	public CustomSlidingAnimationHoneycomb(int _currentX, int _targetX, View _view) {
		targetX = _targetX;
		view = _view;
		currentX = _currentX;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		int newX;
		newX = currentX + (int) ((targetX - currentX) * interpolatedTime);
		view.setX(newX);
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
	}

	@Override
	public boolean willChangeBounds() {
		return true;
	}
}