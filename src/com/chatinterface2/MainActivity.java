package com.chatinterface2;

import org.json.JSONArray;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends ActionBarActivity {
	private GoogleMap mGoogleMap;
	private JSONArray mJsonArray = new JSONArray();
	public static int CURRENT_USER_ID;
	public static RelativeLayout MAP_WRAPPER;

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
		CustomChatList mChatList = (CustomChatList) findViewById(R.id.chat);

		// Get the JSON values and add them to the Custom listView
		mJsonArray = ChatBlock.readJsonFile(this, "examplechat.json");
		for (int i = 0; i < mJsonArray.length(); i++) {
			mChatList.addJson(mJsonArray.optJSONObject(i));
		}

		// Here we use an adapter to set the values of the list.
		ChatListAdapter mAdapter = new ChatListAdapter(this,
				mChatList.getArrayList());

		mChatList.setAdapter(mAdapter);

	}

}
