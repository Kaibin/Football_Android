package com.orange.score.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;

import com.orange.common.android.activity.PPActivity;
import com.orange.common.android.widget.RealtimeScorePopupWindow;
import com.orange.common.util.DateUtil;
import com.orange.score.model.config.ConfigManager;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.match.FollowMatchManager;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchManager;
import com.orange.score.network.ResultCodeType;
import com.orange.score.network.ScoreNetworkRequest;

public class MatchService {
	
	private static final String TAG = "MatchService";		

	MatchManager matchManager = new MatchManager();
	LeagueManager leagueManager = new LeagueManager();
	FollowMatchManager followMatchManager = null;
	
	Timer liveScoreUpdateTimer = null;
	LiveUpdateMatchTask liveUpdateMatchTask = null;
	List<LiveUpdateChangeCallBack> liveUpdateChangeObserverList = new ArrayList<LiveUpdateChangeCallBack>();
	
	
	
	public MatchService(){
		addScoreLiveUpdateObserver(matchManager);
	}
	
	public void setFollowMatchManager(FollowMatchManager manager){
		this.followMatchManager = manager;
	}
	
	public void loadAllMatch(PPActivity activity, MatchServiceCallBack callback){
		LoadAllMatchTask task = new LoadAllMatchTask(activity, callback);
		task.execute("");
	}
	
	public void loadMatchDetail(String matchId, MatchServiceCallBack callBack) {
		LoadMatchDetailTask task = new LoadMatchDetailTask(matchId, callBack);
		task.execute("");
	}
	
	private class LoadMatchDetailTask extends AsyncTask<String, Void, String[]>{

		MatchServiceCallBack callback;
		String matchId;
		
		public LoadMatchDetailTask(String matchId, MatchServiceCallBack callBack) {
			this.matchId = matchId;
			this.callback = callBack;
		}
		
		@Override
		protected String[] doInBackground(String... msg) {
			return ScoreNetworkRequest.getMatchDetail(matchId);
		}
		
		@Override
		protected void onPostExecute(String[] result){
			
			
			
			if (result == null){
				Log.w(TAG, "get match detail from network null");
				return;
			}
			
			if (result.length < ScoreNetworkRequest.INDEX_REALTIME_MATCH_FIELD_MIN_COUNT){
				Log.w(TAG, "get match detail data but length not enough, length = " + result.length);
				return;
			}
			
			matchManager.updateMatchDetail(matchId, result);	
			
			// invoke callback method (notify UI to refresh)
			callback.loadMatchDetailFinish(ResultCodeType.SUCCESS);	
		}
		
	}
	
	private class LoadAllMatchTask extends AsyncTask<String, Void, String[]>{

		PPActivity activity;
		MatchServiceCallBack callback;
    	
		public LoadAllMatchTask(PPActivity activity, MatchServiceCallBack callback) {
			this.activity = activity;
			this.callback = callback;
			// this.backgroundLoading = backgroundLoading;
		}

		@Override
		protected void onPreExecute(){
			
			activity.showProgressDialog("", "加载数据中", this);
		}
		
		@Override
		protected void onPostExecute(String[] result){
			activity.hideDialog();
			
			Log.d(TAG, "<MatchService> enter onPostExecute");
			if (isCancelled()) {
				Log.d(TAG, "<MatchService> onPostExecut is cancel");
				return;
			} 
			
			if (result == null){
				callback.loadAllMatchFinish(ResultCodeType.ERROR_NO_DATA);				
				return;
			}

			boolean hasMatchData = true;
			if (result.length == ScoreNetworkRequest.SEGMENT_REALTIME_SERVER_TIME + 1){
				Log.w(TAG, "no match/league data recv");
				hasMatchData = false;
			}  

			String matchListString = "";
			String leagueListString = "";
			String serverDateString = "";
			if (hasMatchData && result.length >= ScoreNetworkRequest.SEGMENT_REALTIME_MATCH_NUM){
				leagueListString = result[ScoreNetworkRequest.SEGMENT_REALTIME_LEAGUE];
				matchListString = result[ScoreNetworkRequest.SEGMENT_REALTIME_MATCH];
				serverDateString = result[ScoreNetworkRequest.SEGMENT_REALTIME_SERVER_TIME];
			}

			// check if there is any league data before
			boolean hasLeagueDataBefore = leagueManager.hasLeagueData();
			
			// parse league data
			String[] leagueList = leagueListString.split(ScoreNetworkRequest.SEP_RECORD);
			leagueManager.updateDataFromStringList(leagueList, LeagueManager.LEAGUE_FROM_REALTIME_MATCH);			
			
			// parse match data here								
			String[] matchList = matchListString.split(ScoreNetworkRequest.SEP_RECORD);
			matchManager.updateDataFromStringList(matchList, leagueManager, followMatchManager);
			if (!hasLeagueDataBefore){
				matchManager.selectAllLeagues(leagueManager.findAllLeagues());
			}
			
			// update server date
			ConfigManager.getInstance().setServerDifferenceTime(serverDateString);
			
			// invoke callback method (notify UI to refresh
			callback.loadAllMatchFinish(ResultCodeType.SUCCESS);

			// start live score update timer
			startLiveScoreUpdateTimer();
		}
		
