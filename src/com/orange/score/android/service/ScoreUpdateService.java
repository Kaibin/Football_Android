package com.orange.score.android.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.orange.score.R;
import com.orange.score.activity.entry.MainActivity;
import com.orange.score.app.ScoreApplication;
import com.orange.score.model.config.ConfigManager;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchManager;
import com.orange.score.model.match.ScoreUpdate;
import com.orange.score.network.ScoreNetworkRequest;
import com.orange.score.service.LiveUpdateChangeCallBack;
import com.orange.score.service.MatchService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

public class ScoreUpdateService extends Service implements LiveUpdateChangeCallBack {

	private static final String TAG = ScoreUpdateService.class.getName();
	private static final int SCORE_UPDATE_CHANGE = 1;
	public static final String SCORE_UPDATE_BROADCAST = "SCORE_UPDATE_BROADCAST";
	private static final String SCORE_UPDATE_TYPE = "SCORE_UPDATE_TYPE";
	static List<ScoreUpdate> scoreUpdateList = new ArrayList<ScoreUpdate>();
	static HashMap<String, ScoreUpdate> scoreUpdateMap = new HashMap<String, ScoreUpdate>();
	private int messageNotificationID = 1000;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static List<ScoreUpdate> getScoreUpdateList(){
		return scoreUpdateList;
	}
	
	@Override
	public void onCreate(){
		
		messageNotificationID = (int)(System.currentTimeMillis() / 1000);
		
		super.onCreate();
		Log.d(TAG, this.toString() + " <onCreate>");			
		
		ScoreApplication app = (ScoreApplication)getApplication();
		MatchService matchService = app.getRealtimeMatchService();
		matchService.addScoreLiveUpdateObserverAsFirst(this);
	}
	
	@Override
	public void onStart(Intent intent, int startId){				
		super.onStart(intent, startId);
		Log.d(TAG, this.toString() + " <onStart>");				

		// post a message to realtiime match activity so that it can show the follow match count here
		broadcastScoreUpdate();
	}

	@Override
	public void notifyScoreLiveUpdate(List<String[]> list) {
		if (list == null || list.size() == 0)
			return;

		ScoreApplication app = (ScoreApplication)getApplication();
		MatchService matchService = app.getRealtimeMatchService();
		MatchManager matchManager = matchService.getMatchManager();
		
		boolean hasChange = false;
		int size = list.size();
		for (int i=0; i<size; i++){

			String[] data = list.get(i);
			if (data == null || data.length == 0){
				Log.d(TAG, "notifyScoreLiveUpdate but data null or empty");
				continue;
			}
			
			Match match = matchManager.findMatchById(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_MATCHID]);
			if (match == null){
				Log.d(TAG, "notifyScoreLiveUpdate but match id not found, match id = " + 
						data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_MATCHID]);
				continue;
			}
			
