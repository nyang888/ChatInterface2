package com.nelson.chatinterface;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.chatinterface2.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.nelson.chatinterface.chatmessagemodels.ChatBlock;
import com.nelson.chatinterface.chatviews.CustomChatList;
import com.nelson.chatinterface.gingerbreadcontrollers.ChatListAdapterGingerbread;
import com.nelson.chatinterface.honeycombcontrollers.ChatListAdapterHoneycomb;

public class MainActivity extends FragmentActivity implements LocationListener {

	private GoogleMap mGoogleMap;
	private JSONArray mJsonArray = new JSONArray();
	public static int CURRENT_USER_ID;
	public static FrameLayout MAP_WRAPPER;
	public static CustomChatList mChatList;
	private ToggleButton mToggleChat;
	private Button mEmergencyButton;
	private LocationManager mLocationManager;
	private static final long MIN_TIME = 400;
	private static final float MIN_DISTANCE = 1000;
	private LatLng mCurrentLatLng;
	private Button mLocationButton;
	private Button mAddMediaButton;
	public static DisplayMetrics displayMetrics;
	public static boolean isMapCentered;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CURRENT_USER_ID = 10001714; // This is a test userId from the testJson.
		// Use static variable to allow access from the different views.
		MAP_WRAPPER = (FrameLayout) findViewById(R.id.map_wrapper);
		displayMetrics = getResources().getDisplayMetrics();
		isMapCentered = false;

		// Configure the google map: No Zoom in/out Button
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
		mGoogleMap.getUiSettings().setCompassEnabled(false);
		mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.setIndoorEnabled(true);
		mGoogleMap.setBuildingsEnabled(true);

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE,
				(LocationListener) this);

		// Retrieve the ListView.
		mChatList = (CustomChatList) findViewById(R.id.chat);

		// Get the JSON values and add them to the Custom listView
		mJsonArray = ChatBlock.readJsonFile(this, "examplechat.json");
		for (int i = 0; i < mJsonArray.length(); i++) {
			mChatList.addJson(mJsonArray.optJSONObject(i));
		}

		// This is for phones at API10 and below.
		if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
			// Here we use an adapter to set the values of the list.
			ChatListAdapterGingerbread mAdapterGingerbread = new ChatListAdapterGingerbread(
					this, mChatList.getArrayList());
			mChatList.setAdapter(mAdapterGingerbread);
		} else { // This is for phones at API11 and above
			// Here we use an adapter to set the values of the list.
			ChatListAdapterHoneycomb mAdapterHoneycomb = new ChatListAdapterHoneycomb(
					this, mChatList.getArrayList());
			mChatList.setAdapter(mAdapterHoneycomb);
			mChatList.setMotionEventSplittingEnabled(false);
		}

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
				// Only fills in the Dialer, DOES NOT call automatically.
				intent.setData(Uri.parse("tel:911"));
				startActivity(intent);
			}
		});

		// Set up myLocation button
		mLocationButton = (Button) findViewById(R.id.location_center_button);
		mLocationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mGoogleMap.getCameraPosition().zoom <= 13) {
					// If the camera is zoomed too far out, we need to zoom in a
					// little.
					CameraUpdate cameraUpdate = CameraUpdateFactory
							.newLatLngZoom(mCurrentLatLng, 16);
					mGoogleMap.animateCamera(cameraUpdate);
				} else if (isMapCentered == true) {
					CameraUpdate cameraUpdate = CameraUpdateFactory
							.newCameraPosition(new CameraPosition(
									mCurrentLatLng, mGoogleMap
											.getCameraPosition().zoom,
									mGoogleMap.getCameraPosition().tilt, 0));
					mGoogleMap.animateCamera(cameraUpdate);
				} else {
					CameraUpdate cameraUpdate = CameraUpdateFactory
							.newLatLng(mCurrentLatLng);
					mGoogleMap.animateCamera(cameraUpdate);
				}
				isMapCentered = true;
			}
		});

		// Set Add-Media Button
		mAddMediaButton = (Button) findViewById(R.id.add_media);
		mAddMediaButton.setOnClickListener(new OnClickListener() {
			Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);

			@Override
			public void onClick(View arg0) {
				mediaChooser.setType("video/*, images/*");
				startActivityForResult(mediaChooser, 1);
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

	@Override
	public void onLocationChanged(Location location) {
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		mCurrentLatLng = latLng;
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
				16);
		mGoogleMap.animateCamera(cameraUpdate);
		isMapCentered = true;
		mLocationManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}

}
