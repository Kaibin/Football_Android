package com.orange.score.model.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.orange.score.app.ScoreApplication;
import com.orange.score.network.ScoreNetworkRequest;
import com.orange.score.service.LiveUpdateChangeCallBack;
import com.orange.score.service.MatchService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

public class FollowMatchManager {

	private static final String TAG = "FollowMatchManager";
	ArrayList<Match> followMatchList = new ArrayList<Match>();
	HashMap<String, Match>  followMatchMap = new HashMap<String, Match>();
	FollowMatchDBAdapter followMatchDBAdapter;
	
//	// thread-safe singleton implementation
    private static FollowMatchManager manager = null; 
//    private FollowMatchManager(){		
//	} 	
//    
//    public static FollowMatchManager getInstance() {
//    	return manager; 
//    } 
//    

	private static FollowMatchManager getInstance() {
		return manager; 
	} 
	
	public void init(Context _context){
    	followMatchDBAdapter = new FollowMatchDBAdapter(_context);
    	followMatchDBAdapter.open();
    	load();
    }
    
    public void setFollowMatchDBAdapter(FollowMatchDBAdapter adapter){
    	this.followMatchDBAdapter = adapter;
    }
    
    public void followMatch(Match match){
    	match.setIsFollow(true);
//    	followMatchMap.put(match.getMatchId(), match);			// done by createOrUpdate now, benson
    	createOrUpdateMatch(match);
    }
    
    
	public void unfollowMatch(Match match){
    	match.setIsFollow(false);
//    	followMatchMap.remove(match.getMatchId());				// done by delete now, benson
    	deleteMatch(match);
    }
	
    
	public ArrayList<Match> getFollowMatchList(){
    	load();
    	
    	Collection<Match> list = followMatchMap.values();    	
    	ArrayList<Match> retList = new ArrayList<Match>(list);    	
    	Collections.sort(retList);
    	return retList;
    }
    
    public void updateMatch(Match match){
    	followMatchDBAdapter.updateEntry(match);
    	followMatchMap.put(match.matchId, match);
    }
        
    private void load(){
    	Cursor cursor = followMatchDBAdapter.getAllEntries();
    	if (cursor == null)
    		return;

    	cursor.moveToFirst();
    	while (!cursor.isAfterLast()){
    		
    		Match match = new Match();
    		match.fromCursor(cursor);
    		
    		int index = cursor.getColumnIndex(FollowMatchDBAdapter.COLUMN_MATCH_ID);
    		if (index != -1){
    			String matchId = cursor.getString(index);
    			match.setMatchId(matchId);    			
    		}
    		else{
    			Log.e(TAG, "load all follow matches, but cursor index = -1");
    		}
    		
//    		String homeTeamName = cursor.getString(cursor.getColumnIndex(FollowMatchDBAdapter.COLUMN_HOME_TEAM_NAME));
//    		match.setHomeTeamName(homeTeamName);
//
//    		String awayTeamName = cursor.getString(cursor.getColumnIndex(FollowMatchDBAdapter.COLUMN_AWAY_TEAM_NAME));
//    		match.setAwayTeamName(awayTeamName);

    		followMatchMap.put(match.getMatchId(), match);

    		cursor.moveToNext();

    	}
    	    	
    	cursor.close();
    }
    
	public void createOrUpdateMatch(Match match) {
		Cursor cursor = followMatchDBAdapter.findEntryByMatch(match);
		if (cursor == null || cursor.getCount() == 0){
			// create new
			followMatchDBAdapter.insertEntry(match);
		}
		else{
			// update new
			followMatchDBAdapter.updateEntry(match);
		}		
		
		followMatchMap.put(match.matchId, match);
		
		if (cursor != null){
			cursor.close();
		}
	}
    
    private void deleteMatch(Match match) {		
		followMatchMap.remove(match.matchId);
    	followMatchDBAdapter.removeEntry(match.getMatchId());    	
	}
    
	public Match findMatchById(String matchId) {		
		return followMatchMap.get(matchId);
	}
	
	public boolean isMatchFollowed(String matchId){
		return followMatchMap.containsKey(matchId);
	}
		
	public void close() {
		if (followMatchDBAdapter != null){
			followMatchDBAdapter.close();
		}
	}

	/* to be deleted, useless code below
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate(){
		manager = this;
		ScoreApplication.realtimeMatchService.setFollowMatchManager(this);
		
		super.onCreate();
		Log.d(TAG, this.toString() + " <onCreate>");			
		this.init(this.getApplicationContext());
	}
	
	@Override
	public void onStart(Intent intent, int startId){				
		super.onStart(intent, startId);
		Log.d(TAG, this.toString() + " <onStart>");				

		ScoreApplication app = (ScoreApplication)getApplication();
		MatchService matchService = app.getRealtimeMatchService();
		if (matchService != null){
			if (app.getRealtimeMatchActivity() != null){
				matchService.updateAllFollowMatch(app.getRealtimeMatchActivity().getUpdateFollowMatchHandler());
			}
			else{
				Log.d(TAG, "<onCreate> but realtime match activity is null");				
			}
		}
		else{
			Log.d(TAG, "<onCreate> but match service is null");
		}
	}

	@Override
	public void onDestroy(){		
		this.close();
		super.onDestroy();
		Log.d(TAG, this.toString() + " <Destroy>");
	}
	*/

}
