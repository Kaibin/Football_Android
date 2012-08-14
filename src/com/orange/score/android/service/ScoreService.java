package com.orange.score.android.service;

import com.orange.score.app.ScoreApplication;
import com.orange.score.model.match.FollowMatchManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class ScoreService extends Service {

	private static final String TAG = ScoreService.class.getName();
	boolean wifiOpen = false;
	WifiManager.WifiLock wifiLock = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d(TAG, this.toString() + " service <onCreate>");		
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		
		// open wifi to keep wifi alive
		openWifi();
		
		super.onStart(intent, startId);
		Log.d(TAG, this.toString() + " service <onStart>");				
	}

	@Override
	public void onDestroy(){
		
		if (wifiLock != null && wifiLock.isHeld()){
			wifiLock.release();
			wifiLock = null;
		}
		
		super.onDestroy();
		Log.d(TAG, this.toString() + " service <Destroy>");
	}
	
	private void openWifi(){
		WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		if (wm != null){
			
			/*
			if (!wm.isWifiEnabled()){
				wm.setWifiEnabled(true);
				wifiOpen = true;
			}
			*/
			
			if (wifiLock == null){
				Log.d(TAG, "create wifi lock");
				wifiLock = wm.createWifiLock("ScoreServiceWifiLock");
				wifiLock.setReferenceCounted(true);
			}
			
			if (!wifiLock.isHeld()){
				Log.d(TAG, "aquire wifi lock");
				wifiLock.acquire();
			}
			else{
				Log.d(TAG, "aquire wifi lock but it's already held");				
			}
		}		
	}

	// not used yet since we don't force user to enable wifi
	// need android.permission.CHANGE_WIFI_STATE permission is this is used
	private void closeWifi(){ 
		if (wifiOpen){
			WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
			if (wm != null)
				wm.setWifiEnabled(false);
		}		
	}

}
