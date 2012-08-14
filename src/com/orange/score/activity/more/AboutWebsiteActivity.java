package com.orange.score.activity.more;

import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;

import android.app.PendingIntent.OnFinished;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class AboutWebsiteActivity extends CommonFootballActivity {

	private final static String TAG = "AboutWebsiteActivity";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// remove titlebar
		setContentView(R.layout.more_about_website);
		
		TextView textView = (TextView) findViewById(R.id.text_version);	
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			String versionName = info.versionName;
			textView.setText("°æ±¾ºÅ£º".concat(versionName));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, e.toString());
		}
	}
	

}
