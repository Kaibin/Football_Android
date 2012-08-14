package com.orange.score.service;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.orange.common.android.activity.PPActivity;
import com.orange.score.model.config.ConfigManager;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.match.FollowMatchManager;
import com.orange.score.model.match.MatchManager;
import com.orange.score.network.ResultCodeType;
import com.orange.score.network.ScoreNetworkRequest;

public class MatchHistoryService {
	private static final String TAG = "MatchHistoryService";
	private MatchManager matchManager = new MatchManager();
	private LeagueManager leagueManager = new LeagueManager();
//	private FollowMatchManager followMatchManager = FollowMatchManager.getInstance();
	
	
	
	public void loadFinalScoreMatch(PPActivity activity, MatchHistoryServiceCallBack callBack,
			String date) {
		LoadFinalScoreMatchTask task = new LoadFinalScoreMatchTask(activity, callBack, date);
		task.execute("");
	}
	
	private class LoadFinalScoreMatchTask extends AsyncTask<String, Void, String[]> {

		PPActivity activity;
		MatchHistoryServiceCallBack callBack;
		String date;
		
		public LoadFinalScoreMatchTask(PPActivity activity, MatchHistoryServiceCallBack callBack,
				String date) {
			this.activity = activity;
			this.date = date;
			this.callBack = callBack;
		}
		
		@Override
		protected void onPreExecute(){
			activity.showProgressDialog("", "数据加载中...", this);
		}
		
		@Override
		protected String[] doInBackground(String... msg) {
			return ScoreNetworkRequest.getFinishMatch(
					ConfigManager.getInstance().getLanguage(), date);
		}
		
		@Override
		protected void onPostExecute(String[] result) {	
			activity.hideDialog();

			if (result == null) {
				callBack.loadAllMatchFinish(ResultCodeType.ERROR_NO_DATA);
				Log.w(TAG, "getFinalScoreMatch failed");
//				Toast.makeText(activity.getApplicationContext(), "网络错误，请检查网络是否连接",
//						Toast.LENGTH_SHORT).show();
				return;
			} else {
				Log.i(TAG, "results"+result.toString());
				boolean hasMatchData = true;
				if (result.length == ScoreNetworkRequest.SEGMENT_REALTIME_SERVER_TIME + 1){
					Log.w(TAG, "no match/league data recv");
					hasMatchData = false;	
				}
				String matchListString = "";
				String leagueListString = "";
				if (hasMatchData && result.length >= ScoreNetworkRequest.SEGMENT_REALTIME_MATCH_NUM){
					leagueListString = result[ScoreNetworkRequest.SEGMENT_REALTIME_LEAGUE];
					matchListString = result[ScoreNetworkRequest.SEGMENT_REALTIME_MATCH];
				}

				// parse league data
				String[] leagueList = leagueListString.split(ScoreNetworkRequest.SEP_RECORD);
				leagueManager.updateDataFromStringList(leagueList, LeagueManager.LEAGUE_FROM_REALTIME_MATCH);			
				
				// parse match data here								
				String[] matchList = matchListString.split(ScoreNetworkRequest.SEP_RECORD);
				matchManager.updateDataFromStringList(matchList, leagueManager, null);
				Log.i(TAG, matchManager.getMatchList().toString());
				
				callBack.loadAllMatchFinish(ResultCodeType.SUCCESS);	
				
			}	
		}
		
	}
	
	
	public LeagueManager getLeagueManager() {
		return this.leagueManager;
	}
	
	public MatchManager getMatchManager() {
		return this.matchManager;
	}
	
}
