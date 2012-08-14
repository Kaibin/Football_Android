package com.orange.score.activity.entry;

import android.os.Bundle;

import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.app.ScoreApplication;

public class RealtimeIndexActivity extends CommonFootballActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.realindex);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ScoreApplication.currentActivity = this;
	}

	@Override
	protected void onPause() {
		super.onPause();
		ScoreApplication.currentActivity = null;
	}
}
