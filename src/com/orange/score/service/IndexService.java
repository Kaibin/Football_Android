package com.orange.score.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.util.Log;

import com.orange.common.android.activity.PPActivity;
import com.orange.score.model.config.ConfigManager;
import com.orange.score.model.index.CompanyManager;
import com.orange.score.model.index.Odds;
import com.orange.score.model.index.OddsManager;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.match.MatchManager;
import com.orange.score.network.RepositoryNetworkRequest;
import com.orange.score.network.ResultCodeType;
import com.orange.score.network.ScoreNetworkRequest;
import com.orange.score.utils.FileService;

public class IndexService {
	public static final String TAG = "IndexCompanyService";
	public static final int DEFAULT_INTERVAL = 10;
	private OddsManager oddsManager = new OddsManager();
	private CompanyManager companyManager = new CompanyManager();
	private LeagueManager leagueManager = new LeagueManager();
	private MatchManager matchManager = new MatchManager();
	
	LiveUpdateOddsTask liveUpdateOddsTask = null;
	Timer liveOddsUpdateTimer = null;

	List<OddsUpdateChangeCallBack> oddsUpdateChangeObserverList = new ArrayList<OddsUpdateChangeCallBack>();

	public void loadCompanyData(PPActivity activity, IndexServiceCallBack callback) {
		LoadIndexCompanyTask task = new LoadIndexCompanyTask(activity, callback);
		task.execute("");
	}
	
	public void loadOddsData(PPActivity activity, IndexServiceCallBack callBack, String date, String companyID) {
		LoadOddsTask task = new LoadOddsTask(activity, callBack);
		task.execute(date, companyID);
	}
	
	private void liveOddsUpate(){
		liveUpdateOddsTask = new LiveUpdateOddsTask();
		liveUpdateOddsTask.execute("");
	}
	
	private class LiveUpdateOddsTask extends AsyncTask<String, Void, List<String[]>> {

		@Override
		protected List<String[]> doInBackground(String... params) {
			String[] stringList = ScoreNetworkRequest.getOddsLiveChange(companyManager.getOddsType());
			if (stringList == null) {
				return null;
			}
			
			// parse data into a list
			List<String[]> list = new ArrayList<String[]>();
			for (int i=0; i<stringList.length; i++){
				if (stringList[i].length() == 0){
					continue;
				}
				
				// parse fields
				String[] fields = stringList[i].split(ScoreNetworkRequest.SEP_FIELD);
				if (fields == null || fields.length < ScoreNetworkRequest.INDEX_REALTIME_ODDS_CHANGE_FIELD_NUM){
					Log.w(TAG, "LiveUpdateOdds but fields count not enough, count = " + fields.length);
					continue;
				}

				list.add(fields);				
			}

			return list;
		}
		
		@Override
		protected void onPostExecute(List<String[]> list) {
			super.onPostExecute(list);
			Set<Odds> set = oddsManager.getOddsUpdateSet(list, companyManager.getOddsType());
			Log.d(TAG, "LiveUpdateOddsTask onPostExecute");
			if (list != null && list.size() > 0){
				// has update, notify all observers
				int count = oddsUpdateChangeObserverList.size();
				for (int i=0; i<count; i++){
					OddsUpdateChangeCallBack observer = oddsUpdateChangeObserverList.get(i);
					observer.notifyOddsUpdate(set);
				}
			}
			
			// schedule next timer
			startLiveOddsUpdateTimer(false);
		}

	}
	
	private void startLiveOddsUpdateTimer(boolean startNow) {
		if (liveOddsUpdateTimer != null){
			// cancel existing timer if any to avoid conflict
			liveOddsUpdateTimer.cancel();
			liveOddsUpdateTimer = null;
		}
		
		int interval = DEFAULT_INTERVAL; // 10 seconds by default
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
		
		liveOddsUpdateTimer = new Timer();
		liveOddsUpdateTimer.schedule(new TimerTask() {
           public void run() {
        	   Log.d(TAG, "Fire live odds update timer");
        	   liveOddsUpate();
           }
        }, interval*1000);					
	}
	
	private void stopLiveOddsUpdateTimer() {
		if (liveOddsUpdateTimer != null) {
			liveOddsUpdateTimer.cancel();
			liveOddsUpdateTimer = null;
		}
	}
	
	public void addOddsLiveUpdateObserverAsFirst(OddsUpdateChangeCallBack observer){
		this.oddsUpdateChangeObserverList.add(0, observer);
	}

