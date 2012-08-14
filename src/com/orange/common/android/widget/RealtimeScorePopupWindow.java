package com.orange.common.android.widget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.orange.score.R;
import com.orange.score.app.ScoreApplication;
import com.orange.score.model.config.ConfigManager;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchManager;
import com.orange.score.model.match.ScoreUpdate;
import com.orange.score.network.ScoreNetworkRequest;
import com.orange.score.service.LiveUpdateChangeCallBack;

public class RealtimeScorePopupWindow implements LiveUpdateChangeCallBack {

	private static final String TAG = RealtimeScorePopupWindow.class.getName();
	private static Timer timer = null;

	private static PopupWindow popupWindow;
	private static TextView homeTeamNameView;
	private static TextView awayTeamNameView;
	private static TextView homeTeamScoreView;
	private static TextView awayTeamScoreView;
	private static TextView LeagueNameView;
	private static TextView startTimeView;
	
	private static MediaPlayer mp = null;

	List<ScoreUpdate> scoreUpdateList = new ArrayList<ScoreUpdate>();
	
	public RealtimeScorePopupWindow() {
		super();
	}

	public static void showPopupWindow(Activity activity, ScoreUpdate scoreUpdate) {

		if (activity == null) {
			return;
		}

		View parent = activity.getWindow().getDecorView().findViewById(android.R.id.content);
		View contentView = LayoutInflater.from(activity).inflate(R.layout.realtime_score_popup, null);
		
		if (popupWindow == null) {
			int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                    (float) 256.0, activity.getResources().getDisplayMetrics());
			int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                    (float) 44.0, activity.getResources().getDisplayMetrics());
			popupWindow = new PopupWindow(contentView,width, height);
			homeTeamNameView = (TextView) contentView.findViewById(R.id.live_home_team_name);
			awayTeamNameView = (TextView) contentView.findViewById(R.id.live_away_team_name);
			homeTeamScoreView = (TextView) contentView.findViewById(R.id.live_home_team_score);
			awayTeamScoreView = (TextView) contentView.findViewById(R.id.live_away_team_score);
			LeagueNameView = (TextView) contentView.findViewById(R.id.live_league_name);
			startTimeView = (TextView) contentView.findViewById(R.id.live_start_time);
		}
		
		String score = scoreUpdate.score;
		String homeTeamScore = score.split(":")[0];
		String awayTeamScore = score.split(":")[1];
		
		 homeTeamNameView.setText(scoreUpdate.homeTeamName);
		 awayTeamNameView.setText(scoreUpdate.awayTeamName);
		 homeTeamScoreView.setText(homeTeamScore);
		 awayTeamScoreView.setText(awayTeamScore);
		 LeagueNameView.setText(scoreUpdate.leagueName);
		 startTimeView.setText(scoreUpdate.minutes);
		 
		 homeTeamScoreView.setTextColor(Color.parseColor("#FF61180a"));
		 awayTeamScoreView.setTextColor(Color.parseColor("#FF61180a"));
		 
		 if (scoreUpdate.homeAwayFlag == ScoreUpdate.FLAG_HOME) {
			 homeTeamScoreView.setTextColor(Color.RED);
		 } else if (scoreUpdate.homeAwayFlag == ScoreUpdate.FLAG_AWAY) {
			 awayTeamScoreView.setTextColor(Color.RED);
		 } else {
			 homeTeamScoreView.setTextColor(Color.RED);
			 awayTeamScoreView.setTextColor(Color.RED);
		}

		try{
			popupWindow.dismiss();
		}
		catch (Exception e){
			Log.e(TAG, "dismiss popup window before show but catch exception ", e);
		}
		
		Log.d(TAG, "show RealtimeScore PopupWindow...");
		
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) 60.0, activity.getResources().getDisplayMetrics());
		popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, margin);
		
		boolean hasSound = ConfigManager.getInstance().isSound();
		boolean isVibration = ConfigManager.getInstance().isVibrate();
		
		if (hasSound) {
			if (mp == null) {
				mp = MediaPlayer.create(activity, R.raw.goals_sound);   
			}
	        mp.start();
	        mp.setOnCompletionListener(new OnCompletionListener() {

	            @Override
	            public void onCompletion(MediaPlayer mp) {
	            	mp.stop();
	            	try {
						mp.prepare();
					} catch (Exception e) {
						Log.e(TAG, "erron on play goals sound.");
						return;
					}
	            }
	        });
		}
		
		if (isVibration) {
			Vibrator vibrator;
			vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(1000);
		}
		
		// schedule timer here
		setDismissPopupWindowTimer();
	}
	
	public static void cancelDismissPopupWindowTimer(){
		if (timer != null){
			
			timer.cancel();
			timer = null;

			try{
				if (popupWindow != null){
					popupWindow.dismiss();
					popupWindow = null;
				}
			}
			catch (Exception e){
			}			
		}
	}

	public static void setDismissPopupWindowTimer() {
		if (timer == null){
			timer = new Timer();
		}
		
		timer.schedule(new TimerTask() {
	           public void run() {
	        	   try{
	        	   if (popupWindow != null) {
	        		   Log.d(TAG, "dismiss RealtimeScore PopupWindow...");
	   					popupWindow.dismiss();
	   					popupWindow = null;
	   				}
	        	   }catch (Exception e){
	        		   Log.e(TAG, "dismiss RealtimeScore PopupWindow but catch exception", e);
	        	   }
	           }
	        }, 10*1000);		// 10 seconds
	}
	
	@Override
	public void notifyScoreLiveUpdate(List<String[]> list) {
		scoreUpdateList.clear();
		if (list == null || list.size() == 0) {
			//test data
//			ScoreUpdate scoreUpdate = new ScoreUpdate();
//			scoreUpdate.homeAwayFlag = ScoreUpdate.FLAG_HOME;
//			scoreUpdate.homeTeamName = "ÇÐ¶ûÎ÷";
//			scoreUpdate.awayTeamName = "°ÍÈøÂÞÄÉ";
//			scoreUpdate.score = "4:4";
//			scoreUpdate.minutes = "18'";
//			scoreUpdate.leagueName = "Å·¹Ú";
//			scoreUpdate.updateType = ScoreUpdate.UPDATE_TYPE_SCORE;
//			showPopupWindow(ScoreApplication.currentActivity, scoreUpdate);
			
//			MatchManager matchManager = ScoreApplication.realtimeMatchService.getMatchManager();
//			Match match = matchManager.findMatchById("566014");
//			if (match == null){
//				Log.e(TAG, "match null");
//			}
//			String str = "566014^3^20110922160000^20110922170156^4^0^2^0^0^0^0^0";
//			String data[] = str.split("\\^");
//			ScoreUpdate scoreUpdate = ScoreUpdate.createScoreUpdateObject(match, data);
//			
//			if (scoreUpdate != null && scoreUpdate.updateType == ScoreUpdate.UPDATE_TYPE_SCORE){
//				scoreUpdateList.add(0, scoreUpdate);
//			}
//			for (int i = 0; i < scoreUpdateList.size(); i++) {
//				showPopupWindow(ScoreApplication.currentActivity, scoreUpdateList.get(i));
//			}
			
			return;
		}

		boolean hasChange = false;
		int size = list.size();
		for (int i=0; i<size; i++){

			String[] data = list.get(i);
			if (data == null || data.length == 0){
				Log.d(TAG, "notifyScoreLiveUpdate but data null or empty");
				continue;
			}
			
			MatchManager matchManager = ScoreApplication.realtimeMatchService.getMatchManager();
			Match match = matchManager.findMatchById(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_MATCHID]);
			if (match == null){
				Log.d(TAG, "notifyScoreLiveUpdate but match id not found, match id = " + 
						data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_MATCHID]);
				continue;
			}
			
			ScoreUpdate scoreUpdate = ScoreUpdate.createScoreUpdateObject(match, data);
			
			if (scoreUpdate != null && scoreUpdate.updateType == ScoreUpdate.UPDATE_TYPE_SCORE){
				hasChange = true;
				scoreUpdateList.add(0, scoreUpdate);
			}
		}
		
		if (hasChange){			
			if (scoreUpdateList.size() > 0){
				// don't show all, show last one only
				showPopupWindow(ScoreApplication.currentActivity, scoreUpdateList.get(scoreUpdateList.size() - 1));
			}
		}

	}

}
