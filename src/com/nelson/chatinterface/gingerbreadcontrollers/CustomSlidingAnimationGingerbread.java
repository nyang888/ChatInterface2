package com.nelson.chatinterface.gingerbreadcontrollers;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

public class CustomSlidingAnimationGingerbread extends Animation {
	private final int slideAmount;
	private final View view;
	private RelativeLayout.LayoutParams params;
	private int originLeft;
	private int originRight;

	public CustomSlidingAnimationGingerbread(int _slideAmount, View _view) {
		// We initialized based on how much we want to slide by
		slideAmount = _slideAmount;
		view = _view;
		params = (RelativeLayout.LayoutParams) view.getLayoutParams();
		originLeft = params.leftMargin;
		originRight = params.rightMargin;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// We calculate the amount to move based on the time.
		int newMargin;
		newMargin = (int) ((slideAmount) * interpolatedTime);
		// Here we set the marginParams based on the calculated time.
		params.leftMargin = originLeft + newMargin;
		params.rightMargin = originRight - newMargin;
		view.setLayoutParams(params);
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
