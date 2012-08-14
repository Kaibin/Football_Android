package com.orange.score.android.service;

import java.util.ArrayList;
import java.util.List;

import com.orange.score.app.ScoreApplication;
import com.orange.score.model.match.FollowMatchManager;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchManager;
import com.orange.score.network.ScoreNetworkRequest;
import com.orange.score.service.LiveUpdateChangeCallBack;
import com.orange.score.service.MatchService;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class FollowMatchService extends Service implements LiveUpdateChangeCallBack {

	private static final String TAG = FollowMatchService.class.getName();
	private static final int FOLLOW_MATCH_READY = 1;
	private static final int FOLLOW_MATCH_UPDATE = 2;
	public static final String FOLLOW_MATCH_BROADCAST = "FOLLOW_MATCH_BROADCAST";
	public static final String FOLLOW_MATCH_UPDATE_TYPE = "FOLLOW_MATCH_UPDATE_TYPE";

	static FollowMatchManager followMatchManager = new FollowMatchManager();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate(){
		
		super.onCreate();
		Log.d(TAG, this.toString() + " <onCreate>");			
		followMatchManager.init(getApplicationContext());
		
		ScoreApplication app = (ScoreApplication)getApplication();
		MatchService matchService = app.getRealtimeMatchService();
		matchService.addScoreLiveUpdateObserver(this);

	}
	
	public static FollowMatchManager getFollowMatchManager(){
		return followMatchManager;
	}
	
	@Override
	public void onStart(Intent intent, int startId){				
		super.onStart(intent, startId);
		Log.d(TAG, this.toString() + " <onStart>");				

		updateAllFollowMatch();
		 
		// post a message to realtiime match activity so that it can show the follow match count here
		notifyUpdate(FOLLOW_MATCH_READY);
	}

	private void notifyUpdate(int id) {
		Intent intent = new Intent(FOLLOW_MATCH_BROADCAST);
		intent.putExtra(FOLLOW_MATCH_UPDATE_TYPE, id);
		sendBroadcast(intent);
	}

	@Override
	public void onDestroy(){		
		
		ScoreApplication app = (ScoreApplication)getApplication();
		MatchService matchService = app.getRealtimeMatchService();
		matchService.removeScoreLiveUpdateObserver(this);
		
		followMatchManager.close();
		super.onDestroy();
		Log.d(TAG, this.toString() + " <Destroy>");
	}

	Handler updateFollowMatchManagerHandler = new Handler();
	
	public void updateAllFollowMatch(){
		List<Match> matchList = followMatchManager.getFollowMatchList();
		if (matchList == null){
			return;
		}
		
		List<Match> matchListForUpdate = new ArrayList<Match>();
		matchListForUpdate.addAll(matchList);
		
		ScoreApplication app = (ScoreApplication)getApplication();
		MatchService matchService = app.getRealtimeMatchService();
		MatchManager matchManager = matchService.getMatchManager();
		
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
		
		UpdateFollowMatchThread runnable = new UpdateFollowMatchThread(matchListForUpdate);
		updateFollowMatchManagerHandler.post(runnable);
	}
	
	public class UpdateFollowMatchThread implements Runnable{
		
		private static final int UPDATE_FOLLOW_MATCH = 1;
		List<Match> matchList;

		public UpdateFollowMatchThread(List<Match> matchList) {
			this.matchList = matchList;
		}

		@Override
		public void run() {
			if (matchList == null){
				return;
			}
			 
			int count = matchList.size();
			
			for (int i=0; i<count; i++){
				Match match = matchList.get(i);
				UpdateFollowMatchTask task = new UpdateFollowMatchTask();
				task.execute(match);				
			}			
		}
		
	}

	
	private class UpdateFollowMatchTask extends AsyncTask<Match, Void, Match>{
		

		@Override
		protected Match doInBackground(Match... params) {
			if (params != null && params.length > 0){
				Match match = params[0];
				String[] updateData = ScoreNetworkRequest.getMatchDetail(match.matchId);
				match.updateMatchDetail(updateData);
				return match;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Match match){
			if (match == null)
				return;
						
			followMatchManager.updateMatch(match);
			
			// notify realtime match activity to update
			notifyUpdate(FOLLOW_MATCH_UPDATE);
		}

	}

	@Override
	public void notifyScoreLiveUpdate(List<String[]> list) {
		boolean hasChange = false;
		int size = list.size();
		for (int i=0; i<size; i++){
			String[] scoreUpdate = list.get(i);
			if (scoreUpdate == null || 
					scoreUpdate.length < ScoreNetworkRequest.REALTIME_SCORE_FILED_COUNT){
				continue;
			}			
			
			String matchId = scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_MATCHID];
			Match match = followMatchManager.findMatchById(matchId);
			if (match != null){
				Log.d(TAG, "notifyScoreLiveUpdate for follow list, match " + 
						match.toString() + " has live change");
				match.updateDataByLiveChange(scoreUpdate);
				followMatchManager.createOrUpdateMatch(match);
				
				hasChange = true;
			}			
			else{
//				Log.d(TAG, "notifyScoreLiveUpdate, match has live change but not found");				
			}
		}			
		
		if (hasChange){
			notifyUpdate(FOLLOW_MATCH_UPDATE);
		}
	}
	
	

}
