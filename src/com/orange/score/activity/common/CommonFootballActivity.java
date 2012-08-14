package com.orange.score.activity.common;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.KeyguardManager.KeyguardLock;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.orange.common.android.activity.PPActivity;
import com.orange.score.R;
import com.orange.score.activity.entry.MainActivity;
import com.orange.score.activity.more.FeedbackActivity;
import com.orange.score.activity.more.ScorePromptActivity;
import com.orange.score.model.config.ConfigManager;

public class CommonFootballActivity extends PPActivity{
	
	private static final String TAG = "CommonFootballActivity";
	
    public static List<Activity> activityList = new LinkedList<Activity>();  
    
//    public PowerManager.WakeLock mWakeLock;

    private static boolean showNetError = false;
    
    public boolean isTopActivity = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
//		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		
		// 加入这个标志会使得屏幕常亮
//		if (ConfigManager.getInstance().isNotAutoLock()){
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//			Log.d(TAG, "<onCreate> enable not auto lock");
//		}
		
        activityList.add(this);  

	}
	
	@Override  
    public void onDestroy() {  
//		cancelNotification();
        super.onDestroy();  
        activityList.remove(this);  
    }  
      
    public void exitApp() {  
    	cancelNotification();
		
        for(Activity activity : activityList) {  
            activity.finish(); 
        }  
        Process.killProcess(Process.myPid()); 
          
        activityList.clear();  
    }  
	public void onRefresh() {
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();
		Intent intent = new Intent();
		switch (item_id) {
		case R.id.refresh: 
			onRefresh();
			break;
		
		case R.id.setting: 
			intent.setClass(this, ScorePromptActivity.class);
			startActivity(intent);
			break;
		
		case R.id.feedback: 
			intent.setClass(this, FeedbackActivity.class);
			startActivity(intent);
			break;
		
		case R.id.exit: 
			exit();
			break;
		}
		return true;
	}
	
	private void exit() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.confirmExit);

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						exitApp();
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
			   if (isTopActivity){
				   Log.i(TAG, "<onKeyDown> top activity, ask to exit app");
				   exit();
			   }
			   else{
				   Log.i(TAG, "<onKeyDown>clcik back button,hide dialog");			   
				   hideDialog();
				   super.onBackPressed();
			   }
			   return true;
		   } else // 如果不是back键正常响应   
		       return super.onKeyDown(keyCode,event);   
		   }
	
//	 @Override
//	 public void onBackPressed() {
//	    // TODO Auto-generated method stub
//		Log.i(TAG, "<onBackPressed>click key_back, hide dialog");
//		hideDialog();
//	    super.onBackPressed();
//	 } 
	 
	 
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
	 
	 public void cancelNotification() {
		// 取消状态栏的图标
		NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(R.drawable.icon);
	 }
	 
	 
}
	