	public void addOddsLiveUpdateObserver(OddsUpdateChangeCallBack observer){
		this.oddsUpdateChangeObserverList.add(observer);
	}
	
	public void removeOddsLiveUpdateObserver(OddsUpdateChangeCallBack observer){
		this.oddsUpdateChangeObserverList.remove(observer);
	}

	
	private class LoadIndexCompanyTask extends AsyncTask<String, Void, String[]> {

		IndexServiceCallBack callback;
		PPActivity activity;
		
		public LoadIndexCompanyTask(PPActivity activity, IndexServiceCallBack callback) {
			super();
			this.callback = callback;
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			activity.showProgressDialog("", "加载数据中", this);
		}
		
		@Override
		protected String[] doInBackground(String... params) {
			FileService fileService = new FileService(activity);
			String fileName = "odds_company.txt";
			String[] result;
			try {
				if (true) {
					String companyString = fileService.read(fileName);
					Log.d(TAG, "Get odds company from odds_company.txt");
					result = companyString.split(RepositoryNetworkRequest.SEP_RECORD);
					return result;
				}  else {
					//抛出异常，由下面捕获
					throw new Exception();
				}
			} catch (Exception e) {
				Log.d(TAG, "Get odds company from network");
				result = ScoreNetworkRequest.getIndexCompany();
				
				if (result == null) {
					return null;
				}
					
				StringBuilder content = new StringBuilder();
				for (int i = 0; i < result.length; i++) {
					content.append("!").append(result[i]);
				}
				fileService.save(fileName, content.toString());
			}
			return result;
			
		}					
		
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			activity.hideDialog();
			
			if (result != null && result.length > 0){
				companyManager.updateCompanyDataFromStringList(result);
				callback.loadAllCompanyFinish(ResultCodeType.SUCCESS);
			}
		}
	}
	
	private class LoadOddsTask extends AsyncTask<String, Void, String[]> {
		PPActivity activity;
		IndexServiceCallBack callBack;
		
		public LoadOddsTask(PPActivity activity, IndexServiceCallBack callBack) {
			super();
			this.activity = activity;
			this.callBack = callBack;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			activity.showProgressDialog("", "加载数据中", this);
		}
		
		@Override
		protected String[] doInBackground(String... params) {
			return ScoreNetworkRequest.getOddsData(params[0], params[1], 
					ConfigManager.getLanguage(), oddsManager.getFilterScoreType(), companyManager.getOddsType());
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			activity.hideDialog();
			
			if (result == null) {
				Log.w(TAG, "load odds data return null");
				return;
			}

			if (result.length < ScoreNetworkRequest.SEGMENT_REALINDEX_ODDS_COUNT) {
				Log.w(TAG, "get Odds data but length not enough, length = " + result.length);
				return;
			}
			
			String leagueListString = "";
			String matchListString = "";
			String oddsListString = "";

			if (result.length >= ScoreNetworkRequest.SEGMENT_REALINDEX_ODDS_COUNT) {
				leagueListString = result[ScoreNetworkRequest.SEGMENT_REALINDEX_LEAGUE];
				matchListString = result[ScoreNetworkRequest.SEGMENT_REALINDEX_MATCH];
				oddsListString = result[ScoreNetworkRequest.SEGMENT_REALINDEX_ODDS];
			}

			// parse league data
			String[] leagueList = leagueListString.split(ScoreNetworkRequest.SEP_RECORD);
//			indexManager.updateOddsLeagueDataFromStringList(leagueList);
			leagueManager.updateDataFromStringList(leagueList, LeagueManager.LEAGUE_FROM_REALTIME_INDEX);
			
			// parse match data
			String[] matchList = matchListString.split(ScoreNetworkRequest.SEP_RECORD);
			oddsManager.updateOddsMatchDataFromStringList(matchList, leagueManager);
			
			// parse odds data
			String[] oddsList = oddsListString.split(ScoreNetworkRequest.SEP_RECORD);
			oddsManager.updateOddsPeilvDataFromStringList(oddsList, companyManager.getOddsType());

			callBack.loadOddsDataFinish(ResultCodeType.SUCCESS);
			
			// start live odds update timer
			startLiveOddsUpdateTimer(false);
		}
		
	}
	
	public MatchManager getMatchManager() {
		return matchManager;
	}

	public OddsManager getOddsManager() {
		return oddsManager;
	}
	
	public LeagueManager getLeagueManager() {
		return leagueManager;
	}

	public CompanyManager getCompanyManager() {
		return companyManager;
	}

}
