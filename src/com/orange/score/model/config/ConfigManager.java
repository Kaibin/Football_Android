package com.orange.score.model.config;


import java.util.Date;

import android.R.integer;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.orange.common.util.DateUtil;
import com.orange.score.constants.LanguageType;

public class ConfigManager {

	private static final String TAG = "ConfigManager";

	private static final String IS_FIRST_USAGE = "IS_FIRST_USAGE";

	// thread-safe singleton implementation
    private static ConfigManager configManager = new ConfigManager(); 
    
    private static LanguageType languageType = LanguageType.MANDARY;
    
    private static SharedPreferences sharePreference;
    
    long serverDiffTime = 0;
	   
    private ConfigManager(){	
    	
    }

    public static ConfigManager getInstance() { 
    	return configManager; 
    } 
	
    public static LanguageType getLanguage(){
    	if (sharePreference == null) {
    		return LanguageType.MANDARY;
    	}
    	int value  = sharePreference.getInt("LanguageType", LanguageType.MANDARY.intValue());
    	languageType.setIntValue(value);
    	Log.i(TAG,"languageType value is "+value);
		return languageType;
	}
    
    public static void setLanguage(LanguageType type) {
    	languageType = type;
    	
    	int value = type.intValue();
    	Log.i(TAG, "set languageType value="+value);
    	sharePreference.edit().putInt("LanguageType", value).commit();
    }
    
    public static void setLanguage(int typeValue) {
    	if (typeValue == 0) {
    		languageType = LanguageType.MANDARY;
    	} else if (typeValue == 1) {
    		languageType = LanguageType.CANTONESE;
    	} else {
    		languageType = LanguageType.CROWN;
    	}
    	Log.i(TAG, "set languageType value="+typeValue);
    	sharePreference.edit().putInt("LanguageType", typeValue).commit();
    }
    	
	public boolean isVibrate() {
		return sharePreference.getBoolean("vibrate_checkboxPref", true);
	}
	
	public boolean isSound() {
		return sharePreference.getBoolean("sound_checkboxPref", true);
	}
	
	public boolean isScorePush() {
		return sharePreference.getBoolean("push_checkboxPref", true);
	}
	
	public boolean isNotAutoLock() {
		boolean isNotAutoLock = sharePreference.getBoolean("autoLock_checkboxPref", true);
		Log.i("ConfigManager", "not autolock is"+isNotAutoLock);
		return isNotAutoLock;
	}
	
	public String getRefreshInterval() {
		return sharePreference.getString("refleshTime_editTextPref", "20");
	}
	
	public void setSharePreference(SharedPreferences shp) {
		sharePreference = shp;
	}
	
	public void setServerDifferenceTime(String serverDateString) {
		Date serverDate = DateUtil.dateFromChineseStringByFormat(serverDateString, DateUtil.DATE_FORMAT);
		Date now = new Date();
		this.serverDiffTime = (serverDate.getTime() - now.getTime()) / 1000;  
		
		Log.d(TAG, "update server difference time to " + this.serverDiffTime + " seconds");
	}	

	public static long getServerDifferenceTime() {		
		return getInstance().serverDiffTime;
	}

	public boolean getIsFirstUsage() {
		return sharePreference.getBoolean(IS_FIRST_USAGE, true);
	}
	
	public void setIsFirstUsage() {
		SharedPreferences.Editor editor = sharePreference.edit();
		editor.putBoolean(IS_FIRST_USAGE, false);
		editor.commit();
	}

}
