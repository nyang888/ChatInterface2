package com.chatinterface2;

import org.json.JSONArray;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends ActionBarActivity {
	private GoogleMap mGoogleMap;
	private JSONArray mJsonArray = new JSONArray();
	private int mCurrentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mCurrentUser = 10001713; // This is a test userId from the testJson.

		// Configure the google map: No Zoom in/out, add current location
		// button, set map to hybrid satellite map.
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		// Retrieve the ListView.
		CustomChatList mChatList = (CustomChatList) findViewById(R.id.chat);

		// Get the JSON values and add them to the Custom listView
		mJsonArray = ChatBlock.readJsonFile(this, "examplechat.json");
		for (int i = 0; i < mJsonArray.length(); i++) {
			mChatList.addJson(mJsonArray.optJSONObject(i));
		}

		// Here we use an adapter to set the values of the list.
		ChatListAdapter mAdapter = new ChatListAdapter(this,
				mChatList.getArray(), mCurrentUser);
		mChatList.setAdapter(mAdapter);

	}

}
