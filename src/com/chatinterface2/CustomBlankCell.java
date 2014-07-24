package com.chatinterface2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

public class CustomBlankCell extends Button {

	public CustomBlankCell(Context context) {
		super(context);
	}

	public CustomBlankCell(Context _context, AttributeSet attr) {
		super(_context, attr);
	}

	public CustomBlankCell(Context _context, AttributeSet _attr, int _defStyle) {
		super(_context, _attr, _defStyle);
	}

	// Here we just handle the touch event. If the empty space is touched, send
	// the touch to the wrapper for the map.
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (ev.getAction() != 3) {
			CustomChatList.LIST_INTERCEPT_TOUCH = false;
			ev.setLocation(ev.getRawX(), ev.getRawY());
			MainActivity.MAP_WRAPPER.dispatchTouchEvent(ev);
			Log.d("dispatchTouchEvent", "Dispatched " + ev.getAction());
		}

		return true;
	}

}
