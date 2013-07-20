package com.greenrobot.testyesorno;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import de.tavendo.autobahn.WebSocket.ConnectionHandler;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

public class Chat {
	
	private static final String TAG = "yesornotest";
	final String wsuri = "ws://174.129.40.247:9000";
	private long fromId;
	private long user_server_id;
	private final WebSocketConnection mConnection = new WebSocketConnection();	   
	
	public Chat(long fromId, long user_server_id) {
		this.fromId = fromId;
		this.user_server_id = user_server_id;			
	}
	
	public void startChat() {
		Log.i(TAG, "startChat");
		start();
	}
	
	public void stopChat() {
		mConnection.disconnect();
	}
	
	public void sendMessage(String message) {
		if (!mConnection.isConnected()) {
			start();
		}
		if (mConnection.isConnected()) {
			mConnection.sendTextMessage(makeMessageJSON(message, 0).toString());
			MainActivity.messageSent++;
		} else {
			Log.e(TAG, "Couldn't connect to server. Try again later");
		}
	}
	
	private void start() {
		try {
			Log.i(TAG, "start " + wsuri);
			mConnection.connect(wsuri, new ConnectionHandler() {
				@Override
				public void onOpen() {
					Log.d(TAG, "Status: Connected to " + wsuri);
					mConnection.sendTextMessage(makeRegisterJSON().toString());
					MainActivity.messageSent++;
				}

				@Override
				public void onTextMessage(String payload) {
					if (!mConnection.isConnected()) {
						Log.e(TAG, "Couldn't connect to server. Try again later");
					} else {
						Log.i(TAG, "Got echo: " + payload);
						MainActivity.messageReceived++;
					}
				}

				@Override
				public void onClose(int code, String reason) {
					Log.i(TAG, "Connection lost. " + reason);
				}

				@Override
				public void onBinaryMessage(byte[] arg0) {					
				}

				@Override
				public void onRawTextMessage(byte[] arg0) {
				}
			});
		} catch (WebSocketException e) {
			Log.i(TAG, e.toString());
		}
	}
	
	private JSONObject makeMessageJSON(String s, long created) {
		JSONObject result = new JSONObject();
		try {
			result.put("action", "insert");
			result.put("from_id", user_server_id);		
			result.put("to_id", fromId);
			result.put("message", s);
			result.put("match_id", -1);
			result.put("created_at", 0);			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}		
		return result;		
	}
	
	private JSONObject makeRegisterJSON() {
		JSONObject result = new JSONObject();
		try {
			result.put("action", "register");
			result.put("from_id", user_server_id);
			result.put("to_id", fromId);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return result;		
	}	
	
}