		@Override
		protected String[] doInBackground(String... msg) {			
			return ScoreNetworkRequest.getAllMatch(
					ConfigManager.getInstance().getLanguage(),
					matchManager.getFilterScoreType());
		}		
		
		@Override
	    protected void onCancelled() {
			Log.d(TAG, "<LoadAllMatchTask>cancel task.");
	    }
		
	}
	
	private class LiveUpdateMatchTask extends AsyncTask<String, Void, List<String[]>>{

		public LiveUpdateMatchTask() {
		}

		@Override
		protected void onPreExecute(){
		}
		
		@Override
		protected void onPostExecute(List<String[]> list){
			
			Log.d(TAG, "LiveUpdateMatchTask onPostExecute");
			if (list != null && list.size() > 0){
				// has update, notify all observers
				int count = liveUpdateChangeObserverList.size();
				for (int i=0; i<count; i++){
					LiveUpdateChangeCallBack observer = liveUpdateChangeObserverList.get(i);
					observer.notifyScoreLiveUpdate(list);
				}
			}
			
			// schedule next timer
			startLiveScoreUpdateTimer();
		}
		
		@Override
		protected List<String[]> doInBackground(String... msg) {			
			String[] matchUpdateList = ScoreNetworkRequest.getMatchLiveChange();
			if (matchUpdateList == null){
				return null;
			}
			
			// parse data into a list
			List<String[]> list = new ArrayList<String[]>();
			for (int i=0; i<matchUpdateList.length; i++){
				if (matchUpdateList[i].length() == 0){
					continue;
				}
				
				// parse fields
				String[] fields = matchUpdateList[i].split(ScoreNetworkRequest.SEP_FIELD);
				if (fields == null || fields.length < ScoreNetworkRequest.REALTIME_SCORE_FILED_COUNT){
					Log.w(TAG, "LiveUpdateMatch but fields count not enough, count = " + fields.length);
					continue;
				}

				list.add(fields);				
			}

			return list;
		}					
	}

	public MatchManager getMatchManager() {
		return matchManager;
	}		
	
	private void liveScoreUpate(){
		liveUpdateMatchTask = new LiveUpdateMatchTask();
		liveUpdateMatchTask.execute("");
	}
	
	public void startLiveScoreUpdateTimer(){
		startLiveScoreUpdateTimer(false);
	}
	
	public void startLiveScoreUpdateTimer(boolean startNow){
		if (liveScoreUpdateTimer != null){
			// cancel existing timer if any to avoid conflict
			liveScoreUpdateTimer.cancel();
			liveScoreUpdateTimer = null;
		}
		
		int interval = 10; // 10 seconds by default
		try{
			if (startNow){
				interval = 0;
			}
			else{
				interval = Integer.parseInt(ConfigManager.getInstance().getRefreshInterval());
			}
		}
		catch (Exception e){			
		}		
		
		liveScoreUpdateTimer = new Timer();
		liveScoreUpdateTimer.schedule(new TimerTask() {
           public void run() {
        	   Log.d(TAG, "Fire live score update timer");
        	   liveScoreUpate();
           }
        }, interval*1000);			
	}
	
	private void stopLiveScoreUpdateTimer(){
		if (liveScoreUpdateTimer != null){
			liveScoreUpdateTimer.cancel();
			liveScoreUpdateTimer = null;
		}
	}
	
	public void addScoreLiveUpdateObserverAsFirst(LiveUpdateChangeCallBack observer){
		this.liveUpdateChangeObserverList.add(0, observer);
	}

