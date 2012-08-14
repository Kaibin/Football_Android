package com.orange.score.service;

import java.util.UUID;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.orange.common.android.activity.PPActivity;
import com.orange.score.R;
import com.orange.score.activity.entry.MainActivity;
import com.orange.score.model.user.UserManager;

public class UserService {

	private static final String TAG = "UserService";
	
	public void RegisterUser() {
		RegisterUserTask task = new RegisterUserTask();
		task.execute();
	}

	public void CommitComment(PPActivity activity, String url) {
		CommitCommentTask task = new CommitCommentTask(activity, url);
		task.execute(url);
	}
	
	public void updateClient(PPActivity activity, String checkURL, String updateURL) {
		updateClientTask task = new updateClientTask(activity, checkURL, updateURL, false);
		task.execute("");
	}
	
	public void updateClientWhenLaunch(PPActivity activity, String checkURL, String updateURL) {
		updateClientTask task = new updateClientTask(activity, checkURL, updateURL, true);
		task.execute("");
	}
	
	private class updateClientTask extends AsyncTask<String, Void, String> {

		PPActivity activity;
		String checkURL;
		String updateURL;
		boolean isBackgroundUpdate = false;
		
		public updateClientTask(PPActivity activity, String checkURL, String updateURL, boolean isBackgroundUpdate) {
			this.activity = activity;
			this.checkURL = checkURL;
			this.updateURL = updateURL;
			this.isBackgroundUpdate = isBackgroundUpdate;
		}
		
		@Override
		protected void onPreExecute() {
			if (!isBackgroundUpdate)
				activity.showProgressDialog("", "正在检查是否有新版本...");
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			return httpGet(checkURL);
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (!isBackgroundUpdate)
				activity.hideDialog();

			if (result == null) {
				Log.w(TAG, "update client failed.");
				if (!isBackgroundUpdate){
					Toast.makeText(activity.getApplicationContext(),
						"网络错误，请检查网络是否连接", Toast.LENGTH_SHORT).show();
				}
				return;
			} else {
				PackageManager manager = activity.getPackageManager();
				try {
					PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
					String versionName = info.versionName;
					double double_versonName = Double.valueOf(versionName);
					int versionCode = info.versionCode;
						double double_newVersionName = Double.valueOf(result.trim());
						AlertDialog.Builder builder = new AlertDialog.Builder(
								activity);
						boolean hasUpdate = (double_newVersionName != double_versonName);
						if (!hasUpdate) {
							builder.setTitle("客户端已经是最新版本");
							builder.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
										}
									});
							if (!this.isBackgroundUpdate)
								builder.create().show();
						} else {
							final String downloadURL = updateURL + result + ".apk";
							builder.setTitle("检测到有新版本，请问是否升级到最新版本? "); // +  downloadURL);							
							builder.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											Intent intent = new Intent(Intent.ACTION_VIEW);
											intent.setData(Uri.parse(downloadURL));
											activity.startActivity(intent);
										}
									}).setNegativeButton("暂不升级", null);
							builder.create().show();
						}
					} catch (Exception e) {
						Log.e(TAG, e.toString());
					}						
			}
		}				
	}

	// 提交评论
	private class CommitCommentTask extends AsyncTask<String, Void, String> {

		PPActivity activity;
		String url;

		public CommitCommentTask(PPActivity activity, String url) {
			this.activity = activity;
			this.url = url;
		}

		@Override
		protected void onPreExecute() {
			activity.showProgressDialog("", "信息反馈中...");
		}

		@Override
		protected String doInBackground(String... msg) {
			return httpGet(url);
		}

		@Override
		protected void onPostExecute(String result) {
			activity.hideDialog();

			if (result == null) {
				Log.w(TAG, "commit comment failed.");
				Toast.makeText(activity.getApplicationContext(),
						"网络错误，请检查网络是否连接", Toast.LENGTH_SHORT).show();
				return;
			} else {
				Log.i(TAG, "commit comment success.respond=" + result);
				Toast.makeText(activity.getApplicationContext(), "您的反馈已经提交",
						Toast.LENGTH_SHORT).show();
			}

		}
	}
	
	private class RegisterUserTask extends AsyncTask<String, Void, String>{
		String url = "http://bf.bet007.com/phone/Register.aspx?kind=2&token=";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i(TAG, "send register: " + url);
			url += UUID.randomUUID().toString();
		}
		
		@Override
		protected String doInBackground(String... params) {
			return httpGet(url);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) {
				return;
			} else {
				Log.i(TAG, "register success, user id =" + result);
				UserManager.getInstance().saveUserId(result);
			}
		}
		
	}
	
	public static String httpGet(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		ResponseHandler<String> strRespHandler = new BasicResponseHandler();

		String response = null;
		try {
			Log.i(TAG, "Send http request: " + url);
			response = httpclient.execute(httpGet, strRespHandler);
			Log.i(TAG, "Get http response: " + response);									
		} catch (Exception e) {
			Log.i(TAG, "catch exception while send http request " + e.toString(), e);
		}

		return response;
	}

	

	
}
