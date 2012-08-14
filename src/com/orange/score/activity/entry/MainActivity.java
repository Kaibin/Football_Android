package com.orange.score.activity.entry;


import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RemoteViews;
import android.widget.TabHost;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.Notification;

import com.orange.common.android.widget.RealtimeScorePopupWindow;
import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.android.service.FollowMatchService;
import com.orange.score.android.service.ScoreService;
import com.orange.score.android.service.ScoreUpdateService;
import com.orange.score.app.ScoreApplication;
import com.orange.score.model.config.ConfigManager;
import com.orange.score.model.match.FollowMatchManager;
import com.orange.score.model.user.UserManager;
import com.orange.score.service.UserService;

import com.mobclick.android.MobclickAgent;

public class MainActivity extends TabActivity implements OnCheckedChangeListener{
	private TabHost tabHost;
	private RadioGroup radioderGroup;
	
	public static final String SCORE_UPDATE_TAG = "score_update";
	public static final String REALTIME_MATCH_TAG = "realtime_match";
	public static final String REALTIME_INDEX_TAG = "realtime_index";
	public static final String REPOSITORY_TAG = "repository";
	public static final String MORE_TAG = "more";
	public static final String TAG = "MainActivity";
    private static boolean showNetError = false;
    
    public static Notification statusNotification = null;

    BroadcastReceiver networkStateReceiver = null;
    static String currentTag = REALTIME_MATCH_TAG;
    
    public PowerManager.WakeLock mWakeLock;
    
    RealtimeScorePopupWindow realtimeScorePopupWindow = new RealtimeScorePopupWindow();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	    	    	
    	ScoreApplication.setMainActivity(this);
    	ScoreApplication.setAlive();
    	ScoreApplication.setBackground(false);
    	
    	// set share preference for config manager
    	SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
    	ConfigManager.getInstance().setSharePreference(shp);
    	
    	// set share preference for user manager
    	UserManager.getInstance().setSharePreference(shp);
    	    	
    	// start service
    	startService(new Intent(this, ScoreService.class));
    	startService(new Intent(this, FollowMatchService.class));
    	startService(new Intent(this, ScoreUpdateService.class));
    	
        super.onCreate(savedInstanceState);
        
        // init services
//        initFollowManager();
        
        
        //check if register or not
        UserManager.getInstance().checkRegister();
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        
        setContentView(R.layout.maintabs);
        
        tabHost=this.getTabHost();
        
        tabHost.addTab(buildTabSpec(SCORE_UPDATE_TAG, SCORE_UPDATE_TAG, new Intent(this,ScoreUpdateActivity.class)));
        //RealtimeMatchActivity
        tabHost.addTab(buildTabSpec(REALTIME_MATCH_TAG, REALTIME_MATCH_TAG, new Intent(this,RealtimeMatchActivity.class)));

        tabHost.addTab(buildTabSpec(REALTIME_INDEX_TAG, REALTIME_INDEX_TAG, new Intent(this,RealtimeIndexActivity2.class)));

        tabHost.addTab(buildTabSpec(REPOSITORY_TAG, REPOSITORY_TAG, new Intent(this,RepositoryActivity.class)));

        tabHost.addTab(buildTabSpec(MORE_TAG, MORE_TAG, new Intent(this,MoreActivity.class)));

        radioderGroup = (RadioGroup) findViewById(R.id.main_radio);
		radioderGroup.setOnCheckedChangeListener(this);
//        radioderGroup.check(R.id.radio_button1);
		
		selectTab(currentTag);
        
        addNotificaction();
        
        registerNetworkReceiver();
        
        // add observer for pop up window
        ScoreApplication.realtimeMatchService.addScoreLiveUpdateObserverAsFirst(realtimeScorePopupWindow);
        
