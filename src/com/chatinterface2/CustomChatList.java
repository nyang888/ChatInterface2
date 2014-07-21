package com.chatinterface2;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CustomChatList extends ListView {
	private ArrayList<ChatBlock> mListOfChatBlocks = new ArrayList<ChatBlock>();
	private DisplayMetrics displayMetrics;
	public static boolean LIST_INTERCEPT_TOUCH; // This variable will be
												// accessed by all the
												// touchListeners so that the
												// list does not intercept when
												// the map is being used.

	public CustomChatList(Context _context) {
		super(_context);
		LIST_INTERCEPT_TOUCH = true;
		displayMetrics = _context.getResources().getDisplayMetrics();
	}

	public CustomChatList(Context _context, AttributeSet attr) {
		super(_context, attr);
		LIST_INTERCEPT_TOUCH = true;
		displayMetrics = _context.getResources().getDisplayMetrics();
	}

	public CustomChatList(Context _context, AttributeSet _attr, int _defStyle) {
		super(_context, _attr, _defStyle);
		LIST_INTERCEPT_TOUCH = true;
		displayMetrics = _context.getResources().getDisplayMetrics();
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		// Refresh list when scrolling up.
		((ArrayAdapter<ChatBlock>) super.getAdapter()).notifyDataSetChanged();

		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX,
				5 * (displayMetrics.heightPixels / 160), isTouchEvent);
	}

	// This function adds a JSONObject to the list of chats.
	public void addJson(JSONObject json) {

		if (json != null) {
			TextBlock tempTextBlock = new TextBlock();
			tempTextBlock.parseJson(json);

			if (mListOfChatBlocks.size() > 1) {
				// We must use size-2 since we need to account for the empty
				// spacing.
				if ((mListOfChatBlocks.get(mListOfChatBlocks.size() - 2) instanceof TextBlock)) {
					if (mListOfChatBlocks.get(mListOfChatBlocks.size() - 2)
							.getUserId() == tempTextBlock.getUserId()) {
						StringBuilder sb = new StringBuilder();
						sb.append(((TextBlock) mListOfChatBlocks
								.get(mListOfChatBlocks.size() - 2)).getText());
						sb.append("\n");
						sb.append("\n");
						sb.append(tempTextBlock.getText());
						((TextBlock) mListOfChatBlocks.get(mListOfChatBlocks
								.size() - 2)).setText(sb.toString());
						// Remove the empty spacing after getting rid of the
						// cell.
						mListOfChatBlocks.remove(mListOfChatBlocks.size() - 1);
					} else {
						mListOfChatBlocks.add(tempTextBlock);
					}
				} else {
					mListOfChatBlocks.add(tempTextBlock);
				}
			} else {
				mListOfChatBlocks.add(tempTextBlock);
			}
			mListOfChatBlocks.add(new EmptyBlock());
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
		return mListOfChatBlocks;
	}

	public void collapseAll() {
		for (int i = 0; i < mListOfChatBlocks.size(); i++) {
			Log.d("collapseAll", "mChatContainer Succeed");
			mListOfChatBlocks.get(i).setOpen(false);
			((ArrayAdapter<ChatBlock>) super.getAdapter())
					.notifyDataSetChanged();
		}
	}

	public void expandAll() {
		for (int i = 0; i < mListOfChatBlocks.size(); i++) {
			Log.d("collapseAll", "mChatContainer Succeed");
			mListOfChatBlocks.get(i).setOpen(true);
			((ArrayAdapter<ChatBlock>) super.getAdapter())
					.notifyDataSetChanged();
		}
	}

}
