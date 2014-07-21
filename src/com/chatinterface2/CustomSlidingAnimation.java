package com.chatinterface2;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CustomSlidingAnimation extends Animation {
	private final int targetX;
	private final int currentX;
	private final View view;

	public CustomSlidingAnimation(int _currentX, int _targetX, View _view) {
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
