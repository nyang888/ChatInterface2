package com.chatinterface2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CustomChatContainer extends FrameLayout {
	private static boolean isOpen = true;

	public CustomChatContainer(Context context) {
		super(context);
	}

	public CustomChatContainer(Context _context, AttributeSet attr) {
		super(_context, attr);
	}

	public CustomChatContainer(Context _context, AttributeSet _attr,
			int _defStyle) {
		super(_context, _attr, _defStyle);
	}

	public void setOpen(boolean open) {
		isOpen = open;
	}

	public boolean isOpen() {
		return isOpen;
	}
}
