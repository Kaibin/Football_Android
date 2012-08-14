package com.orange.score.service;

import android.os.AsyncTask;
import android.util.Log;

import com.orange.common.android.activity.PPActivity;
import com.orange.score.constants.LanguageType;
import com.orange.score.model.config.ConfigManager;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.repository.RepositoryManager;
import com.orange.score.network.RepositoryNetworkRequest;
import com.orange.score.network.ResultCodeType;
import com.orange.score.utils.FileService;

public class RepositoryService {
	private static final String TAG = RepositoryService.class.getName();
	private RepositoryManager repositoryManager = new RepositoryManager();
	private LeagueManager leagueManager = new LeagueManager();
	
	public void loadRepository(PPActivity activity, RepositoryServiceCallBack callback) {
		LoadRepositoryTask task = new LoadRepositoryTask(activity, callback);
		task.execute("");
	}

	public void loadGroupInfo(PPActivity activity, RepositoryServiceCallBack callBack, String leagueId, String season) {
		LoadGroupInfoTask task = new LoadGroupInfoTask(callBack);
		task.execute(leagueId, season);
	}
	
	public void loadRoundInfo(PPActivity activity, RepositoryServiceCallBack callBack, String leagueId, String season) {
		LoadRoundInfoTask task = new LoadRoundInfoTask(activity, callBack);
		task.execute(leagueId, season);
	}

	private class LoadRepositoryTask extends AsyncTask<String, Void, String[]> {

		RepositoryServiceCallBack callback;
		PPActivity activity;

		public LoadRepositoryTask(PPActivity activity, RepositoryServiceCallBack callBack) {
			this.callback = callBack;
			this.activity = activity;
		}
		
		@Override
		protected void onPreExecute(){
			activity.showProgressDialog("", "加载数据中");
		}

		@Override
		protected String[] doInBackground(String... msg) {
			String[] result = new String[3];
			String currentDateString = FileService.currentDate();
			FileService fileService = new FileService(activity);
			String fileName = "repository.txt";
			try {
				String repositoryString = fileService.read(fileName);
				Log.d(TAG, "Get repository info from repository.txt");
				String[] repositoryArray = repositoryString.split(RepositoryNetworkRequest.SEP_SEGMENT);
				int languege = Integer.parseInt(repositoryArray[0]);
				String oldDateString = repositoryArray[1];
				int currentDateInt = Integer.parseInt(currentDateString);
				int oldDateInt = Integer.parseInt(oldDateString);
				if (languege != ConfigManager.getLanguage().intValue()) {
					throw new Exception();
				}
				if (currentDateInt - oldDateInt < 7 * 24 ) {
					for (int i = 0; i < result.length; i++) {
						result[i] = repositoryArray[i + 2];
					}
					return result;
				} else {
					//抛出异常，由下面捕获
					throw new Exception();
				}

			} catch (Exception e) {
				Log.d(TAG, "Get repository info from network");
				result = RepositoryNetworkRequest.getAllRepository(ConfigManager.getLanguage());
				if (result == null){
					return null;
				}
				
				StringBuilder content = new StringBuilder();
				content.append(ConfigManager.getLanguage().intValue()).append("$$");
				content.append(currentDateString);
				for (int i = 0; i < result.length; i++) {
					content.append("$$").append(result[i]);
				}
				fileService.save(fileName, content.toString());
			}
			return result;
		}

		@Override
		protected void onPostExecute(String[] result) {
			
			activity.hideDialog();
			
			if (result == null) {
				Log.w(TAG, "get repository from network null");
				return;
			}

			if (result.length < RepositoryNetworkRequest.SEGMENT_REPOSITORY_NUM) {
				Log.w(TAG, "get repository data but length not enough, length = " + result.length);
				return;
			}

			String continentListString = "";
			String countryListString = "";
			String leagueListString = "";

			if (result.length >= RepositoryNetworkRequest.SEGMENT_REPOSITORY_NUM) {
				continentListString = result[RepositoryNetworkRequest.SEGMENT_REPOSITORY_CONTINENT];
				countryListString = result[RepositoryNetworkRequest.SEGMENT_REPOSITORY_COUNTRY];
				leagueListString = result[RepositoryNetworkRequest.SEGMENT_REPOSITORY_LEAGUE];
			}

			// parse continent data
			String[] continentList = continentListString.split(RepositoryNetworkRequest.SEP_RECORD);
			repositoryManager.updateContinentDataFromStringList(continentList);

			// parse country data
			String[] countryList = countryListString.split(RepositoryNetworkRequest.SEP_RECORD);
			repositoryManager.updateCountryDataFromStringList(countryList);

			// parse league data
			String[] leagueList = leagueListString.split(RepositoryNetworkRequest.SEP_RECORD);
//			repositoryManager.updateLeagueDataFromStringList(leagueList);
			leagueManager.updateDataFromStringList(leagueList, LeagueManager.LEAGUE_FROM_REPOSITORY);

			// invoke callback method (notify UI to refresh)
			callback.loadAllRepositoryFinish(ResultCodeType.SUCCESS);
		}

	}

