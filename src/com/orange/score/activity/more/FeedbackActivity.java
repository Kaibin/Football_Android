package com.orange.score.activity.more;

import java.io.UnsupportedEncodingException;

import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.model.user.UserManager;
import com.orange.score.service.UserService;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FeedbackActivity extends CommonFootballActivity {
	
	private TextView textView;
	private EditText commentText;
	private EditText contactText;
	private Button submitButton;
	private UserManager userManager;
	private static final String TAG = "FeedbackActivity";
	private static String URL = "http://bf.bet007.com/phone/Feedback.aspx?UserID=%s&content=%s&contact=%s";
	private UserService userService = new UserService();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// remove titlebar
		setContentView(R.layout.more_feedback);
		commentText = (EditText) findViewById(R.id.feedback_editText1);
		contactText = (EditText) findViewById(R.id.feedback_editText2);
		submitButton = (Button) findViewById(R.id.feedback_button);
		
		textView = (TextView) findViewById(R.id.textview_version);	

		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			String versionName = info.versionName;
			textView.setText("版本号：".concat(versionName));
		} catch (NameNotFoundException e) {
			Log.i(TAG, e.toString());
		}
		
		submitButton.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				String comment = commentText.getText().toString().trim();
				String contact = contactText.getText().toString().trim();
				if (comment.equals("")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
					builder.setTitle("请输入评价内容");
					builder.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							});
					builder.create().show();
				} else {
					try {
						comment = java.net.URLEncoder.encode(comment, "GB2312");
						contact = java.net.URLEncoder.encode(contact, "GB2312");
						userManager = UserManager.getInstance();
						String id = userManager.getUserId();
						String url = String.format(URL, id, comment, contact);
						Log.i(TAG, "Comment="+url);
						userService.CommitComment(FeedbackActivity.this, url);
					} catch (UnsupportedEncodingException e) {
						Log.e(TAG, e.toString());
					}
					
				}
			}	
		});
	}
	
}
