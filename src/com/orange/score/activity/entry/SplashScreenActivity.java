package com.orange.score.activity.entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.orange.score.R;
import com.orange.score.app.ScoreApplication;

public class SplashScreenActivity extends Activity {
	
	//logo��ʾʱ��
	private int duration = 1000;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��������
		
		setContentView(R.layout.splashscreen);

		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					while (!ScoreApplication.isAlive() && duration > 0) {
						sleep(100);
						duration -= 100; // 100ms per time
					}
				} catch (InterruptedException e) {
					// do nothing
				} finally {
					finish();
					// ������Ӧ��
					startActivity(new Intent(SplashScreenActivity.this,
							MainActivity.class));
				}
			}
		};
		splashTread.start();
	}
}
