package com.greenrobot.testyesorno;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Server {
	public static final String TAG = "YesOrNo";
	private static Server sInstance;
	private Activity myContext;

	public Server(final Activity context) {
		this.myContext = context;
	}

	public void sync(User user) {
		new SyncTask(this.myContext, user).execute();
	}

	class SyncTask extends AsyncTask<Void, Void, ResponseItem> {

		private final Activity mContext;
		private final User user;

		SyncTask(final Activity context, final User u) {
			this.mContext = context;
			this.user = u;
		}

		@Override
		protected ResponseItem doInBackground(final Void... params) {
			String baseURL = CommonConstants.SYNC_URL;

			JSONObject baseJSON = new JSONObject();

			String facebookId = user.facebookId;
			String gender = user.gender;
			String longitude = user.longitude;
			String latitude = user.latitude;
			String name = user.name;
			String birthday = user.birthday;
			String email = user.email;

			String image1 = user.image1;
			String image2 = user.image2;
			String image3 = user.image3;
			String image4 = user.image4;
			String image5 = user.image5;

			String interestedInM = user.interestedInM;
			String interestedInF = user.interestedInF;
			String aboutMe = user.aboutMe;

			try {
				baseJSON.put("facebook_id", facebookId);
				baseJSON.put("gender", gender);
				baseJSON.put("longitude", longitude);
				baseJSON.put("latitude", latitude);
				baseJSON.put("name", name);
				baseJSON.put("birthday", birthday);
				baseJSON.put("email", email);
				baseJSON.put("interested_in_m", interestedInM);
				baseJSON.put("interested_in_f", interestedInF);
				if (image1 != null) {
					baseJSON.put("image_1", image1);
				}
				if (image2 != null) {
					baseJSON.put("image_2", image2);
				}
				if (image3 != null) {
					baseJSON.put("image_3", image3);
				}
				if (image4 != null) {
					baseJSON.put("image_4", image4);
				}
				if (image5 != null) {
					baseJSON.put("image_5", image5);
				}
				if (aboutMe != null) {
					baseJSON.put("about_me", aboutMe);
				}				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String jsonString = baseJSON.toString();
			// String jsonString = friends.toString();
//			Log.v("baseJSON", jsonString);

			return fetchDataForSync(baseURL, jsonString, this.mContext);
		}

		@Override
		protected void onPostExecute(final ResponseItem result) {
			super.onPostExecute(result);
			MainActivity.syncDone++;
		}
	}

	public ResponseItem fetchDataForSync(final String baseURL, final String json,
			final Context context) {
		Header[] headers = null;

		headers = new Header[] { new BasicHeader("Accept-Language", "en,es"),
				new BasicHeader("Content-Type", "application/json; charset=utf-8"),
				new BasicHeader("Accept-Encoding", "gzip,deflate") };

		String responseString = HTTPHelper.doRequest(baseURL, HTTPHelper.POST, null, headers, json);
//		Log.i(Server.TAG, "json: " + json);
//		Log.i(Server.TAG, "fetchData(): " + responseString);
		if (!HTTPHelper.isNullOrEmpty(responseString)) {
			ResponseItem responseItem = new ResponseItem();	
			return responseItem;
		}
		return null;
	}	

	public static Server getInstance(final Activity context) {
		if (sInstance == null) {
			sInstance = new Server(context);
		} else
			sInstance.myContext = context;
		return sInstance;
	}
	
	public void getTestUsers() {
		new GetTestUsersTask().execute();
	}

	class GetTestUsersTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Header[] headers = null;

			headers = new Header[] { new BasicHeader("Accept-Language", "en,es"),
					new BasicHeader("Content-Type", "application/json; charset=utf-8"),
					new BasicHeader("Accept-Encoding", "gzip,deflate") };
			String responseString = HTTPHelper.doRequest("http://yesornomeet.com/yesorno/gettestusers.php", HTTPHelper.POST, null, headers, null);
			if (!HTTPHelper.isNullOrEmpty(responseString)) {
				return responseString;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				JSONArray ja = new JSONArray(result);
				for (int i=0;i<MainActivity.countOfActiveTestUsers;i++) {//ja.length();i++) {
					MainActivity.testActiveUsers[i] = new User(ja.getJSONObject(i));
//					Log.i(Server.TAG, "user " + MainActivity.testActiveUsers[i].toString());
				}
				MainActivity.testActiveUsersDownloaded = true;
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
		
	}
}