	private class LoadGroupInfoTask extends AsyncTask<String, Void, String[]> {

		RepositoryServiceCallBack callback;

		public LoadGroupInfoTask(RepositoryServiceCallBack callBack) {
			this.callback = callBack;
		}

		@Override
		protected String[] doInBackground(String... params) {
			return RepositoryNetworkRequest.getRepositoryGroupInfo(params[0], params[1]);
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			
			if (result == null) {
				Log.w(TAG, "get groupInfo from network null");
				return;
			}

			if (result.length < RepositoryNetworkRequest.SEGMENT_REPOSITORY_CUP_SCHEDULE_COUNT) {
				Log.w(TAG, "get groupInfo data but length not enough, length = " + result.length);
				return;
			}

			String groupInfoListString = "";

			if (result.length >= RepositoryNetworkRequest.SEGMENT_REPOSITORY_CUP_SCHEDULE_COUNT) {
				groupInfoListString = result[RepositoryNetworkRequest.SEGMENT_REPOSITORY_CUP_SCHEDULE_GROUP_INFO];
			}

			// parse continent data
			String[] continentList = groupInfoListString.split(RepositoryNetworkRequest.SEP_RECORD);
			repositoryManager.updateGroupInfoFromStringList(continentList);
			
			callback.loadGroupInfoFinish(ResultCodeType.SUCCESS);
		}
		

	}
	private class LoadRoundInfoTask extends AsyncTask<String, Void, String[]> {

		RepositoryServiceCallBack callback;
		PPActivity activity;

		public LoadRoundInfoTask(PPActivity activity, RepositoryServiceCallBack callBack) {
			this.activity = activity;
			this.callback = callBack;
		}

		@Override
		protected String[] doInBackground(String... params) {
			return RepositoryNetworkRequest.getRepositoryRoundInfo(params[0], params[1]);
		}
		
		@Override
		protected void onPreExecute() {
//			activity.showProgressDialog("", "加载数据中");
		}
		@Override
		protected void onPostExecute(String[] result) {
			
//			activity.hideDialog();
			
			if (result == null) {
				Log.w(TAG, "get LoadRoundInfo from network null");
				return;
			}

			if (result.length < RepositoryNetworkRequest.SEGMENT_REPOSITORY_LEAGUE_SCHEDULE_COUNT) {
				Log.w(TAG, "get LoadRoundInfo data but length not enough, length = " + result.length);
				return;
			}

			String roundInfoListString = "";
			String totalRound = "";

			if (result.length >= RepositoryNetworkRequest.SEGMENT_REPOSITORY_LEAGUE_SCHEDULE_COUNT) {
				totalRound = result[RepositoryNetworkRequest.SEGMENT_REPOSITORY_LEAGUE_SCHEDULE_TOTAL_ROUND];
				roundInfoListString = result[RepositoryNetworkRequest.SEGMENT_REPOSITORY_LEAGUE_SCHEDULE_ROUND_DETAIL];
			}

			// parse round data
			String[] roundList = roundInfoListString.split(RepositoryNetworkRequest.SEP_RECORD);
			String currentRound = roundList[0].split(RepositoryNetworkRequest.SEP_FIELD)[0];
			leagueManager.updateLeagueRound(totalRound, currentRound);
			
			callback.loadRoundInfoFinish(ResultCodeType.SUCCESS);
		}
		

	}

	public RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	public LeagueManager getLeagueManager() {
		return leagueManager;
	}
}
