package com.chatinterface2;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomChatList extends ListView {
	private ArrayList<ChatBlock> mChatList = new ArrayList<ChatBlock>();
	public static boolean LIST_INTERCEPT_TOUCH; // This variable will be
												// accessed by all the
												// touchListeners so that the
												// list does not intercept when
												// the map is being used.

	public CustomChatList(Context _context) {
		super(_context);
		LIST_INTERCEPT_TOUCH = true;
	}

	public CustomChatList(Context _context, AttributeSet attr) {
		super(_context, attr);
		LIST_INTERCEPT_TOUCH = true;
	}

	public CustomChatList(Context _context, AttributeSet _attr, int _defStyle) {
		super(_context, _attr, _defStyle);
		LIST_INTERCEPT_TOUCH = true;
	}

	// This function adds a JSONObject to the list of chats.
	public void addJson(JSONObject json) {

		if (json != null) {
			TextBlock tempTextBlock = new TextBlock();
			tempTextBlock.parseJson(json);

			if (mChatList.size() > 1) {
				// We must use size-2 since we need to account for the empty
				// spacing.
				if ((mChatList.get(mChatList.size() - 2) instanceof TextBlock)) {
					if (mChatList.get(mChatList.size() - 2).getUserId() == tempTextBlock
							.getUserId()) {
						StringBuilder sb = new StringBuilder();
						sb.append(((TextBlock) mChatList.get(mChatList.size() - 2))
								.getText());
						sb.append("\n");
						sb.append("\n");
						sb.append(tempTextBlock.getText());
						((TextBlock) mChatList.get(mChatList.size() - 2))
								.setText(sb.toString());
						// Remove the empty spacing after getting rid of the
						// cell.
						mChatList.remove(mChatList.size() - 1);
					} else {
						mChatList.add(tempTextBlock);
					}
				} else {
					mChatList.add(tempTextBlock);
				}
			} else {
				mChatList.add(tempTextBlock);
			}
			mChatList.add(new EmptyBlock());
		}
	}

	// Here we set if the intercept is needed.
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (LIST_INTERCEPT_TOUCH) {
			return super.onInterceptTouchEvent(ev);
		}
		return false;
	}

	// Return the ArrayList of ChatBlocks for use in the Adapter
	public ArrayList<ChatBlock> getArrayList() {
		return mChatList;
	}
}