			ScoreUpdate scoreUpdate = createScoreUpdateObject(match, data);
			if (scoreUpdate != null){
				Log.d(TAG, "score update has change, matchId=" + match.getMatchId() + 
						", type = " + scoreUpdate.updateType + ", home/away flag = " + scoreUpdate.homeAwayFlag);
				hasChange = true;
				
				addScoreUpdate(match.matchId, scoreUpdate);	
				
				fireScoreUpdateNotification(scoreUpdate, match.matchId);				
				
				// update match update time for show the background changes in list view
				match.latestScoreUpdateTime = System.currentTimeMillis();
			}
		}
		
		if (hasChange){
			broadcastScoreUpdate();
		}
	}

	


	private void broadcastScoreUpdate() {
		Log.d(TAG, "broadcastScoreUpdate");
		Intent intent = new Intent(SCORE_UPDATE_BROADCAST);
		intent.putExtra(SCORE_UPDATE_TYPE, SCORE_UPDATE_CHANGE);
		sendBroadcast(intent);		
	}
	
	public ScoreUpdate createScoreUpdateObject(Match match, String[] data){
		if (match == null || data == null || data.length == 0)
			return null;
		
		
		ScoreUpdate obj = new ScoreUpdate();
		
		boolean homeTeamScoreUpdate = false;
		boolean homeTeamRedUpdate = false;
		boolean homeTeamYellowUpdate = false;
		boolean awayTeamScoreUpdate = false;
		boolean awayTeamRedUpdate = false;
		boolean awayTeamYellowUpdate = false;
		
		String lastHomeTeamScore = "0";
		String lastAwayTeamScore = "0";
		String lastHomeTeamRed = "0";
		String lastAwayTeamRed = "0";
		String lastHomeTeamYellow = "0";
		String lastAwayTeamYellow = "0";
				
		if (scoreUpdateMap.containsKey(match.matchId)){
			ScoreUpdate lastUpdate = scoreUpdateMap.get(match.matchId);
			if (lastUpdate != null){
				
				lastHomeTeamScore = lastUpdate.homeTeamScore;
				lastAwayTeamScore = lastUpdate.awayTeamScore;

				lastHomeTeamRed = lastUpdate.homeTeamRed;
				lastAwayTeamRed = lastUpdate.awayTeamRed;

				lastHomeTeamYellow = lastUpdate.homeTeamYellow;
				lastAwayTeamYellow = lastUpdate.awayTeamYellow;
			}
		}
		else{
			lastHomeTeamScore = match.homeTeamScore;
			lastAwayTeamScore = match.awayTeamScore;

			lastHomeTeamRed = match.homeTeamRed;
			lastAwayTeamRed = match.awayTeamRed;

			lastHomeTeamYellow = match.homeTeamYellow;
			lastAwayTeamYellow = match.awayTeamYellow;			
		}

		if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_SCORE], lastHomeTeamScore)){
	    	homeTeamScoreUpdate = true;
	    }	    
	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_SCORE], lastAwayTeamScore)){
	    	awayTeamScoreUpdate = true;
	    }

	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_RED], lastHomeTeamRed)){
	    	homeTeamRedUpdate = true;
	    }	    
	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_RED], lastAwayTeamRed)){
	    	awayTeamRedUpdate = true;
	    }

	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_YELLOW], lastHomeTeamYellow)){
	    	homeTeamYellowUpdate = true;
	    }	    
	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_YELLOW], lastAwayTeamYellow)){
	    	awayTeamYellowUpdate = true;
	    }
	    
	    if (homeTeamScoreUpdate || awayTeamScoreUpdate){
	    	obj.updateType = ScoreUpdate.UPDATE_TYPE_SCORE;
	    	if (homeTeamScoreUpdate && awayTeamScoreUpdate){
	    		obj.homeAwayFlag = ScoreUpdate.FLAG_BOTH;
	    	}
	    	else if (homeTeamScoreUpdate){
	    		obj.homeAwayFlag = ScoreUpdate.FLAG_HOME;
	    	}
	    	else if (awayTeamScoreUpdate){
	    		obj.homeAwayFlag = ScoreUpdate.FLAG_AWAY;
	    	}	    		
	    }
	    else if (homeTeamRedUpdate || awayTeamRedUpdate){
	    	obj.updateType = ScoreUpdate.UPDATE_TYPE_RED_CARD;	    	
	    	if (homeTeamRedUpdate && awayTeamRedUpdate){
	    		obj.homeAwayFlag = ScoreUpdate.FLAG_BOTH;
	    	}
	    	else if (homeTeamRedUpdate){
	    		obj.homeAwayFlag = ScoreUpdate.FLAG_HOME;
	    	}
	    	else if (awayTeamRedUpdate){
	    		obj.homeAwayFlag = ScoreUpdate.FLAG_AWAY;
	    	}	    		
	    }
	    else if (homeTeamYellowUpdate || awayTeamYellowUpdate){
	    	obj.updateType = ScoreUpdate.UPDATE_TYPE_YELLOW_CARD;	    		    	
	    	if (homeTeamYellowUpdate && awayTeamYellowUpdate){
	    		obj.homeAwayFlag = ScoreUpdate.FLAG_BOTH;
	    	}
	    	else if (homeTeamYellowUpdate){
	    		obj.homeAwayFlag = ScoreUpdate.FLAG_HOME;
	    	}
	    	else if (awayTeamYellowUpdate){
	    		obj.homeAwayFlag = ScoreUpdate.FLAG_AWAY;
	    	}	    		
	    } 
	    else{
	    	// the same as current match data, no update
	    	return null;
	    }
	    
	    obj.score = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_SCORE].
	    						concat(":").
	    						concat(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_SCORE]);
	    obj.redCardScore = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_RED].
										concat(":").
										concat(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_RED]);
	    obj.yellowCardScore = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_YELLOW].
											concat(":").
											concat(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_YELLOW]);

	    obj.homeTeamScore = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_SCORE];
	    obj.awayTeamScore = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_SCORE];
	    
	    obj.homeTeamRed = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_RED];
	    obj.awayTeamRed = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_RED];

	    obj.homeTeamYellow = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_YELLOW];
	    obj.awayTeamYellow = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_YELLOW];

	    obj.scoreUpdateData = data;		
		obj.minutes = match.matchMinuteString();
		obj.homeTeamName = match.homeTeamName;
		obj.awayTeamName = match.awayTeamName;
		obj.date = match.getDateString();
		obj.leagueName = match.getLeagueName();
		obj.leagueColor = match.getLeagueColor();
		
		return obj;
	}
	
	public boolean isDataChange(String newValue, String oldValue){
		if (oldValue == null || newValue == null)
			return false;
		
		int oldIntValue = 0;
		int newIntValue = 0;
		if (oldValue.length() > 0){
			oldIntValue = Integer.parseInt(oldValue);
		}
		
		if (newValue.length() > 0){
			newIntValue = Integer.parseInt(newValue);
		}
		
		if (newIntValue > oldIntValue){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static void clearData(){
		scoreUpdateList.clear();
		scoreUpdateMap.clear();
	}
	
	private void addScoreUpdate(String matchId, ScoreUpdate scoreUpdate) {
		scoreUpdateList.add(0, scoreUpdate);
		scoreUpdateMap.put(matchId, scoreUpdate);
	}
	

	private void fireScoreUpdateNotification(ScoreUpdate scoreUpdate, String matchId) {
		
		ConfigManager configManager = ConfigManager.getInstance();
		if (!configManager.isScorePush()) {
			return;
		}
		if (!ScoreApplication.isBackground()) {
			Log.i(TAG, "Application is not in background.");
			return;
		}
		if (scoreUpdate.updateType != ScoreUpdate.UPDATE_TYPE_SCORE) {
			return;
		}
		
		// add by Benson, only match is followed can fire notification
		if (!FollowMatchService.getFollowMatchManager().isMatchFollowed(matchId)){
			return;
		}
			
		addNotificaction(scoreUpdate);		
	}
	
	private void addNotificaction(ScoreUpdate scoreUpdate) {  
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon, "球探体育比分新消息", System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		ConfigManager configManager = ConfigManager.getInstance();
		if (configManager.isVibrate()) {
			// add the vibrate
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
		}
		if (configManager.isSound()) {
			// add the sound 
			MediaPlayer mMediaPlayer = new MediaPlayer(); 
			mMediaPlayer = MediaPlayer.create(this, R.raw.goals_sound);   
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
			mMediaPlayer.setLooping(false); 
			mMediaPlayer.start(); 
		}
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 设置notification的布局
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_update_item);
//		contentView.setImageViewResource(R.id.image, R.drawable.icon);
		contentView.setTextViewText(R.id.su_league_name, scoreUpdate.leagueName);
		contentView.setTextViewText(R.id.su_hometeam_name, scoreUpdate.homeTeamName);
		contentView.setTextViewText(R.id.su_awayteam_name, scoreUpdate.awayTeamName);
		contentView.setTextViewText(R.id.su_match_minutes, scoreUpdate.minutes);
		contentView.setTextViewText(R.id.su_home_score, scoreUpdate.homeTeamScore);
		contentView.setTextViewText(R.id.su_away_score, scoreUpdate.awayTeamScore);
			
		
		notification.contentView = contentView;
		notification.contentIntent = pendingIntent;
		notificationManager.notify(messageNotificationID, notification);
		//每次通知完，通知ID递增一下，避免消息覆盖掉
        messageNotificationID++;
		
	}
	
}
