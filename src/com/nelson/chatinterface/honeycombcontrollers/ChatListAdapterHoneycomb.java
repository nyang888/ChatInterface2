package com.nelson.chatinterface.honeycombcontrollers;

import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chatinterface2.R;
import com.nelson.chatinterface.MainActivity;
import com.nelson.chatinterface.chatmessagemodels.ChatBlock;
import com.nelson.chatinterface.chatmessagemodels.EmptyBlock;
import com.nelson.chatinterface.chatmessagemodels.TextBlock;
import com.nelson.chatinterface.chatviews.CustomChatContainer;
import com.nelson.chatinterface.chatviews.CustomChatList;

@SuppressLint("NewApi")
public class ChatListAdapterHoneycomb extends ArrayAdapter<ChatBlock> {
	private Context mContext;
	private ArrayList<ChatBlock> mListOfChatBlocks;
	private int mCurrentUserId;
	public static DisplayMetrics displayMetrics;
	private static int animationDuration = 500; // Change this for sliding
												// animation time in
												// milliseconds.

	// Here is the constructor.
	public ChatListAdapterHoneycomb(Context _context,
			ArrayList<ChatBlock> _chatBlocks) {
		super(_context, R.layout.text_chat_block_left, _chatBlocks);
		mContext = _context;
		mListOfChatBlocks = _chatBlocks;
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

			// Here we set up an OnTouchListener to deal with swipes and clicks.
			if (mListOfChatBlocks.get(position).getUserId() == mCurrentUserId) {
				// In the case that the chatBlock in on the right:
				mChatBlockContainer.setOnTouchListener(new RightTouchHandler(
						mChatBlockContainer, position));
				mProfilePictureView
						.setOnTouchListener(new RightImageTouchHandler(
								mChatBlockContainer, position));
				if (mListOfChatBlocks.get(position).getOpen() == false) {
					// If the block is collapsed, do not re-renderit
					mChatBlockContainer.setX(displayMetrics.widthPixels);
					mBlankAreaParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					mBlankAreaParams.addRule(RelativeLayout.LEFT_OF,
							R.id.profile_picture_toggle);
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
					// If the block is collapsed, do not re-renderit
					mChatBlockContainer.setX(-displayMetrics.widthPixels);
					mBlankAreaParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					mBlankAreaParams.addRule(RelativeLayout.RIGHT_OF,
							R.id.profile_picture_toggle);
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

	// Here we implement the touch listeners as separate classes since we use
	// them so much.
	private class RightTouchHandler implements OnTouchListener {
		private final int MIN_SWIPE_DISTANCE = 10 * (displayMetrics.widthPixels / 160);
		private final int MIN_MOVE_DISTANCE = 5 * (displayMetrics.widthPixels / 160);
		private int prevX;
		private int newX;
		private CustomChatContainer mChatViewContainer;
		private float currentChatViewContainerX;
		private int mPositionInArray;

		public RightTouchHandler(CustomChatContainer _chatContainer,
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
				prevX = (int) event.getRawX();
				currentChatViewContainerX = mChatViewContainer.getX();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// This sections is mainly just to allow for a bit of animation
				Log.d("onTouch", "ACTION_MOVE");
				newX = (int) event.getRawX();
				if (Math.abs(newX - prevX) >= MIN_MOVE_DISTANCE) {
					// Only move after moving a bit left and right. Avoids the
					// setting of the location when you are just scrolling.
					mChatViewContainer.setX(currentChatViewContainerX
							+ (newX - prevX));
					// Make sure that the move does not move out of bounds.
					if (mChatViewContainer.getX() < displayMetrics.widthPixels
							- mChatViewContainer.getWidth()) {
						mChatViewContainer.setX(displayMetrics.widthPixels
								- mChatViewContainer.getWidth());
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				newX = (int) event.getRawX();
				if ((newX - prevX) >= MIN_SWIPE_DISTANCE) {
					// If swiping from left to right: Close Chat
					TranslateAnimation animation = new TranslateAnimation(0,
							mChatViewContainer.getWidth(), 0, 0);
					animation.setDuration(animationDuration);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationEnd(Animation arg0) {
							mListOfChatBlocks.get(mPositionInArray).setOpen(
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
					// Otherwise keep chat open
					mListOfChatBlocks.get(mPositionInArray).setOpen(true);
					notifyDataSetChanged();
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				Log.d("onTouch", "ACTION_CANCEL");
				newX = (int) event.getRawX();
				if ((newX - prevX) >= MIN_SWIPE_DISTANCE) {
					// If swiping from left to right: Close Chat
					TranslateAnimation animation = new TranslateAnimation(0,
							mChatViewContainer.getWidth(), 0, 0);
					animation.setDuration(animationDuration);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationEnd(Animation arg0) {
							mListOfChatBlocks.get(mPositionInArray).setOpen(
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
				} else {
					// Otherwise keep chat open
					mListOfChatBlocks.get(mPositionInArray).setOpen(true);
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
		private float currentChatViewContainerX;
		private int mPositionInArray;

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
				prevX = (int) event.getRawX();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// This sections is mainly just to allow for a bit of animation
				Log.d("onTouch", "ACTION_MOVE");
				newX = (int) event.getRawX();
				if (Math.abs(newX - prevX) >= MIN_MOVE_DISTANCE) {
					// Only move after moving a bit left and right. Avoids the
					// setting of the location when you are just scrolling.
					mChatViewContainer.setX(currentChatViewContainerX
							+ (newX - prevX));
					if (mChatViewContainer.getX() > 0) {
						mChatViewContainer.setX(0);
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				newX = (int) event.getRawX();
				if ((prevX - newX) >= MIN_SWIPE_DISTANCE) {
					// If swiping from right to left: Close Chat
					TranslateAnimation animation = new TranslateAnimation(0,
							-mChatViewContainer.getWidth(), 0, 0);
					animation.setDuration(animationDuration);
					animation.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation arg0) {
							mListOfChatBlocks.get(mPositionInArray).setOpen(
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
					// Otherwise keep open
					mListOfChatBlocks.get(mPositionInArray).setOpen(true);
					notifyDataSetChanged();

				}
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				Log.d("onTouch", "ACTION_CANCEL");
				newX = (int) event.getRawX();
				if ((prevX - newX) >= MIN_SWIPE_DISTANCE) {
					// If swiping from right to left: Close Chat
					TranslateAnimation animation = new TranslateAnimation(0,
							-mChatViewContainer.getWidth(), 0, 0);
					animation.setDuration(animationDuration);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationEnd(Animation arg0) {
							mListOfChatBlocks.get(mPositionInArray).setOpen(
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
					// Otherwise keep open
					mListOfChatBlocks.get(mPositionInArray).setOpen(true);
					notifyDataSetChanged();
				}
				break;
			}
			}
			return true;
		}
	}

	// Here are the image listeners. They are touch listeners so that they will
	// not cancel the scrolling when touch the icons.
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
				pressStartTime = System.currentTimeMillis();
				prevX = (int) event.getRawX();
				prevY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				long pressDuration = System.currentTimeMillis()
						- pressStartTime;
				newX = (int) event.getRawX();
				newY = (int) event.getRawY();
				if (pressDuration < MAX_CLICK_DURATION
						&& Math.abs(prevX - newX) < MAX_CLICK_DISTANCE
						&& Math.abs(prevY - newY) < MAX_CLICK_DISTANCE) {
					if (mChatViewContainer.getX() >= displayMetrics.widthPixels) {
						// If the chat is closed, open
						CustomSlidingAnimationHoneycomb animation = new CustomSlidingAnimationHoneycomb(
								(int) displayMetrics.widthPixels,
								(int) displayMetrics.widthPixels
										- mChatViewContainer.getWidth(),
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
						// If the chat is open, close
						TranslateAnimation animation = new TranslateAnimation(
								0, mChatViewContainer.getWidth(), 0, 0);
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
				pressStartTime = System.currentTimeMillis();
				prevX = (int) event.getRawX();
				prevY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_UP: {
				Log.d("onTouch", "ACTION_UP");
				long pressDuration = System.currentTimeMillis()
						- pressStartTime;
				newX = (int) event.getRawX();
				newY = (int) event.getRawY();
				if (pressDuration < MAX_CLICK_DURATION
						&& Math.abs(prevX - newX) < MAX_CLICK_DISTANCE
						&& Math.abs(prevY - newY) < MAX_CLICK_DISTANCE) {
					if (mChatViewContainer.getX() < 0) {
						// If it was closed, open
						CustomSlidingAnimationHoneycomb animation = new CustomSlidingAnimationHoneycomb(
								(int) mChatViewContainer.getX(), 0,
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
						TranslateAnimation animation = new TranslateAnimation(
								0, -mChatViewContainer.getWidth(), 0, 0);
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
			return false;
		}
	}

	// This long click listener will fill in the dialer of the phone.
	private class LongClickHandler implements OnLongClickListener {
		private String mTele;

		public LongClickHandler(String tele) {
			mTele = tele;
		}

		@Override
		public boolean onLongClick(View arg0) {
			arg0.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
					HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse(mTele));
			System.out.println("Calling: " + mTele);
			mContext.startActivity(intent);
			return false;
		}

	}
}