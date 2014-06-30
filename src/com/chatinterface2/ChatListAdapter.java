package com.chatinterface2;

import java.util.ArrayList;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatListAdapter extends ArrayAdapter<ChatBlock> {
	private Context mContext;
	private ArrayList<ChatBlock> mChatBlocks;
	private int mCurrentUserId;
	private DisplayMetrics displayMetrics;

	// Here is the constructor.
	public ChatListAdapter(Context _context, ArrayList<ChatBlock> _chatBlocks,
			int currentId) {
		super(_context, R.layout.text_chat_block_left, _chatBlocks);
		mContext = _context;
		mChatBlocks = _chatBlocks;
		mCurrentUserId = currentId;
		displayMetrics = mContext.getResources().getDisplayMetrics();
	}

	// getView returns the views that will then be put into the ListView.
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Here we need to inflate the layouts to be able to interact with them
		// better. In order to differentiate between the current user and the
		// others, we need to first check each chatBlock for the userId. And
		// then we inflate the corresponding layout, either left or right.
		View mChatView;
		View mChatCell;
		Button mButton;
		if (mChatBlocks.get(position).getUserId() == mCurrentUserId) {
			mChatCell = mInflater.inflate(R.layout.chat_cell_left, parent,
					false);
			mButton = (Button) mChatCell.findViewById(R.id.toggle_button);
			mChatView = mInflater.inflate(R.layout.text_chat_block_left,
					parent, false);

		} else {
			mChatCell = mInflater.inflate(R.layout.chat_cell_right, parent,
					false);
			mButton = (Button) mChatCell.findViewById(R.id.toggle_button);
			mChatView = mInflater.inflate(R.layout.text_chat_block_right,
					parent, false);
		}

		// Here we retrieve the remaining views from the layouts.
		TextView mNameView = (TextView) mChatView
				.findViewById(R.id.profile_name);
		TextView mDateView = (TextView) mChatView.findViewById(R.id.time_stamp);
		TextView mMessageView = (TextView) mChatView
				.findViewById(R.id.chat_message);

		// Here we set the values for each of the views.
		mNameView.setText(mChatBlocks.get(position).getUsername());
		mDateView.setText(mChatBlocks.get(position).getDate().toString());
		mMessageView.setText(((TextBlock) mChatBlocks.get(position)).getText());
		final FrameLayout mChatContainer = (FrameLayout) mChatCell
				.findViewById(R.id.chat_block_container);

		mChatContainer.addView(mChatView);

		// Here we provide instructions for the button.
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mChatContainer
						.getLayoutParams();
				if (params.width == 0) {
					// When the button is clicked, if the block is already
					// hidden, bring it out. Otherwise hide it.
					params.width = params.width
							+ (int) ((300 * displayMetrics.density) + 0.5);
					mChatContainer.setLayoutParams(params);
				} else {
					params.width = params.width
							- (int) ((300 * displayMetrics.density) + 0.5);
					mChatContainer.setLayoutParams(params);
				}
			}

		});

		return mChatCell;
	}
}
