package com.nelson.chatinterface.chatviews;

import com.nelson.chatinterface.MainActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CustomChatContainer extends FrameLayout {
	// This variable tells you the size of the chatBlock
	public static int CHAT_WIDTH = 300 * (MainActivity.displayMetrics.widthPixels / 160);

	public CustomChatContainer(Context context) {
		super(context);
		System.out.println(CustomChatContainer.CHAT_WIDTH);
	}

	public CustomChatContainer(Context _context, AttributeSet attr) {
		super(_context, attr);
		System.out.println(CustomChatContainer.CHAT_WIDTH);
	}

	public CustomChatContainer(Context _context, AttributeSet _attr,
			int _defStyle) {
		super(_context, _attr, _defStyle);
		System.out.println(CustomChatContainer.CHAT_WIDTH);
	}

}