	public void addScoreLiveUpdateObserver(LiveUpdateChangeCallBack observer){
		this.liveUpdateChangeObserverList.add(observer);
	}
	
	public void removeScoreLiveUpdateObserver(LiveUpdateChangeCallBack observer){
		this.liveUpdateChangeObserverList.remove(observer);
	}

	public LeagueManager getLeagueManager() {
		return this.leagueManager;
	}
	
	
	/* useless code, to be deleted
	public void updateAllFollowMatch(Handler handler){
		List<Match> matchList = followMatchManager.getFollowMatchList();
		if (matchList == null){
			return;
		}
		
		List<Match> matchListForUpdate = new ArrayList<Match>();
		matchListForUpdate.addAll(matchList);
		
		int count = matchList.size();
		for (int i=0; i<count; i++){
			Match match = matchList.get(i);
			if (match.isFinish()){
				// already finish, no need to update
				matchListForUpdate.remove(match);
				Log.d(TAG, "skip follow match for update, it's done, match id = " + match.matchId);				
			}
			else{
				Match newMatch = matchManager.findMatchById(match.matchId);			
				if (newMatch != null){
					followMatchManager.updateMatch(newMatch);
					matchListForUpdate.remove(match);	// this match will update by live change
					Log.d(TAG, "skip follow match for update, it's in latest list, match id = " + match.matchId);				
				}
			}
		}
		
		if (handler == null){
			Log.w(TAG, "updateAllFollowMatch but handler is null!");
			return;
		}
		
		UpdateFollowMatchThread runnable = new UpdateFollowMatchThread(matchListForUpdate, handler);
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	public class UpdateFollowMatchThread implements Runnable{
		
		private static final int UPDATE_FOLLOW_MATCH = 1;
		List<Match> matchList;
		Handler		  handler;

		public UpdateFollowMatchThread(List<Match> matchList, Handler handler) {
			this.matchList = matchList;
			this.handler = handler;
		}

		@Override
		public void run() {
			if (matchList == null){
				return;
			}
			 
			int count = matchList.size();
			for (int i=0; i<count; i++){
				Match match = matchList.get(i);
				// send request
				String[] data = ScoreNetworkRequest.getMatchDetail(match.matchId);
				match.updateMatchDetail(data);
				followMatchManager.updateMatch(match);
			}
			
//			if (count > 0){
				Message message = new Message();
				message.what = UPDATE_FOLLOW_MATCH;
				handler.sendMessage(message);
//			}
		}
		
	}
	*/
	
	/*
	Timer matchUpdateTimer = new Timer(); // timer to reload all match in the morning
	public void scheduleMatchUpdateTimer(boolean fromToday){
				
		Date date = getMatchUpdateDate(fromToday);
		Log.d(TAG, "schedule reload all match data timer at " + date.toGMTString());

		matchUpdateTimer.schedule(new TimerTask(){

			@Override
			public void run() {
				
				Log.d(TAG, "fire reload all match data timer");
				
				scheduleMatchUpdateTimer(false);
			}
			
		},  date);
	}

	private Date getMatchUpdateDate(boolean fromToday) {
		int SCHEDULE_HOUR = 11;
		int SCHEDULE_MINUTE = 01;
		if (fromToday){
			Date date = DateUtil.getChineseDateOfTodayTime(SCHEDULE_HOUR, SCHEDULE_MINUTE, 0);
			Date now = new Date();
			if (date.before(now)){
				date = DateUtil.getChineseDateOfTomorrowTime(SCHEDULE_HOUR, SCHEDULE_MINUTE, 0);
			}
			return date;
		}
		else{
			return DateUtil.getChineseDateOfTomorrowTime(SCHEDULE_HOUR, SCHEDULE_MINUTE, 0);
		}
	}
	
	
	private Date lastLoadMatchDate = null;
	public void saveLoadMatchTime(){
		lastLoadMatchDate = new Date();
	}
	
	public boolean needLoadAllMatch(){
		int SCHEDULE_HOUR = 11;
		int SCHEDULE_MINUTE = 00;

		Date date = DateUtil.getChineseDateOfTodayTime(SCHEDULE_HOUR, SCHEDULE_MINUTE, 0);
		if (lastLoadMatchDate.before(date)){
			return true;
		}
		else{
			return false;
		}			
	}
	*/
}
