package com.nelson.chatinterface.chatviews;

import java.util.ArrayList;

import org.json.JSONObject;

import com.nelson.chatinterface.chatmessagemodels.ChatBlock;
import com.nelson.chatinterface.chatmessagemodels.EmptyBlock;
import com.nelson.chatinterface.chatmessagemodels.TextBlock;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CustomChatList extends ListView {
	private ArrayList<ChatBlock> mListOfChatBlocks = new ArrayList<ChatBlock>();
	public static boolean LIST_INTERCEPT_TOUCH; // This variable will be
												// accessed by all the
												// touchListeners so that the
												// list does not intercept when
												// the map is being used.

	public CustomChatList(Context _context) {
		super(_context);
		// We add a empty block into the list for some extra spacing.
		LIST_INTERCEPT_TOUCH = true;
		mListOfChatBlocks.add(new EmptyBlock());
	}

	public CustomChatList(Context _context, AttributeSet attr) {
		super(_context, attr);
		LIST_INTERCEPT_TOUCH = true;
		mListOfChatBlocks.add(new EmptyBlock());
	}

	public CustomChatList(Context _context, AttributeSet _attr, int _defStyle) {
		super(_context, _attr, _defStyle);
		LIST_INTERCEPT_TOUCH = true;
		mListOfChatBlocks.add(new EmptyBlock());
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
						// Here we add 2 empty lines as extra spacing.
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
			// Add an empty block for some extra spacing.
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
		// Here we go through the entire list of items and set them as false.
		for (int i = 0; i < mListOfChatBlocks.size(); i++) {
			Log.d("collapseAll", "mChatContainer Succeed");
			mListOfChatBlocks.get(i).setOpen(false);
			// After updating values, we update the Adapter
			((ArrayAdapter<ChatBlock>) super.getAdapter())
					.notifyDataSetChanged();
		}
	}

	public void expandAll() {
		// Here we go through and set them as open
		for (int i = 0; i < mListOfChatBlocks.size(); i++) {
			Log.d("collapseAll", "mChatContainer Succeed");
			mListOfChatBlocks.get(i).setOpen(true);
			// Then we update the adapter
			((ArrayAdapter<ChatBlock>) super.getAdapter())
					.notifyDataSetChanged();
		}
	}

}
