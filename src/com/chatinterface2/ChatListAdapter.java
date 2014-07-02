package com.chatinterface2;

import java.util.ArrayList;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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
		if (mChatBlocks.get(position).getUserId() != mCurrentUserId) {
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
		FrameLayout mChatContainer = (FrameLayout) mChatCell
				.findViewById(R.id.chat_block_container);

		mChatContainer.addView(mChatView);

		// Here we set up an OnTouchListener to deal with swipes and clicks.
		if (mChatBlocks.get(position).getUserId() == mCurrentUserId) {
			// In the case that the chatBlock in on the right:
			mChatContainer.setOnTouchListener(new RightTouchHandler(
					mChatContainer, mButton));
			mButton.setOnTouchListener(new RightTouchHandler(mChatContainer,
					mButton));
		} else {
			// The case that the chatBlock is on Left:
			mChatContainer.setOnTouchListener(new LeftTouchHandler(
					mChatContainer, mButton));
			mButton.setOnTouchListener(new LeftTouchHandler(mChatContainer,
					mButton));
		}
		return mChatCell;
	}

	// Here we implement the touch listeners as separate classes since we use
	// them so much.
	private class RightTouchHandler implements OnTouchListener {
		private int x1;
		private int x2;
		private Button mButton;
		private View mChatContainer;
		private RelativeLayout.LayoutParams params;

		public RightTouchHandler(View _chatContainer, Button _button) {
			mChatContainer = _chatContainer;
			mButton = _button;
			params = (RelativeLayout.LayoutParams) mChatContainer
					.getLayoutParams();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				Log.d("onTouch", "ACTION_DOWN");
				x1 = (int) event.getRawX();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				Log.d("onTouch", "ACTION_MOVE");
				x2 = (int) event.getRawX();
				params.leftMargin = x2;
				params.rightMargin = 0 - (mChatContainer.getWidth() - (displayMetrics.widthPixels
						- x2 - mButton.getWidth()));
				mChatContainer.setLayoutParams(params);
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				x2 = (int) event.getRawX();
				if (x2 < x1) {
					params.leftMargin = (displayMetrics.widthPixels
							- mChatContainer.getWidth() - mButton.getWidth());
					params.rightMargin = 0;
				} else {
					params.leftMargin = displayMetrics.widthPixels;
					params.rightMargin = (0 - mChatContainer.getWidth());
				}
				mChatContainer.setLayoutParams(params);
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				Log.d("onTouch", "ACTION_CANCEL");
				x2 = (int) event.getRawX();
				if (x2 < x1) {
					params.leftMargin = (displayMetrics.widthPixels
							- mChatContainer.getWidth() - mButton.getWidth());
					params.rightMargin = 0;
				} else {
					params.leftMargin = displayMetrics.widthPixels;
					params.rightMargin = (0 - mChatContainer.getWidth());
				}
				mChatContainer.setLayoutParams(params);
				break;
			}
			}
			return true;
		}
	}

	private class LeftTouchHandler implements OnTouchListener {
		private int x1;
		private int x2;
		private Button mButton;
		private View mChatContainer;
		private RelativeLayout.LayoutParams params;

		public LeftTouchHandler(View _chatContainer, Button _button) {
			mChatContainer = _chatContainer;
			mButton = _button;
			params = (RelativeLayout.LayoutParams) mChatContainer
					.getLayoutParams();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				Log.d("onTouch", "ACTION_DOWN");
				x1 = (int) event.getRawX();
				mChatContainer.setLayoutParams(params);
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				Log.d("onTouch", "ACTION_MOVE");
				x2 = (int) event.getRawX();
				params.leftMargin = (x2 - mChatContainer.getWidth());
				params.rightMargin = (displayMetrics.widthPixels - x2 - mButton
						.getWidth());
				mChatContainer.setLayoutParams(params);
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				x2 = (int) event.getRawX();
				if (x2 > x1) {
					params.leftMargin = 0;
					params.rightMargin = (displayMetrics.widthPixels - mChatContainer
							.getWidth());
				} else {
					params.leftMargin = (0 - mChatContainer.getWidth());
					params.rightMargin = (displayMetrics.widthPixels);
				}
				mChatContainer.setLayoutParams(params);
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				Log.d("onTouch", "ACTION_CANCEL");
				x2 = (int) event.getRawX();
				if (x2 > x1) {
					params.leftMargin = 0;
					params.rightMargin = (displayMetrics.widthPixels - mChatContainer
							.getWidth());
				} else {
					params.leftMargin = (0 - mChatContainer.getWidth());
					params.rightMargin = (displayMetrics.widthPixels);
				}
				mChatContainer.setLayoutParams(params);
				break;
			}
			}
			return true;
		}
	}
}
