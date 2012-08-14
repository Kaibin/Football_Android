package com.orange.score.model.user;

import com.orange.score.service.UserService;

import android.content.SharedPreferences;

public class UserManager {

	public static final String USER_ID = "userId";
    private static SharedPreferences sharePreference;

	private static UserManager userManager = new UserManager();
	private UserService userService = new UserService();

	private UserManager() {
	}

	public static UserManager getInstance() {
		return userManager;
	}
	
	public void setSharePreference(SharedPreferences shp) {
		sharePreference = shp;
	}

	public String getUserId() {
		String uid = sharePreference.getString(USER_ID, null); 
		return uid;
	}

	public void saveUserId(String uid) {
		sharePreference.edit().putString(USER_ID, uid).commit();
	}
	
	public void checkRegister() {
		String uid = getUserId();
		if (uid == null || uid.length() == 0) {
			userService.RegisterUser();
		} 
	}

}
