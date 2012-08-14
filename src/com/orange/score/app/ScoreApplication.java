package com.orange.score.app;

import android.app.Activity;
import android.app.Application;

import com.orange.score.activity.entry.MainActivity;
import com.orange.score.activity.entry.RealtimeMatchActivity;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.match.FollowMatchManager;
import com.orange.score.model.repository.RepositoryManager;
import com.orange.score.service.IndexService;
import com.orange.score.service.MatchService;
import com.orange.score.service.RepositoryService;

public class ScoreApplication extends Application {

	public static Activity currentActivity = null;

	public static MatchService realtimeMatchService = new MatchService();
	public static RepositoryService repositoryService = new RepositoryService();
	public static IndexService indexService = new IndexService();
	public static MainActivity mainActivity = null;
	public static RealtimeMatchActivity realtimeMatchActivity = null;
	public static boolean alive = false;
	public static boolean isBackground = false;

	public static boolean isBackground() {
		return isBackground;
	}

	public static void setBackground(boolean isBackground) {
		ScoreApplication.isBackground = isBackground;
	}

	public static FollowMatchManager followMatchManager = null;
	
	public MatchService getRealtimeMatchService() {
		return realtimeMatchService;
	}

	public void setRealtimeMatchService(MatchService realtimeMatchService) {
		this.realtimeMatchService = realtimeMatchService;
	}
	
	public LeagueManager getLeagueManager(int from) {
		if (from == LeagueManager.LEAGUE_FROM_REALTIME_MATCH) {
			return getRealtimeMatchService().getLeagueManager();
		} else if (from == LeagueManager.LEAGUE_FROM_REALTIME_INDEX) {
			return getIndexService().getLeagueManager();
		} else {
			return null;
		}
	}
	
	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}
	
	public RepositoryManager getRepositoryManager() {
		return repositoryService.getRepositoryManager();
	}

	public IndexService getIndexService() {
		return indexService;
	}
	
	public static void setMainActivity(MainActivity activity) {
		mainActivity = activity;
	}
	
	public static MainActivity getMainActivity(){
		return mainActivity;
	}
	
	public static void gotoRealtimeMatchTab(boolean forceRefreshWhenResume){
		if (mainActivity != null){
			if (realtimeMatchActivity != null){
				realtimeMatchActivity.forceRefreshWhenResume = forceRefreshWhenResume;
			}
			mainActivity.selectTab(MainActivity.REALTIME_MATCH_TAG);
		}
	}

	public static void setRealtimeMatchActivity(RealtimeMatchActivity activity) {
		realtimeMatchActivity = activity;
	}
	
	public static void reloadRealtimMatch(){
		if (realtimeMatchActivity != null){
			realtimeMatchActivity.refreshCurrentMatchList();
		}
	}

	public static void setAlive() {
		alive = true;
	}
	
	public static boolean isAlive(){
		return alive;
	}

	public RealtimeMatchActivity getRealtimeMatchActivity() {
		return realtimeMatchActivity;
	}

	static boolean detectAppUpdate = false;
	
	public static boolean hasDetectUpdate() {
		return detectAppUpdate;
	}
	
	public static void setDetectUpdate(){
		detectAppUpdate = true;
	}

//	public ConfigManager getConfigManager() {
//		return configManager;
//	}
//
//	public void setConfigManager(ConfigManager configManager) {
//		this.configManager = configManager;
//	}
	
	
}
