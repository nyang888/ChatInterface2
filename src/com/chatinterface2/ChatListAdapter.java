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
			mButton = (Button) mChatCell.findViewById(R.id.toggle_chat_button);
			mChatView = mInflater.inflate(R.layout.text_chat_block_left,
					parent, false);

		} else {
			mChatCell = mInflater.inflate(R.layout.chat_cell_right, parent,
					false);
			mButton = (Button) mChatCell.findViewById(R.id.toggle_chat_button);
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
		FrameLayout mBlank = (FrameLayout) mChatCell
				.findViewById(R.id.empty_blank);

		mChatContainer.addView(mChatView);
		// Here we re-adjust the size of the button to fit.
		mButton.setHeight(mChatContainer.getHeight());

		// Here we set up an OnTouchListener to deal with swipes and clicks.
		if (mChatBlocks.get(position).getUserId() == mCurrentUserId) {
			// In the case that the chatBlock in on the right:
			mChatContainer.setOnTouchListener(new RightTouchHandler(
					mChatContainer, mButton, mBlank));
			mButton.setOnTouchListener(new RightTouchHandler(mChatContainer,
					mButton, mBlank));
		} else {
			// The case that the chatBlock is on Left:
			mChatContainer.setOnTouchListener(new LeftTouchHandler(
					mChatContainer, mButton, mBlank));
			mButton.setOnTouchListener(new LeftTouchHandler(mChatContainer,
					mButton, mBlank));
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
		private RelativeLayout.LayoutParams mChatParams;
		private RelativeLayout.LayoutParams mBlankParams;
		private View mBlank;

		public RightTouchHandler(View _chatContainer, Button _button,
				View _blank) {
			mChatContainer = _chatContainer;
			mButton = _button;
			mBlank = _blank;
			mBlankParams = (RelativeLayout.LayoutParams) mBlank
					.getLayoutParams();
			mChatParams = (RelativeLayout.LayoutParams) mChatContainer
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
				mChatParams.leftMargin = 0;
				mChatParams.rightMargin = 0 - (mChatContainer.getWidth() - (displayMetrics.widthPixels - x2));
				mBlankParams.width = x2;
				mBlankParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				mBlank.setLayoutParams(mBlankParams);
				mChatContainer.setLayoutParams(mChatParams);
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				x2 = (int) event.getRawX();
				if (x2 < x1) {
					mChatParams.leftMargin = 0;
					mChatParams.rightMargin = 0;
					mBlankParams.width = displayMetrics.widthPixels
							- mChatContainer.getWidth() - mButton.getWidth();
					mBlankParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				} else {
					mChatParams.leftMargin = 0;
					mChatParams.rightMargin = (0 - mChatContainer.getWidth());
					mBlankParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					mBlankParams.addRule(RelativeLayout.RIGHT_OF,
							R.id.toggle_chat_button);
				}
				mBlank.setLayoutParams(mBlankParams);
				mChatContainer.setLayoutParams(mChatParams);
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				Log.d("onTouch", "ACTION_CANCEL");
				x2 = (int) event.getRawX();
				if (x2 < x1) {
					mChatParams.leftMargin = 0;
					mChatParams.rightMargin = 0;
					mBlankParams.width = displayMetrics.widthPixels
							- mChatContainer.getWidth() - mButton.getWidth();
					mBlankParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				} else {
					mChatParams.leftMargin = 0;
					mChatParams.rightMargin = (0 - mChatContainer.getWidth());
					mBlankParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					mBlankParams.addRule(RelativeLayout.RIGHT_OF,
							R.id.toggle_chat_button);
				}
				mBlank.setLayoutParams(mBlankParams);
				mChatContainer.setLayoutParams(mChatParams);
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
		private RelativeLayout.LayoutParams mChatParams;
		private RelativeLayout.LayoutParams mBlankParams;
		private View mBlank;

		public LeftTouchHandler(View _chatContainer, Button _button, View _blank) {
			mChatContainer = _chatContainer;
			mButton = _button;
			mBlank = _blank;
			mBlankParams = (RelativeLayout.LayoutParams) mBlank
					.getLayoutParams();
			mChatParams = (RelativeLayout.LayoutParams) mChatContainer
					.getLayoutParams();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				Log.d("onTouch", "ACTION_DOWN");
				x1 = (int) event.getRawX();
				mChatContainer.setLayoutParams(mChatParams);
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				Log.d("onTouch", "ACTION_MOVE");
				x2 = (int) event.getRawX();
				mChatParams.leftMargin = (x2 - mChatContainer.getWidth());
				mChatParams.rightMargin = 0;
				mBlankParams.width = (displayMetrics.widthPixels - x2);
				mBlankParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mBlank.setLayoutParams(mBlankParams);
				mChatContainer.setLayoutParams(mChatParams);
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				x2 = (int) event.getRawX();
				if (x2 > x1) {
					mChatParams.leftMargin = 0;
					mChatParams.rightMargin = 0;
					mBlankParams.width = displayMetrics.widthPixels
							- mChatContainer.getWidth() - mButton.getWidth();
					mBlankParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				} else {
					mChatParams.leftMargin = (0 - mChatContainer.getWidth());
					mChatParams.rightMargin = 0;
					mBlankParams.width = displayMetrics.widthPixels
							- mButton.getWidth();
					mBlankParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				}
				mBlank.setLayoutParams(mBlankParams);
				mChatContainer.setLayoutParams(mChatParams);
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				Log.d("onTouch", "ACTION_CANCEL");
				x2 = (int) event.getRawX();
				if (x2 > x1) {
					mChatParams.leftMargin = 0;
					mChatParams.rightMargin = 0;
					mBlankParams.width = displayMetrics.widthPixels
							- mChatContainer.getWidth() - mButton.getWidth();
					mBlankParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				} else {
					mChatParams.leftMargin = (0 - mChatContainer.getWidth());
					mChatParams.rightMargin = 0;
					mBlankParams.width = displayMetrics.widthPixels
							- mButton.getWidth();
					mBlankParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				}
				mBlank.setLayoutParams(mBlankParams);
				mChatContainer.setLayoutParams(mChatParams);
				break;
			}
			}
			return true;
		}
	}
}
