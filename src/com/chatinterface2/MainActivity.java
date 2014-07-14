package com.chatinterface2;

import org.json.JSONArray;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends FragmentActivity {
	private GoogleMap mGoogleMap;
	private JSONArray mJsonArray = new JSONArray();
	public static int CURRENT_USER_ID;
	public static RelativeLayout MAP_WRAPPER;
	public static CustomChatList mChatList;
	private ToggleButton mToggleChat;
	private Button mEmergencyButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CURRENT_USER_ID = 10001714; // This is a test userId from the testJson.
		// Use static variable to allow access from the different views.
		MAP_WRAPPER = (RelativeLayout) findViewById(R.id.map_wrapper);

		// Configure the google map: No Zoom in/out Button
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
		mGoogleMap.setMyLocationEnabled(true);

		// Retrieve the ListView.
		mChatList = (CustomChatList) findViewById(R.id.chat);

		// Get the JSON values and add them to the Custom listView
		mJsonArray = ChatBlock.readJsonFile(this, "examplechat.json");
		for (int i = 0; i < mJsonArray.length(); i++) {
			mChatList.addJson(mJsonArray.optJSONObject(i));
		}

		// Here we use an adapter to set the values of the list.
		ChatListAdapter mAdapter = new ChatListAdapter(this,
				mChatList.getArrayList());

		mChatList.setAdapter(mAdapter);

		// Set up the ToggleChat Button
		mToggleChat = (ToggleButton) findViewById(R.id.toggle_chat);
		mToggleChat.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_UP: {
					// Only need ACTION_UP as touching the button should
					// automatically change state.
					if (((ToggleButton) arg0).isChecked() == true) {
						// If it was checked, then make it invisible and
						// unchecked.
						mChatList.collapseAll();
					} else {
						mChatList.expandAll();
					}
					break;
				}
				}

				return false;
			}

		});

		// Set up the emergency call button (Testing with 201.658.1091)
		mEmergencyButton = (Button) findViewById(R.id.emergency_button);
		mEmergencyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:2016581091"));
				startActivity(intent);
			}
		});
	}

	public static void enableDisableViewGroup(boolean enabled) {
		int childCount = mChatList.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = mChatList.getChildAt(i);
			view.setEnabled(enabled);
			if (view instanceof ViewGroup) {
				enableDisableViewGroup(enabled);
			}
		}
	}

}