        // control screen is auto locked
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        
		
    }        

	private void registerNetworkReceiver() {
    	networkStateReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		    	
		    	if (!haveInternet(getApplicationContext()) && !showNetError) {
		    		showNetError = true;
		    		Toast.makeText(getApplicationContext(), "网络连接错误，请检查网络是否连接", 
			    			Toast.LENGTH_LONG).show();
		    		Log.w(TAG, "Network Type not connected");
		    	} else {
		    		showNetError = false;
		    		Log.d(TAG, "Network Type is connected");
		    	}
		    
		    }
		};
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);        
		registerReceiver(networkStateReceiver, filter);
	}
    
    private void unregisterNetworkReceiver(){
    	if (networkStateReceiver != null){
    		unregisterReceiver(networkStateReceiver);
    		networkStateReceiver = null;
    	}
    }
    
    public boolean haveInternet(Context ctx) {
	    NetworkInfo info = (NetworkInfo) ((ConnectivityManager) ctx
	            .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

	    if (info == null || !info.isConnected()) {
	        return false;
	    }
	    if (info.isRoaming()) {
	        // here is the roaming option you can change it if you want to
	        // disable internet while roaming, just return false
	        return false;
	    }
	    return true;
	}


	private TabHost.TabSpec buildTabSpec(String tag, String label, Intent intent) {
    	return tabHost.newTabSpec(tag).setIndicator(label).setContent(intent);
    }
    
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.radio_button0:
			tabHost.setCurrentTabByTag(SCORE_UPDATE_TAG);
			currentTag = SCORE_UPDATE_TAG;
			break;
		case R.id.radio_button1:
			tabHost.setCurrentTabByTag(REALTIME_MATCH_TAG);
			currentTag = REALTIME_MATCH_TAG;
			break;
		case R.id.radio_button2:
			tabHost.setCurrentTabByTag(REALTIME_INDEX_TAG);
			currentTag = REALTIME_INDEX_TAG;
			break;
		case R.id.radio_button3:
			tabHost.setCurrentTabByTag(REPOSITORY_TAG);
			currentTag = REPOSITORY_TAG;
			break;
		case R.id.radio_button4:
			tabHost.setCurrentTabByTag(MORE_TAG);
			currentTag = MORE_TAG;
			break;
		}		
	}	
	
	@Override
	public void onDestroy(){
		Log.d(TAG, "onDestroy");
//		closeFollowMatchManager();
		unregisterNetworkReceiver();
		
		// remove listener
		ScoreApplication.realtimeMatchService.removeScoreLiveUpdateObserver(realtimeScorePopupWindow);
		RealtimeScorePopupWindow.cancelDismissPopupWindowTimer();
		
		ScoreApplication.setBackground(true);
		
		super.onDestroy();
	}
	
	private void addNotificaction() {  
		if (statusNotification != null)
			return;
		
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		statusNotification = new Notification(R.drawable.icon, "球探体育比分", System.currentTimeMillis());
		statusNotification.flags =  Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		
//		PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
		PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 设置notification的布局
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
		contentView.setImageViewResource(R.id.image, R.drawable.icon);
		contentView.setTextViewText(R.id.title, "球探体育比分");
//		statusNotification.setLatestEventInfo(context, contentTitle, contentText, contentIntent)
		statusNotification.contentView = contentView;
		statusNotification.contentIntent = pendingIntent;
		notificationManager.notify(R.drawable.icon, statusNotification);
	}
	
	@Override
	public void onResume() {
				
	    super.onResume();
	    ScoreApplication.setBackground(false);
	    // umeng API
	    MobclickAgent.setDebugMode(false);
	    MobclickAgent.onResume(this);
	    
	    if (ConfigManager.getInstance().isNotAutoLock()){
			// 在程序要退出前景的时候重新唤醒屏幕
			if (!mWakeLock.isHeld()){
				mWakeLock.acquire();
			}
		} 
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    ScoreApplication.setBackground(true);
	    // umeng API
	    MobclickAgent.setDebugMode(false);
	    MobclickAgent.onPause(this);
	    
	    if (ConfigManager.getInstance().isNotAutoLock()){
			if (mWakeLock.isHeld()){
				mWakeLock.release();
			}
//			Log.d(TAG, "<onPause> enable not auto lock");
		} 
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		if (!haveInternet(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), "网络连接错误，请检查网络是否连接",
					Toast.LENGTH_LONG).show();
			Log.w(TAG, "<onRestart>Network Type not connected");
		}
	}
	
	public void selectTab(String tag) {
		
		if (tag == null){
			return;
		}
		
		if (tag.equals(SCORE_UPDATE_TAG)){
			radioderGroup.check(R.id.radio_button0);
		}
		else if (tag.equals(REALTIME_MATCH_TAG)){
			radioderGroup.check(R.id.radio_button1);
		}
		else if (tag.equals(REALTIME_INDEX_TAG)){
			radioderGroup.check(R.id.radio_button2);
		}
		else if (tag.equals(REPOSITORY_TAG)){
			radioderGroup.check(R.id.radio_button3);
		}
		else if (tag.equals(MORE_TAG)){
			radioderGroup.check(R.id.radio_button4);
		}
		
//		tabHost.setCurrentTabByTag(tag);
	}
	
	/*
	private void exit() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.confirmExit);

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CommonFootballActivity.exitApp();
					}
				});

		builder.setNegativeButton(R.string.cancl,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

					}

				});
		builder.create().show();
	}
	
	public boolean onKeyDown(int keyCode,KeyEvent event) {   
		   // 是否触发按键为back键   
		   if (keyCode == KeyEvent.KEYCODE_BACK) { 
			   Log.i(TAG, "<onKeyDown>clcik back button,exit app");
			   exit();
			   return true;
		   } else // 如果不是back键正常响应   
		       return super.onKeyDown(keyCode,event);   
		   }
		   */
}