package com.greenrobot.testyesorno;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public static int messageSent = 0;
	public static int messageReceived = 0;
	public static int syncDone = 0;
	private static int startUserId = 1000;
	private static int countOfTestChats = 10;
	private static int countOfTestUsers = 10000;
	private Chat[] chats;
	public static int countOfActiveTestUsers = 50;//(int)(0.005*countOfTestUsers);
	public static User[] testActiveUsers = new User[countOfActiveTestUsers];
	public static boolean testActiveUsersDownloaded = false;
	private TextView tvMessageSent;
	private TextView tvMessageReceived;
	private TextView tvSyncDone;
	private Handler mHandler = new Handler();
	private Runnable mUpdateInfo = new Runnable() {
		public void run() {
			showInfo();
			if (testActiveUsersDownloaded) {
				startSync();
				testActiveUsersDownloaded = false;
			}
			mHandler.postAtTime(this, SystemClock.uptimeMillis() + 2000);
			if (messageReceived == 2*countOfTestChats) {
				for (int i = 0; i < 2*countOfTestChats; i++) {
					chats[i].sendMessage("hello from " + startUserId+i);
				}
			}
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tvMessageSent = (TextView)findViewById(R.id.tvMessageSent);
		tvMessageReceived = (TextView)findViewById(R.id.tvMessageReceived);
		tvSyncDone = (TextView)findViewById(R.id.tvSyncDone);
		chats = new Chat[2*countOfTestChats];
		
		Server.getInstance(this).getTestUsers();
		
		for (int i = 0; i < 2*countOfTestChats; i++) {
			chats[i] = new Chat(startUserId+i, startUserId+i+countOfTestUsers/2);
			chats[i].startChat();
			i++;
			chats[i] = new Chat(startUserId+i+countOfTestUsers/2, startUserId+i);
			chats[i].startChat();
		}
		mHandler.post(mUpdateInfo);
	}
	
	public void showInfo() {
		tvMessageSent.setText("Message sent: " + messageSent);
		tvMessageReceived.setText("Message received: " + messageReceived);
		tvSyncDone.setText("Sync done: " + syncDone);
	}
	
	public void startSync() {
		for(User u : testActiveUsers) Server.getInstance(this).sync(u);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		for (int i = 0; i < 2*countOfTestChats; i++) {
			chats[i].stopChat();
		}
		messageSent = 0;
		messageReceived = 0;
		syncDone = 0;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
