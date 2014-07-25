package com.nelson.chatinterface.chatmessagemodels;

import org.json.JSONObject;

public class TextBlock extends ChatBlock {
	private String mChatMessage;

	// Here is the constructor. It also calls the Chat constructor.
	public TextBlock() {
		super();
		mChatMessage = "";
	}

	// Here are setters and getters.
	public void setText(String _text) {
		mChatMessage = _text;
	}

	public String getText() {
		return mChatMessage;
	}

	// Here we parse the text section of the JSON.
	public void parseJson(JSONObject json) {
		super.parseJson(json);
		mChatMessage = json.optString("chat");
	}

}
