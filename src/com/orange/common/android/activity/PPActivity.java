package com.orange.common.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class PPActivity extends Activity {

	private static final String TAG = "PPActivity";
	ProgressDialog progressDialog;
	
	public void showProgressDialog(String title, String message, final AsyncTask task){
		progressDialog = ProgressDialog.show(this, title, message);
		if (task == null)
			return;
		
		// set cancel listener
		progressDialog.setCancelable(true);
		DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				boolean cancel = task.cancel(true);
				Log.d(TAG, "<showProgressDialog> onCancelListener is fired, task cancel result is " + cancel);
			}
		};			
		progressDialog.setOnCancelListener(cancelListener);
	}
	
	public void showProgressDialog(String title, String message){
		showProgressDialog(title, message, null); 
	}
	
	public void showProgressDialog(int titleId, int messageId){
		Resources res = getResources();
		String title = res.getString(titleId);
		String message = res.getString(messageId);
		showProgressDialog(title, message);
	}
	
	public void hideDialog(){
		if (progressDialog != null){
			Log.d(TAG, "hide progress dialog");
			progressDialog.cancel();
			progressDialog.dismiss();
		}
	}
	
	private static final int ACTION_TAKE_PHOTO = 1000; 
	Bitmap takePhotoBitmap = null;
	
	public void takePhoto(){
		takePhoto(ACTION_TAKE_PHOTO);
	}
	
	private void takePhoto(int requestCode) {
		Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		startActivityForResult(i, requestCode);		
	}
	
	private void savePhotoTaken(Intent intent){
		Bundle extras = intent.getExtras();
		if (extras == null) {
			return;
		}

		takePhotoBitmap = (Bitmap) extras.get("data");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTION_TAKE_PHOTO) {
			savePhotoTaken(data);
		}
	}
	
	public ProgressDialog getProgressDialog() {
		return this.progressDialog;
	}

}
