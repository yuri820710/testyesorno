package com.greenrobot.testyesorno;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	
	String facebookId = null;
	String gender = null;
	String longitude = "0";
	String latitude = "0";
	String name = null;
	String birthday = null;
	String email = null;
	String image1 = null;
	String image2 =  null;
	String image3 = null;
	String image4 = null;
	String image5 = null;
	String matchAgeFrom = null;
	String matchAgeTo = null;
	String matchDistance = null;
	String interestedInM = "0";
	String interestedInF = "0";
	String aboutMe = "";
	
	public User (JSONObject jo) {
		try {
			facebookId = jo.getString("facebook_id");		
			gender = jo.getString("gender");
			name = jo.getString("name");
			birthday = jo.getString("birthday");
			email = jo.getString("email");
			image1 = jo.getString("image_1");
			image2 =  jo.getString("image_2");
			image3 = jo.getString("image_3");
			image4 = jo.getString("image_4");
			image5 = jo.getString("image_5");
			interestedInM = jo.getString("interested_in_m");
			interestedInF = jo.getString("interested_in_f");
			aboutMe = jo.getString("about_me");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "User [facebookId=" + facebookId + ", gender=" + gender
				+ ", longitude=" + longitude + ", latitude=" + latitude
				+ ", name=" + name + ", birthday=" + birthday + ", email="
				+ email + ", image1=" + image1 + ", image2=" + image2
				+ ", image3=" + image3 + ", image4=" + image4 + ", image5="
				+ image5 + ", matchAgeFrom=" + matchAgeFrom + ", matchAgeTo="
				+ matchAgeTo + ", matchDistance=" + matchDistance
				+ ", interestedInM=" + interestedInM + ", interestedInF="
				+ interestedInF + ", aboutMe=" + aboutMe + "]";
	}
	
	

}
