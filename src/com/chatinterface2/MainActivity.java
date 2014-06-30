package com.chatinterface2;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends ActionBarActivity {
	private GoogleMap mGoogleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Configure the google map: No Zoom in/out, add current location
		// button, set map to hybrid satellite map.
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		CustomChatList mChatList = (CustomChatList) findViewById(R.id.chat);
		ChatListAdapter mAdapter = new ChatListAdapter(this, mChatList.getArray());
		mChatList.setAdapter(mAdapter);
	}

}
