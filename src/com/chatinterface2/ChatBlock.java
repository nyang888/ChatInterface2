package com.chatinterface2;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;

public class ChatBlock {
	private int mChatId;
	private long mEDate;
	private int mUserId;
	private String mUsername;
	private Date mDate;

	// Here is a constructor. You should only need color as parseJSON will fill
	// in the information.
	public ChatBlock() {
		mChatId = 0;
		mEDate = 0;
		mUserId = 0;
		mUsername = "";
	}

	// Here are the setters and getters.
	public void setChatId(int _chatId) {
		mChatId = _chatId;
	}

	public void setEDate(long _eDate) {
		mEDate = _eDate;
	}

	public void setUserId(int _userId) {
		mUserId = _userId;
	}

	public void setUsername(String _username) {
		mUsername = _username;
	}

	public int getChatId() {
		return mChatId;
	}

	public long getEDate() {
		return mEDate;
	}

	public Date getDate() {
		return mDate;
	}

	public String getDateString() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(mDate);
		StringBuilder sb = new StringBuilder();
		if (cal.HOUR_OF_DAY < 12) {
			sb.append(cal.HOUR_OF_DAY);
			sb.append(":");
			sb.append(cal.MINUTE);
			sb.append(" am");
		} else if (cal.HOUR_OF_DAY == 12) {
			sb.append(cal.HOUR_OF_DAY);
			sb.append(":");
			sb.append(cal.MINUTE);
			sb.append(" pm");
		} else {
			sb.append(cal.HOUR_OF_DAY - 12);
			sb.append(":");
			sb.append(cal.MINUTE);
			sb.append(" pm");
		}
		return sb.toString();
	}

	public int getUserId() {
		return mUserId;
	}

	public String getUsername() {
		return mUsername;
	}

	// Parse the JSON object and extract the ChatID and eDate
	public void parseJson(JSONObject json) {

		mChatId = json.optInt("eventchatid");
		mEDate = json.optLong("edatecreated");
		mUserId = json.optInt("userid");
		mUsername = json.optString("username");
		mDate = new Date(mEDate); // Here we translate the eDate into a
									// regular date.

	}

	// This method will read the .json file that will be passed. It will parse
	// the file and return a JSON Array that will be sent to addJSON.
	public static JSONArray readJsonFile(Activity _activity, String assetUrl) {
		JSONArray mTestMessages = new JSONArray();
		String mJsonString;
		try {
			InputStream is = _activity.getAssets().open(assetUrl);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			mJsonString = new String(buffer, "UTF-8");

			mTestMessages = new JSONArray(mJsonString);
		} catch (Exception e) {
			System.err.println("Caught Exception: " + e.getMessage());
		}

		return mTestMessages;
	}
}