package com.chatinterface2;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
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

			if (mChatList.size() > 0) {
				if ((mChatList.get(mChatList.size() - 1) instanceof TextBlock)) {
					if (mChatList.get(mChatList.size() - 1).getUserId() == tempTextBlock
							.getUserId()) {
						StringBuilder sb = new StringBuilder();
						sb.append(((TextBlock) mChatList.get(mChatList.size() - 1))
								.getText());
						sb.append("\n");
						sb.append("\n");
						sb.append(tempTextBlock.getText());
						((TextBlock) mChatList.get(mChatList.size() - 1))
								.setText(sb.toString());
					} else {
						mChatList.add(tempTextBlock);
					}
				} else {
					mChatList.add(tempTextBlock);
				}
			} else {
				mChatList.add(tempTextBlock);
			}

		}
	}

	// Return the ArrayList of ChatBlocks for use in the Adapter
	public ArrayList<ChatBlock> getArrayList() {
		return mChatList;
	}
}
