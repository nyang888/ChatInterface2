package com.chatinterface2;

import java.util.ArrayList;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatListAdapter extends ArrayAdapter<ChatBlock> {
	private Context mContext;
	private ArrayList<ChatBlock> mChatBlocks;
	private int mCurrentUserId;
	private DisplayMetrics displayMetrics;

	// Here is the constructor.
	public ChatListAdapter(Context _context, ArrayList<ChatBlock> _chatBlocks) {
		super(_context, R.layout.text_chat_block_left, _chatBlocks);
		mContext = _context;
		mChatBlocks = _chatBlocks;
		mCurrentUserId = MainActivity.CURRENT_USER_ID;
		displayMetrics = mContext.getResources().getDisplayMetrics();
	}

	// getView returns the views that will then be put into the ListView.
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Here we need to inflate the layouts to be able to interact with them
		// better. We need to check what type of block the cell will be first.
		// That includes checking to see if it will be an empty cell used for
		// spacing.
		View mChatCell;
		if (mChatBlocks.get(position) instanceof EmptyBlock) {
			mChatCell = mInflater.inflate(R.layout.empty_cell, parent, false);
		} else {
			// In order to differentiate between the current user and the
			// others, we need to first check each chatBlock for the userId. And
			// then we inflate the corresponding layout, either left or right.
			View mChatView;
			if (mChatBlocks.get(position).getUserId() != mCurrentUserId) {
				mChatCell = mInflater.inflate(R.layout.chat_cell_left, parent,
						false);
				mChatView = mInflater.inflate(R.layout.text_chat_block_left,
						parent, false);

			} else {
				mChatCell = mInflater.inflate(R.layout.chat_cell_right, parent,
						false);
				mChatView = mInflater.inflate(R.layout.text_chat_block_right,
						parent, false);
			}

			// Here we retrieve the remaining views from the layouts.
			TextView mNameView = (TextView) mChatView
					.findViewById(R.id.profile_name);
			TextView mDateView = (TextView) mChatView
					.findViewById(R.id.time_stamp);
			TextView mMessageView = (TextView) mChatView
					.findViewById(R.id.chat_message);
			ImageView mImageView = (ImageView) mChatCell
					.findViewById(R.id.profile_picture_toggle);

			if (mChatBlocks.get(position).getUserId() == 0) {
				mImageView.setImageResource(R.drawable.system_standin);
			} else if (mChatBlocks.get(position).getUserId() != mCurrentUserId) {
				mImageView.setImageResource(R.drawable.boy_standin);
			} else {
				mImageView.setImageResource(R.drawable.girl_standin);
			}

			// Here we set the values for each of the views.
			mNameView.setText(mChatBlocks.get(position).getUsername());
			mDateView.setText(mChatBlocks.get(position).getDateString());
			mMessageView.setText(((TextBlock) mChatBlocks.get(position))
					.getText());
			Button mBlank = (Button) mChatCell.findViewById(R.id.empty_blank);
			CustomChatContainer mChatContainer = (CustomChatContainer) mChatCell
					.findViewById(R.id.chat_block_container);

			mChatContainer.addView(mChatView);

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBlank
					.getLayoutParams();

			// Here we set up an OnTouchListener to deal with swipes and clicks.
			if (mChatBlocks.get(position).getUserId() == mCurrentUserId) {
				// In the case that the chatBlock in on the right:
				mChatContainer.setOnTouchListener(new RightTouchHandler(
						mChatContainer, mBlank));
				mImageView.setOnClickListener(new RightImageClickHandler(
						mChatContainer, mBlank));
				if (mChatContainer.isOpen() == false) {
					mChatContainer.setX(displayMetrics.widthPixels);
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					params.addRule(RelativeLayout.LEFT_OF,
							R.id.profile_picture_toggle);
					mBlank.setLayoutParams(params);
				}
			} else {
				// The case that the chatBlock is on Left:
				mChatContainer.setOnTouchListener(new LeftTouchHandler(
						mChatContainer, mBlank));
				mImageView.setOnClickListener(new LeftImageClickHandler(
						mChatContainer, mBlank));

				if (mChatContainer.isOpen() == false) {
					mChatContainer.setX(displayMetrics.widthPixels);
					params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					params.addRule(RelativeLayout.RIGHT_OF,
							R.id.profile_picture_toggle);
					mBlank.setLayoutParams(params);
				}
			}

		}

		return mChatCell;
	}

	// Here we implement the touch listeners as separate classes since we use
	// them so much.
	private class RightTouchHandler implements OnTouchListener {
		private final int MIN_SWIPE_DISTANCE = 40 * (displayMetrics.widthPixels / 160);
		private int x1;
		private int x2;
		private CustomChatContainer mChatContainer;
		private View mBlank;
		private RelativeLayout.LayoutParams params;

		public RightTouchHandler(CustomChatContainer _chatContainer, View _blank) {
			mChatContainer = _chatContainer;
			mBlank = _blank;
			params = (RelativeLayout.LayoutParams) _blank.getLayoutParams();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				Log.d("onTouch", "ACTION_DOWN");
				CustomChatList.LIST_INTERCEPT_TOUCH = true;
				x1 = (int) event.getRawX();
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				CustomChatList.LIST_INTERCEPT_TOUCH = true;
				x2 = (int) event.getRawX();
				if ((x2 - x1) >= MIN_SWIPE_DISTANCE) {
					// If swiping from left to right: Close Chat
					mChatContainer.setX(displayMetrics.widthPixels);
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					params.addRule(RelativeLayout.LEFT_OF,
							R.id.profile_picture_toggle);
				} else if ((x1 - x2) >= MIN_SWIPE_DISTANCE) {
					// If swiping from right to left: Open Chat
					mChatContainer.setX(displayMetrics.widthPixels
							- mChatContainer.getWidth());
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					params.addRule(RelativeLayout.LEFT_OF,
							R.id.chat_block_container);
				}
				mBlank.setLayoutParams(params);
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				Log.d("onTouch", "ACTION_CANCEL");
				CustomChatList.LIST_INTERCEPT_TOUCH = true;
				x2 = (int) event.getRawX();
				if ((x2 - x1) >= MIN_SWIPE_DISTANCE) {
					// If swiping from left to right: Close Chat
					mChatContainer.setX(displayMetrics.widthPixels);
					params.width = displayMetrics.widthPixels;
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				} else if ((x1 - x2) >= MIN_SWIPE_DISTANCE) {
					// If swiping from right to left: Open Chat
					mChatContainer.setX(displayMetrics.widthPixels
							- mChatContainer.getWidth());
					params.width = displayMetrics.widthPixels
							- mChatContainer.getWidth();
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				}
				mBlank.setLayoutParams(params);
				break;
			}
			}
			return true;
		}
	}

	private class LeftTouchHandler implements OnTouchListener {
		private final int MIN_SWIPE_DISTANCE = 40 * (displayMetrics.widthPixels / 160);
		private int x1;
		private int x2;
		private CustomChatContainer mChatContainer;
		private View mBlank;
		private RelativeLayout.LayoutParams params;

		public LeftTouchHandler(CustomChatContainer _chatContainer, View _blank) {
			mChatContainer = _chatContainer;
			mBlank = _blank;
			params = (RelativeLayout.LayoutParams) _blank.getLayoutParams();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				Log.d("onTouch", "ACTION_DOWN");
				CustomChatList.LIST_INTERCEPT_TOUCH = true;
				x1 = (int) event.getRawX();
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				CustomChatList.LIST_INTERCEPT_TOUCH = true;
				x2 = (int) event.getRawX();
				if ((x2 - x1) >= MIN_SWIPE_DISTANCE) {
					// If swiping from left to right: Open Chat
					mChatContainer.setX(0);
					params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					params.addRule(RelativeLayout.RIGHT_OF,
							R.id.chat_block_container);
				} else if ((x1 - x2) >= MIN_SWIPE_DISTANCE) {
					// If swiping from right to left: Close Chat
					mChatContainer.setX(0 - mChatContainer.getWidth());
					params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					params.addRule(RelativeLayout.RIGHT_OF,
							R.id.profile_picture_toggle);
				}
				mBlank.setLayoutParams(params);
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				Log.d("onTouch", "ACTION_CANCEL");
				CustomChatList.LIST_INTERCEPT_TOUCH = true;
				x2 = (int) event.getRawX();
				if ((x2 - x1) >= MIN_SWIPE_DISTANCE) {
					// If swiping from left to right: Open Chat
					mChatContainer.setX(0);
					params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					params.addRule(RelativeLayout.RIGHT_OF,
							R.id.chat_block_container);
				} else if ((x1 - x2) >= MIN_SWIPE_DISTANCE) {
					// If swiping from right to left: Close Chat
					mChatContainer.setX(0 - mChatContainer.getWidth());
					params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					params.addRule(RelativeLayout.RIGHT_OF,
							R.id.profile_picture_toggle);
				}
				mBlank.setLayoutParams(params);
				break;
			}
			}
			return true;
		}
	}

	private class RightImageClickHandler implements OnClickListener {
		private CustomChatContainer mChatContainer;
		private View mBlank;
		private RelativeLayout.LayoutParams params;

		public RightImageClickHandler(CustomChatContainer _chatContainer,
				View _blank) {
			mChatContainer = _chatContainer;
			mBlank = _blank;
			params = (RelativeLayout.LayoutParams) _blank.getLayoutParams();
		}

		@Override
		public void onClick(View arg0) {
			CustomChatList.LIST_INTERCEPT_TOUCH = true;
			if (mChatContainer.getX() >= displayMetrics.widthPixels) {
				// If the chat is closed, open
				mChatContainer.setX(displayMetrics.widthPixels
						- mChatContainer.getWidth());
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.LEFT_OF,
						R.id.chat_block_container);
			} else {
				// If the chat is open, close
				mChatContainer.setX(displayMetrics.widthPixels);
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.LEFT_OF,
						R.id.profile_picture_toggle);
			}
			mBlank.setLayoutParams(params);
		}
	}

	private class LeftImageClickHandler implements OnClickListener {
		private CustomChatContainer mChatContainer;
		private View mBlank;
		private RelativeLayout.LayoutParams params;

		public LeftImageClickHandler(CustomChatContainer _chatContainer,
				View _blank) {
			mChatContainer = _chatContainer;
			mBlank = _blank;
			params = (RelativeLayout.LayoutParams) _blank.getLayoutParams();
		}

		@Override
		public void onClick(View arg0) {
			CustomChatList.LIST_INTERCEPT_TOUCH = true;
			if (mChatContainer.getX() < 0) {
				// If it was closed, open
				mChatContainer.setX(0);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.RIGHT_OF,
						R.id.chat_block_container);
			} else {
				// If it was open, close
				mChatContainer.setX(0 - mChatContainer.getWidth());
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.RIGHT_OF,
						R.id.profile_picture_toggle);
			}
			mBlank.setLayoutParams(params);
		}

	}

}
