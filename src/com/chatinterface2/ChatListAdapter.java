package com.chatinterface2;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ChatListAdapter extends ArrayAdapter<ChatBlock> {
	private Context mContext;
	private ArrayList<ChatBlock> mChatBlocks;

	// Here is the constructor.
	public ChatListAdapter(Context _context, ArrayList<ChatBlock> _chatBlocks) {
		super(_context, R.layout.text_chat_block, _chatBlocks);
		mContext = _context;
		mChatBlocks = _chatBlocks;
	}

	// getView returns the views that will then be put into the ListView.
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View mChatView = mInflater.inflate(R.layout.text_chat_block, parent,
				false);
		View mChatCell = mInflater.inflate(R.layout.chat_cell, parent, false);

		TextView mNameView = (TextView) mChatView
				.findViewById(R.id.profile_name);
		TextView mDateView = (TextView) mChatView.findViewById(R.id.time_stamp);
		TextView mMessageView = (TextView) mChatView
				.findViewById(R.id.chat_message);
		final ViewGroup mChatContainer = (ViewGroup) mChatCell
				.findViewById(R.id.chat_block_container);
		Button mButton = (Button) mChatCell.findViewById(R.id.toggle_button);

		mNameView.setText(mChatBlocks.get(position).getUsername());
		mDateView.setText(mChatBlocks.get(position).getDate().toString());
		mMessageView.setText(((TextBlock) mChatBlocks.get(position)).getText());
		mChatContainer.addView(mChatView);

		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mChatContainer.getVisibility() == View.VISIBLE) {
					mChatContainer.setVisibility(View.INVISIBLE);
				} else {
					mChatContainer.setVisibility(View.VISIBLE);
				}
			}

		});

		return mChatCell;
	}
}
