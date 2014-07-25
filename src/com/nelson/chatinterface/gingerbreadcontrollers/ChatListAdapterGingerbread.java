package com.nelson.chatinterface.gingerbreadcontrollers;

import java.util.ArrayList;

import com.chatinterface2.R;
import com.nelson.chatinterface.MainActivity;
import com.nelson.chatinterface.chatmessagemodels.ChatBlock;
import com.nelson.chatinterface.chatmessagemodels.EmptyBlock;
import com.nelson.chatinterface.chatmessagemodels.TextBlock;
import com.nelson.chatinterface.chatviews.CustomChatContainer;
import com.nelson.chatinterface.chatviews.CustomChatList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatListAdapterGingerbread extends ArrayAdapter<ChatBlock> {
	private Context mContext;
	private ArrayList<ChatBlock> mListOfChatBlocks;
	private int mCurrentUserId;
	public static DisplayMetrics displayMetrics;
	private static int animationDuration = 500; // Change this for sliding
												// animation time in
												// milliseconds.

	// Here is the constructor.
	public ChatListAdapterGingerbread(Context _context,
			ArrayList<ChatBlock> _chatBlocks) {
		super(_context, R.layout.text_chat_block_left, _chatBlocks);
		mContext = _context;
		mListOfChatBlocks = _chatBlocks;
		mCurrentUserId = MainActivity.CURRENT_USER_ID;
		displayMetrics = MainActivity.displayMetrics;
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
		View mChatListCell;
		if (mListOfChatBlocks.get(position) instanceof EmptyBlock) {
			mChatListCell = mInflater.inflate(R.layout.empty_cell, parent,
					false);
		} else {
			// In order to differentiate between the current user and the
			// others, we need to first check each chatBlock for the userId. And
			// then we inflate the corresponding layout, either left or right.
			View mChatBlockSection;
			if (mListOfChatBlocks.get(position).getUserId() != mCurrentUserId) {
				mChatListCell = mInflater.inflate(R.layout.chat_cell_left,
						parent, false);
				mChatBlockSection = mInflater.inflate(
						R.layout.text_chat_block_left, parent, false);

			} else {
				mChatListCell = mInflater.inflate(R.layout.chat_cell_right,
						parent, false);
				mChatBlockSection = mInflater.inflate(
						R.layout.text_chat_block_right, parent, false);
			}

			// Here we retrieve the remaining views from the layouts.
			TextView mNameView = (TextView) mChatBlockSection
					.findViewById(R.id.profile_name);
			TextView mDateView = (TextView) mChatBlockSection
					.findViewById(R.id.time_stamp);
			TextView mMessageView = (TextView) mChatBlockSection
					.findViewById(R.id.chat_message);
			ImageView mProfilePictureView = (ImageView) mChatListCell
					.findViewById(R.id.profile_picture_toggle);

			if (mListOfChatBlocks.get(position).getUserId() == 0) {
				mProfilePictureView.setImageResource(R.drawable.system_standin);
			} else if (mListOfChatBlocks.get(position).getUserId() != mCurrentUserId) {
				mProfilePictureView.setImageResource(R.drawable.boy_standin);
			} else {
				mProfilePictureView.setImageResource(R.drawable.girl_standin);
			}

			// Here we set the values for each of the views.
			mNameView.setText(mListOfChatBlocks.get(position).getUsername());
			mDateView.setText(mListOfChatBlocks.get(position).getDateString());
			mMessageView.setText(((TextBlock) mListOfChatBlocks.get(position))
					.getText());
			Button mBlankArea = (Button) mChatListCell
					.findViewById(R.id.empty_blank);
			CustomChatContainer mChatBlockContainer = (CustomChatContainer) mChatListCell
					.findViewById(R.id.chat_block_container);

			mChatBlockContainer.addView(mChatBlockSection);

			RelativeLayout.LayoutParams mBlankAreaParams = (RelativeLayout.LayoutParams) mBlankArea
					.getLayoutParams();
			RelativeLayout.LayoutParams mChatBlockParams = (RelativeLayout.LayoutParams) mChatBlockContainer
					.getLayoutParams();

			// Here we set up an OnTouchListener to deal with swipes and clicks.
			// We also set the behavior when the chat is open/closed. Default
			// setting is open.
			if (mListOfChatBlocks.get(position).getUserId() == mCurrentUserId) {
				// In the case that the chatBlock in on the right:
				mChatBlockContainer.setOnTouchListener(new RightTouchHandler(
						mChatBlockContainer, position));
				mProfilePictureView
						.setOnTouchListener(new RightImageTouchHandler(
								mChatBlockContainer, position));
				if (mListOfChatBlocks.get(position).getOpen() == false) {
					// If the right chatBlock is closed.
					mChatBlockParams.leftMargin = displayMetrics.widthPixels / 2;
					mChatBlockParams.rightMargin = 0 - CustomChatContainer.CHAT_WIDTH;
					mBlankAreaParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					mBlankAreaParams.addRule(RelativeLayout.LEFT_OF,
							R.id.profile_picture_toggle);
					mChatBlockContainer.setLayoutParams(mChatBlockParams);
					mBlankArea.setLayoutParams(mBlankAreaParams);
				}
			} else {
				// The case that the chatBlock is on Left:
				mChatBlockContainer.setOnTouchListener(new LeftTouchHandler(
						mChatBlockContainer, position));
				mProfilePictureView
						.setOnTouchListener(new LeftImageTouchHandler(
								mChatBlockContainer, position));

				if (mListOfChatBlocks.get(position).getOpen() == false) {
					// If the left chatBlock is closed.
					mChatBlockParams.rightMargin = displayMetrics.widthPixels / 2;
					mChatBlockParams.leftMargin = 0 - CustomChatContainer.CHAT_WIDTH;
					mBlankAreaParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					mBlankAreaParams.addRule(RelativeLayout.RIGHT_OF,
							R.id.profile_picture_toggle);
					mChatBlockContainer.setLayoutParams(mChatBlockParams);
					mBlankArea.setLayoutParams(mBlankAreaParams);
				}
			}
			// Here we set up the LongClickListener that will call the person
			// when hit.
			mProfilePictureView.setOnLongClickListener(new LongClickHandler(
					mListOfChatBlocks.get(position).getTele()));
		}
		return mChatListCell;
	}

	// Here we implement the touch listeners as separate classes.
	private class RightTouchHandler implements OnTouchListener {
		private final int MIN_SWIPE_DISTANCE = 10 * (displayMetrics.widthPixels / 160);
		private final int MIN_MOVE_DISTANCE = 5 * (displayMetrics.widthPixels / 160);
		private int prevX;
		private int newX;
		private CustomChatContainer mChatViewContainer;
		private int mPositioninArray;
		private CustomSlidingAnimationGingerbread animation;

		public RightTouchHandler(CustomChatContainer _chatContainer,
				int position) {
			mChatViewContainer = _chatContainer;
			mPositioninArray = position;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			CustomChatList.LIST_INTERCEPT_TOUCH = true;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				// Get the X position at the start of the touch.
				Log.d("onTouch", "ACTION_DOWN");
				prevX = (int) event.getRawX();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// The ACTION_MOVE is mainly just for some small animation to
				// follow the finger.
				Log.d("onTouch", "ACTION_MOVE");
				newX = (int) event.getRawX();
				if (Math.abs(newX - prevX) > MIN_MOVE_DISTANCE) {
					// We need to make sure that the user is intending to swipe
					// if they are moving the chat. We implement a small check
					// that most swipes should abide by.
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mChatViewContainer
							.getLayoutParams();
					params.leftMargin = displayMetrics.widthPixels
							- CustomChatContainer.CHAT_WIDTH + (newX - prevX);
					params.rightMargin = 0 - (newX - prevX);

					// If you move left, you should not be able to move past
					// origin.
					if (params.leftMargin < displayMetrics.widthPixels
							- CustomChatContainer.CHAT_WIDTH) {
						params.leftMargin = displayMetrics.widthPixels
								- CustomChatContainer.CHAT_WIDTH;
						params.rightMargin = 0;
					}

					mChatViewContainer.setLayoutParams(params);
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				newX = (int) event.getRawX();
				if ((newX - prevX) >= MIN_SWIPE_DISTANCE) {
					// If you swipe right, it closes. The animation should make
					// it look as if it is moving off screen.
					animation = new CustomSlidingAnimationGingerbread(
							CustomChatContainer.CHAT_WIDTH, mChatViewContainer);
					animation.setDuration(animationDuration);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationEnd(Animation arg0) {
							Log.d("SlidingAnimation", "End: "
									+ mChatViewContainer.getLeft());
							mListOfChatBlocks.get(mPositioninArray).setOpen(
									false);
							notifyDataSetChanged();
						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
						}

						@Override
						public void onAnimationStart(Animation arg0) {
							Log.d("SlidingAnimation", "Start: "
									+ mChatViewContainer.getLeft());
						}
					});
					mChatViewContainer.startAnimation(animation);

				} else {
					// Otherwise it stays open.
					mListOfChatBlocks.get(mPositioninArray).setOpen(true);
					notifyDataSetChanged();
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				// ACTION_CANCEL is for the times that your finger swipes out of
				// the bounds of the ChatView.
				Log.d("onTouch", "ACTION_CANCEL");
				newX = (int) event.getRawX();
				if ((newX - prevX) >= MIN_SWIPE_DISTANCE) {
					// If you swipe right, it closes. The animation should make
					// it look as if it is moving off screen.
					animation = new CustomSlidingAnimationGingerbread(
							CustomChatContainer.CHAT_WIDTH, mChatViewContainer);
					animation.setDuration(animationDuration);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationEnd(Animation arg0) {
							mListOfChatBlocks.get(mPositioninArray).setOpen(
									false);
							notifyDataSetChanged();
						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
						}

						@Override
						public void onAnimationStart(Animation arg0) {
						}
					});
					mChatViewContainer.startAnimation(animation);

				} else {
					// Otherwise it stays open.
					mListOfChatBlocks.get(mPositioninArray).setOpen(true);
					notifyDataSetChanged();
				}
				break;
			}
			}
			return true;
		}
	}

	private class LeftTouchHandler implements OnTouchListener {
		private final int MIN_SWIPE_DISTANCE = 10 * (displayMetrics.widthPixels / 160);
		private final int MIN_MOVE_DISTANCE = 5 * (displayMetrics.widthPixels / 160);
		private int prevX;
		private int newX;
		private CustomChatContainer mChatViewContainer;
		private int mPositionInArray;
		private CustomSlidingAnimationGingerbread animation;

		public LeftTouchHandler(CustomChatContainer _chatContainer, int position) {
			mChatViewContainer = _chatContainer;
			mPositionInArray = position;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			CustomChatList.LIST_INTERCEPT_TOUCH = true;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				Log.d("onTouch", "ACTION_DOWN");
				// Get the X position of the start of the touch.
				prevX = (int) event.getRawX();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// This will make the chat block follow the finger motions to
				// make the experience smoother.
				Log.d("onTouch", "ACTION_MOVE");
				newX = (int) event.getRawX();
				if (Math.abs(newX - prevX) > MIN_MOVE_DISTANCE) {
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mChatViewContainer
							.getLayoutParams();
					params.rightMargin = displayMetrics.widthPixels
							- CustomChatContainer.CHAT_WIDTH + (prevX - newX);
					params.leftMargin = 0 - (prevX - newX);

					// If you swipe to the right, it should not go past limit.
					if (params.rightMargin < displayMetrics.widthPixels
							- CustomChatContainer.CHAT_WIDTH) {
						params.rightMargin = displayMetrics.widthPixels
								- CustomChatContainer.CHAT_WIDTH;
						params.leftMargin = 0;
					}

					mChatViewContainer.setLayoutParams(params);
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				newX = (int) event.getRawX();
				if ((prevX - newX) >= MIN_SWIPE_DISTANCE) {
					// Here we swipe left to close with a sliding animation.
					animation = new CustomSlidingAnimationGingerbread(
							0 - CustomChatContainer.CHAT_WIDTH,
							mChatViewContainer);
					animation.setDuration(animationDuration);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationEnd(Animation arg0) {
							Log.d("SlidingAnimation", "End: "
									+ mChatViewContainer.getLeft());
							mListOfChatBlocks.get(mPositionInArray).setOpen(
									false);
							notifyDataSetChanged();
						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
						}

						@Override
						public void onAnimationStart(Animation arg0) {
							Log.d("SlidingAnimation", "Start: "
									+ mChatViewContainer.getLeft());
						}
					});
					mChatViewContainer.startAnimation(animation);
				} else {
					mListOfChatBlocks.get(mPositionInArray).setOpen(true);
					notifyDataSetChanged();
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				Log.d("onTouch", "ACTION_CANCEL");
				newX = (int) event.getRawX();
				if ((prevX - newX) >= MIN_SWIPE_DISTANCE) {
					// Here we swipe left to close with a sliding animation
					mListOfChatBlocks.get(mPositionInArray).setOpen(false);
					animation = new CustomSlidingAnimationGingerbread(
							0 - CustomChatContainer.CHAT_WIDTH,
							mChatViewContainer);
					animation.setDuration(animationDuration);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationEnd(Animation arg0) {
							Log.d("SlidingAnimation", "End: "
									+ mChatViewContainer.getLeft());
							mListOfChatBlocks.get(mPositionInArray).setOpen(
									false);
							notifyDataSetChanged();
						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
						}

						@Override
						public void onAnimationStart(Animation arg0) {
							Log.d("SlidingAnimation", "Start: "
									+ mChatViewContainer.getLeft());
						}
					});
					mChatViewContainer.startAnimation(animation);
				} else {
					mListOfChatBlocks.get(mPositionInArray).setOpen(true);
					notifyDataSetChanged();
				}
				break;
			}
			}
			return true;
		}
	}

	// These classes are for the profile pictures. The TouchHandlers will handle
	// clicking as well as scrolling. The LongClickListener will handle a call
	// when held down.
	private class RightImageTouchHandler implements OnTouchListener {
		private int prevX;
		private int newX;
		private long pressStartTime;
		private int prevY;
		private int newY;
		private CustomChatContainer mChatViewContainer;
		private static final int MAX_CLICK_DURATION = 500;
		private static final int MAX_CLICK_DISTANCE = 15;
		private int mPositionInArray;
		private CustomSlidingAnimationGingerbread animation;

		public RightImageTouchHandler(CustomChatContainer _chatContainer,
				int position) {
			mChatViewContainer = _chatContainer;
			mPositionInArray = position;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			CustomChatList.LIST_INTERCEPT_TOUCH = true;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				Log.d("onTouch", "ACTION_DOWN");
				// Get the startTime and the position of the start of the touch
				pressStartTime = System.currentTimeMillis();
				prevX = (int) event.getRawX();
				prevY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				// Get the time and position of the end of the touch
				long pressDuration = System.currentTimeMillis()
						- pressStartTime;
				newX = (int) event.getRawX();
				newY = (int) event.getRawY();
				if (pressDuration < MAX_CLICK_DURATION
						&& Math.abs(prevX - newX) < MAX_CLICK_DISTANCE
						&& Math.abs(prevY - newY) < MAX_CLICK_DISTANCE) {
					// If the start and end are close enough and the person
					// didn't move his finger around, then it is a click.
					if (mChatViewContainer.getLeft() >= displayMetrics.widthPixels) {
						// If the chat is closed, open
						animation = new CustomSlidingAnimationGingerbread(
								0 - CustomChatContainer.CHAT_WIDTH,
								mChatViewContainer);
						animation.setDuration(animationDuration);
						animation.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationEnd(Animation arg0) {
								Log.d("SlidingAnimation", "End: "
										+ mChatViewContainer.getLeft());
								mListOfChatBlocks.get(mPositionInArray)
										.setOpen(true);
								notifyDataSetChanged();
							}

							@Override
							public void onAnimationRepeat(Animation arg0) {
							}

							@Override
							public void onAnimationStart(Animation arg0) {
								Log.d("SlidingAnimation", "Start: "
										+ mChatViewContainer.getLeft());
							}
						});
						mChatViewContainer.startAnimation(animation);
					} else {
						// If the chat is open, close
						animation = new CustomSlidingAnimationGingerbread(
								CustomChatContainer.CHAT_WIDTH,
								mChatViewContainer);
						animation.setDuration(animationDuration);
						animation.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationEnd(Animation arg0) {
								Log.d("SlidingAnimation", "End: "
										+ mChatViewContainer.getLeft());
								mListOfChatBlocks.get(mPositionInArray)
										.setOpen(false);
								notifyDataSetChanged();
							}

							@Override
							public void onAnimationRepeat(Animation arg0) {
							}

							@Override
							public void onAnimationStart(Animation arg0) {
								Log.d("SlidingAnimation", "Start: "
										+ mChatViewContainer.getLeft());
							}
						});
						mChatViewContainer.startAnimation(animation);
					}
				}
				break;
			}
			}
			// We return false so that the LongClickListener can also receive
			// the event.
			return false;
		}
	}

	private class LeftImageTouchHandler implements OnTouchListener {
		private int prevX;
		private int newX;
		private long pressStartTime;
		private int prevY;
		private int newY;
		private CustomChatContainer mChatViewContainer;
		private static final int MAX_CLICK_DURATION = 500;
		private static final int MAX_CLICK_DISTANCE = 15;
		private int mPositionInArray;
		private CustomSlidingAnimationGingerbread animation;

		public LeftImageTouchHandler(CustomChatContainer _chatContainer,
				int position) {
			mChatViewContainer = _chatContainer;
			mPositionInArray = position;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			CustomChatList.LIST_INTERCEPT_TOUCH = true;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				Log.d("onTouch", "ACTION_DOWN");
				// Get the startTime and position of the start of the touch
				// event.
				pressStartTime = System.currentTimeMillis();
				prevX = (int) event.getRawX();
				prevY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				// Here we get the endTime and position of the event.
				long pressDuration = System.currentTimeMillis()
						- pressStartTime;
				newX = (int) event.getRawX();
				newY = (int) event.getRawY();
				if (pressDuration < MAX_CLICK_DURATION
						&& Math.abs(prevX - newX) < MAX_CLICK_DISTANCE
						&& Math.abs(prevY - newY) < MAX_CLICK_DISTANCE) {
					// If the times were close enough to represent a click, and
					// the finger didn't move, then it is a click.
					if (mChatViewContainer.getRight() <= 0) {
						// If it was closed, open
						animation = new CustomSlidingAnimationGingerbread(
								CustomChatContainer.CHAT_WIDTH,
								mChatViewContainer);
						animation.setDuration(animationDuration);
						animation.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationEnd(Animation arg0) {
								mListOfChatBlocks.get(mPositionInArray)
										.setOpen(true);
								notifyDataSetChanged();
							}

							@Override
							public void onAnimationRepeat(Animation arg0) {
							}

							@Override
							public void onAnimationStart(Animation arg0) {
							}
						});
						mChatViewContainer.startAnimation(animation);
					} else {
						// If it was open, close
						animation = new CustomSlidingAnimationGingerbread(
								0 - CustomChatContainer.CHAT_WIDTH,
								mChatViewContainer);
						animation.setDuration(animationDuration);
						animation.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationEnd(Animation arg0) {
								mListOfChatBlocks.get(mPositionInArray)
										.setOpen(false);
								notifyDataSetChanged();
							}

							@Override
							public void onAnimationRepeat(Animation arg0) {
							}

							@Override
							public void onAnimationStart(Animation arg0) {
							}
						});
						mChatViewContainer.startAnimation(animation);
					}
				}
				break;
			}
			}
			// We return false to make sure that the LongClickListener will also
			// hear the event.
			return false;
		}
	}

	// This LongClickListener will handle calling the person on a longPress
	private class LongClickHandler implements OnLongClickListener {
		private String mTele;

		public LongClickHandler(String tele) {
			mTele = tele;
		}

		@Override
		public boolean onLongClick(View arg0) {
			// We add a bit of HapticFeedback to let people know that something
			// happened when pressed and that they should let go.
			arg0.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
					HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
			// Get the dialer from the phone and set the number but it does not
			// Call.
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse(mTele));
			System.out.println("Calling: " + mTele);
			mContext.startActivity(intent);
			return false;
		}

	}
}
