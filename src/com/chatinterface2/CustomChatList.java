package com.chatinterface2;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomChatList extends ListView {
	private ArrayList<ChatBlock> mChatList = new ArrayList<ChatBlock>();

	public CustomChatList(Context _context) {
		super(_context);
	}

	public CustomChatList(Context _context, AttributeSet attr) {
		super(_context, attr);
	}

	public CustomChatList(Context _context, AttributeSet _attr, int _defStyle) {
		super(_context, _attr, _defStyle);
	}

	// This function adds a JSONObject to the list of chats.
	public void addJson(JSONObject json) {

		if (json != null) {
			TextBlock tempTextBlock = new TextBlock();

			tempTextBlock.parseJson(json);

			mChatList.add(tempTextBlock);

		}
	}

	public ArrayList<ChatBlock> getArray() {
		return mChatList;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		super.dispatchTouchEvent(event);
		
		return true;
	}

}
