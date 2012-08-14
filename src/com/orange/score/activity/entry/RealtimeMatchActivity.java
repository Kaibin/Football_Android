package com.orange.score.activity.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.common.android.widget.PullToRefreshExpandableListView;
import com.orange.common.android.widget.PullToRefreshListView;
import com.orange.common.android.widget.PullToRefreshListView.OnRefreshListener;
import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.activity.league.SelectLeagueActivity;
import com.orange.score.activity.match.RealtimeMatchDetailActivity;
import com.orange.score.android.service.FollowMatchService;
import com.orange.score.app.ScoreApplication;
import com.orange.score.constants.ScoreType;
import com.orange.score.model.config.ConfigManager;
import com.orange.score.model.league.League;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.match.FilterMatchStatusType;
import com.orange.score.model.match.FollowMatchManager;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchGroup;
import com.orange.score.model.match.MatchManager;
import com.orange.score.network.ResultCodeType;
import com.orange.score.service.LiveUpdateChangeCallBack;
import com.orange.score.service.MatchService;
import com.orange.score.service.MatchServiceCallBack;
import com.orange.score.service.UserService;
import com.orange.score.utils.ToastUtil;
import com.readystatesoftware.viewbadger.BadgeView;

public class RealtimeMatchActivity extends CommonFootballActivity implements MatchServiceCallBack, 
	LiveUpdateChangeCallBack, FollowMatchCallBack {
	
	private static final String TAG = "RealtimeMatchActivity";
	protected static final int ACTION_SELECT_LEAGUE = 20120109;
	protected static final int UPDATE_LIST_VIEW = 0;
	private int visibleLastIndex = 0;    
	private int firstVisibleIndex = 0;

	PullToRefreshListView listView = null;
	RealtimeMatchListAdapter adapter;
	Intent intent = new Intent();
	
	PullToRefreshExpandableListView expandableListView = null;
	RealtimeMatchExpandableListAdapter expandableAdapter = null;
	private int currentSort = 0; //0:time 1:league

	TextView noMatchTextView;
	
	BadgeView badge;
	Button followButton;
	int followCount = 0; // FollowMatchManager.getInstance().getFollowMatchList().size();
	
	Timer updateListViewTimer;
	
	MatchManager matchManager;
	MatchService matchService;
	LeagueManager leagueManager;
	
	FollowMatchManager followMatchManager = null; //FollowMatchManager.getInstance();
//	FollowMatchDBAdapter dbAdapter = new FollowMatchDBAdapter(this);
	
//	Set<String> filterLeagueSet = null;			
	
	public boolean forceRefreshWhenResume = false;
	boolean switchFilterScoreType = true;
	Date lastLoadAllMatchDate = new Date();
	private static final long LOAD_ALL_MATCH_INTERVAL = 30*60*1000;	// 30 minutes
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	isTopActivity = true;
    	
		registerReceiver();
    	
		ScoreApplication.setRealtimeMatchActivity(this);
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtime_match);        
        
        initServicesAndManagers();
        
        // init list view && its adapter
        initListView();
        initExpandableListView();
        
        // init no match title view
        noMatchTextView = (TextView)findViewById(R.id.no_match_view);
        noMatchTextView.setVisibility(View.INVISIBLE);
        
        // set follow button and its badge
        followButton = (Button)findViewById(R.id.button5);
		badge = new BadgeView(this, followButton);
		badge.setBadgeMargin(0, 0);
		badge.setTextSize(10);
		
        // init buttons on title bar
		initScoreTypeButton();
		initSelectLeagueButton();
		initSortLeagueButton();
		
		// init button click listeners
		setMatchFilterButtonClickListener(R.id.button1, FilterMatchStatusType.ALL);
		setMatchFilterButtonClickListener(R.id.button2, FilterMatchStatusType.NOT_STARTED);
		setMatchFilterButtonClickListener(R.id.button3, FilterMatchStatusType.ONGOING);
		setMatchFilterButtonClickListener(R.id.button4, FilterMatchStatusType.FINISH);
		setMatchFilterButtonClickListener(R.id.button5, FilterMatchStatusType.FOLLOWED);
		updateMatchFilterButton();
				
		// add observer on score live update 
		matchService.addScoreLiveUpdateObserver(this);

		// start loading all match data
		loadAllMatch();
		
//		matchService.updateAllFollowMatch(updateFollowMatchHandler);
		
    	if (ConfigManager.getInstance().getIsFirstUsage()){
    		Toast.makeText(this, "温馨提示：比分&指数即时更新数据流量较大，建议在wifi下使用本应用。", Toast.LENGTH_LONG).show();
    		ConfigManager.getInstance().setIsFirstUsage();
    	}
    	
    	if (!ScoreApplication.hasDetectUpdate()){
    		UserService service = new UserService();
    		service.updateClientWhenLaunch(this, MoreActivity.CHECKCLIENT_URL, MoreActivity.UPDATECLIENT_URL);
    		ScoreApplication.setDetectUpdate(); 
    	}

    }
     
    
    
    final Handler updateFollowMatchHandler = new Handler(){
    	public void handleMessage(Message msg) {  
            switch (msg.what) {      
            	default:
            		Log.d(TAG, "updateFollowMatchHandler <handleMessage>");
            		updateFollowCountBadge();
            		if (matchManager.getFilterMatchStatus() == FilterMatchStatusType.FOLLOWED){
            			reloadListDataAndView();            			
            		}
            		break;      
            }      
    		super.handleMessage(msg);  
        }  
    };
    
    private void stopUpdateListViewTimer(){
    	if (updateListViewTimer != null){
    		updateListViewTimer.cancel();
    		updateListViewTimer = null;
    	}    	
    }
    
    final Handler timerTaskHandler = new Handler(){  
    	public void handleMessage(Message msg) {  
        switch (msg.what) {      
             case UPDATE_LIST_VIEW:
            	 adapter.notifyDataSetChanged();
            	 expandableAdapter.notifyDataSetChanged(); 
                 break;      
             }      
             super.handleMessage(msg);  
         }    
     };   
    
    private void startUpdateListViewTimer() {
    	
    	if (updateListViewTimer != null){
    		updateListViewTimer.cancel();
    		updateListViewTimer = null;
    	}
    	
    	updateListViewTimer = new Timer();
    	updateListViewTimer.schedule(new TimerTask() {
	           public void run() {	        	   	        	   
	        	   Log.d(TAG, " fire list view update timer");
	        	   Message message = new Message();      
	        	   message.what = UPDATE_LIST_VIEW;      
	        	   timerTaskHandler.sendMessage(message);
	           }
	        }, 0, 1*1000);		// every 2 seconds	

    }

    private boolean isDataTooOld(){
		Date now = new Date();
		if (now.getTime() - lastLoadAllMatchDate.getTime() > LOAD_ALL_MATCH_INTERVAL){
			return true;
		}
		else{
			return false;
		}

    }
    
	@Override
    protected void onResume() {
    	super.onResume();
    	
//    	this.hideDialog();
    	
    	ScoreApplication.currentActivity = this;
		RealtimeMatchDetailActivity.EXIT_APPLICATION = false;

//    	if (followCount > 0) {
//			badge.setText(followCount + "");
//			badge.show();
//		}
    	
		listView.onRefreshComplete();
		
		// check if the match is too old, if so, load all match again
		if (isDataTooOld() || forceRefreshWhenResume){
			Log.d(TAG, "match data too old, reload all match data");
			loadAllMatch();
			forceRefreshWhenResume = false;
		}
				
//		startReloadAllMatchTimer();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	ScoreApplication.currentActivity = null;
    	
		Log.d(TAG, "unregister follow match broadcast receiver");
//    	stopReloadAllMatchTimer();
    	
    	this.stopUpdateListViewTimer();
    }
    
    @Override
    public void onDestroy() {
    	unregisterReceiver(receiver);

    	Log.d(TAG, "onDestroy");
    	matchService.removeScoreLiveUpdateObserver(this);    	
    	this.stopUpdateListViewTimer();
    	ScoreApplication.setRealtimeMatchActivity(null);
    	super.onDestroy();
    }
    
    
    
	@Override
	public void onBackPressed() {
		RealtimeMatchDetailActivity.EXIT_APPLICATION = true;
        this.finish();
        return;
	}
	
	private void initExpandableListView() {
		expandableListView = (PullToRefreshExpandableListView) findViewById(R.id.expandableListView);
		expandableListView.setFastScrollEnabled(true);
		initializeExpandableAdapter();
		expandableListView.setAdapter(expandableAdapter);
		expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

					@Override
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						Match match = groups.get(groupPosition).children.get(childPosition);
						if (match == null) {
							Log.e(TAG, "RealtimeMatch expandableListView get match at groupPosition " + groupPosition + " childPosition " + childPosition + " null");
							return false;
						}
		                intent.setClass(RealtimeMatchActivity.this, RealtimeMatchDetailActivity.class);
		                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		                Bundle bundle = new Bundle();
		                String matchId = match.getMatchId();
		                
		                Log.d(TAG, "matchId: " + match.getMatchId());
		                
		                bundle.putString("matchId", matchId);
		                intent.putExtras(bundle);
		                startActivity(intent);
						return false;
					}

				});
		expandableListView.setOnRefreshListener(new PullToRefreshExpandableListView.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				refreshCurrentMatchList();				
			}
		});
	}
    
    private void initListView(){
        listView = (PullToRefreshListView) findViewById(R.id.listView1); 
        listView.setFastScrollEnabled(true);
		initializeAdapter();
		listView.setAdapter(adapter);
		listView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
            	refreshCurrentMatchList();
            }
        });

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// why -1? because refreshListView header occupy 1 item
				Match match = adapter.getItem(position - 1);
				if (match == null) {
					Log.e(TAG, "RealtimeMatch adapter get match at position " + position + " null");
					return;
				}
                intent.setClass(RealtimeMatchActivity.this, RealtimeMatchDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                Bundle bundle = new Bundle();
                String matchId = match.getMatchId();
                
                Log.d(TAG, "matchId: " + match.getMatchId());
                
                bundle.putString("matchId", matchId);
                intent.putExtras(bundle);
                startActivity(intent);
			}
			
		});
		
		listView.setOnScrollListener(new OnScrollListener() {
	            @Override
	            public void onScrollStateChanged(AbsListView view, int scrollState) {
	                
	            }
	            
	            @Override
	            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	                
//	            	Log.d(TAG, "firstVisibleItem = " + firstVisibleItem); 
//	                Log.d(TAG, "visibleItemCount = " + visibleItemCount); 
//	                Log.d(TAG, "totalItemCount = " + totalItemCount); 
	                
	                visibleLastIndex = firstVisibleItem + visibleItemCount ;
	       
//	                Log.d(TAG, "visibleLastIndex = " +  visibleLastIndex);
	                
	                if (adapter.getCount() == visibleLastIndex - 1) {  // 2 for pull refresh
	                	if (adapter.nextPage()){
	                		adapter.notifyDataSetChanged();
	                	}
	                }
	            }
	        });
		
    }
    
	private void initServicesAndManagers() {
//		dbAdapter.open();
//		followMatchManager.setFollowMatchDBAdapter(dbAdapter);
		matchService = this.getMatchService();
		matchManager = this.getMatchManager();
		leagueManager = this.getLeagueManager();
	}
	
	private ScoreType buttonIndexToScoreType(int whichButton) {
		switch (whichButton){
		case 0:
		default:
			return ScoreType.FIRST;		
		case 1:
			return ScoreType.ALL;
		case 2:
			return ScoreType.ZUCAI;
		case 3:
			return ScoreType.JINGCAI;
		case 4:
			return ScoreType.DANCHANGE;		
		}
	}
	
	private int buttonIndexFromScoreType(ScoreType filterScoreType) {
		switch (filterScoreType){
		case FIRST:
			return 0;		
		case ALL:
			return 1;
		case ZUCAI:
			return 2;
		case JINGCAI:
			return 3;
		case DANCHANGE:
			return 4;		
		}
		
		return 0;
	}
	
	private String getScoreTypeString(ScoreType scoreType){
		switch (scoreType){
		case ALL:
			return getResources().getString(R.string.score_type_all);
		case JINGCAI:
			return getResources().getString(R.string.score_type_jingcai);
		case DANCHANGE:
			return getResources().getString(R.string.score_type_danchang);		
		case ZUCAI:
			return getResources().getString(R.string.score_type_zucai);
		default:
		case FIRST:
			return getResources().getString(R.string.score_type_first);		
		}
	}
	
	private void updateScoreTypeButton(){
		Button button = (Button) findViewById(R.id.button_filter_score_type);
		button.setText(getScoreTypeString(matchManager.getFilterScoreType()));
	}
	
	private void initSelectLeagueButton(){
        Button button = (Button) findViewById(R.id.button_filter_league);
        button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
                intent.setClass(RealtimeMatchActivity.this, SelectLeagueActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(SelectLeagueActivity.KEY_LEAGUE_FROM, 
                		LeagueManager.LEAGUE_FROM_REALTIME_MATCH);

                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.addAll(matchManager.getFilterLeagueSet());
                bundle.putStringArrayList(SelectLeagueActivity.KEY_SELECTED_LEAGUE, arrayList);
                intent.putExtras(bundle);
                startActivityForResult(intent, ACTION_SELECT_LEAGUE);
			}
		});        
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTION_SELECT_LEAGUE) {
			if (data == null){
				Log.d(TAG, "onActivityResult but data is null");
				return;				
			}
			
			// handle select league result (return a league id list)
			ArrayList<String> selectedList = null;
			selectedList = data.getStringArrayListExtra(SelectLeagueActivity.KEY_SELECTED_LEAGUE);
			if (selectedList == null){
				Log.d(TAG, "onActivityResult but select league list is null");
				return;
			}
			
//			HashSet<String> filterLeagueSet = new HashSet<String>(selectedList);
			Log.d(TAG, "onActivityResult, select league list is " + selectedList);
			matchManager.selectLeagues(selectedList);
			this.reloadListDataAndView();
		}
	}
	
	private void initSortLeagueButton () {
        expandableListView.onRefreshComplete();
		Button button = (Button) findViewById(R.id.button_sort_type);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(RealtimeMatchActivity.this)
//		        .setIcon(R.drawable.alert_dialog_icon)
		        .setTitle(R.string.score_sort_type_prompt)
		        .setSingleChoiceItems(R.array.match_sort_types, currentSort, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		                /* User clicked on a radio button do some stuff */
		            	Log.d(TAG, "click " + whichButton);
		            	dialog.dismiss();
		            	currentSort = whichButton;
		            	
		            	switch (whichButton) {
						case 0://default
							listView.setVisibility(View.VISIBLE);
							expandableListView.setVisibility(View.GONE);
							break;
						case 1://sort by league
							listView.setVisibility(View.GONE);
							expandableListView.setVisibility(View.VISIBLE);
							break;

						default:
							break;
						} 
		            	
		            }

		        })
		        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		                /* User clicked No so do some stuff */
		            	Log.d(TAG, "click cancel");
		            }
		        })
		       .create();
		        
		        dialog.show();
			}
		});
	}
	
	private void initScoreTypeButton() {
        Button button = (Button) findViewById(R.id.button_filter_score_type);
        updateScoreTypeButton();
        button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentIndex = buttonIndexFromScoreType(matchManager.getFilterScoreType());
				Log.d(TAG, "current index = " + currentIndex);
		        Dialog dialog = new AlertDialog.Builder(RealtimeMatchActivity.this)
//		        .setIcon(R.drawable.alert_dialog_icon)
		        .setTitle(R.string.score_filter_type_prompt)
		        .setSingleChoiceItems(R.array.match_filter_types, currentIndex, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		                /* User clicked on a radio button do some stuff */
		            	Log.d(TAG, "click " + whichButton);
		            	dialog.dismiss();
		            	
		            	ScoreType scoreType = buttonIndexToScoreType(whichButton);		            	
		            	if (scoreType != matchManager.getFilterScoreType() || isDataTooOld()){
		            		// update data and reload match
		            		switchFilterScoreType = true;
		            		matchManager.setFilterScoreType(scoreType);
		            		loadAllMatch();
		            		
		            		// update UI
		            		updateScoreTypeButton();
		            		updateFollowCountBadge();
		            		
		            	}
		            	else{
		            		switchFilterScoreType = false;
		            	}
		            	
		            }

		        })
//		        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
//		            public void onClick(DialogInterface dialog, int whichButton) {
//		                /* User clicked Yes so do some stuff */
//		            }
//		        })
		        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		                /* User clicked No so do some stuff */
		            	Log.d(TAG, "click cancel");
		            }
		        })
		       .create();
		        
		        dialog.show();
			}


		});
        

	}

    private void initializeAdapter () {
		List<Match> list = matchManager.filterMatch();
        adapter = new RealtimeMatchListAdapter(list, this, this);
	}
    
    List<MatchGroup> groups = new ArrayList<MatchGroup>();
    
    private void initializeExpandableAdapter() {
    	List<Match> list = matchManager.filterMatch();
    	groups = composeListToGroup(list);
    	expandableAdapter = new RealtimeMatchExpandableListAdapter(groups, this, this);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<MatchGroup> composeListToGroup(List<Match> list) {
    	Map<String, List<Match>> map = new HashMap<String, List<Match>>();
    	
    	for (Match match : list) {
			String leagueId = match.getLeagueId();
			
			List<Match>  matchList = map.get(leagueId);
			if (matchList == null || matchList.size() == 0) {
				matchList = new ArrayList<Match>();
				matchList.add(match);
			} else {
				matchList.add(match);
			}
			map.put(leagueId, matchList);
		}
		groups.clear();
		
		List<League> leagueList = leagueManager.findAllLeagues();
		if (leagueList != null && leagueList.size() > 0) {
			for (League league : leagueList) {
				for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
					Entry entry = (Entry)iterator.next();
					String leagueId = (String) entry.getKey();
					if (leagueId.equals(league.getLeagueId())) {
						List<Match> children = (List<Match>) entry.getValue();
						MatchGroup group = new MatchGroup(league.getLeagueName(), children);
						groups.add(group);
						break;
					}
				}
			}
		}
		return groups;
	}
    
	private void updateMatchFilterButton(){
    	int buttonId = R.id.button1;
    	FilterMatchStatusType status = matchManager.getFilterMatchStatus();
    	switch (status){
    	case ALL:    		
    		buttonId = R.id.button1;
    		break;
    	case NOT_STARTED:
    		buttonId = R.id.button2;
    		break;
    	case ONGOING:
    		buttonId = R.id.button3;
    		break;
    	case FINISH:
    		buttonId = R.id.button4;
    		break;
    	case FOLLOWED:
    		buttonId = R.id.button5;
    		break;    		
    	}
    	
    	int id[] = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5};
    	for (int i=0; i<id.length; i++){
    		Button button = (Button)findViewById(id[i]);
    		if (id[i] == buttonId){
    			button.setSelected(true);
    		}
    		else{
    			button.setSelected(false);
    		}
    	}
    }
    
	private void setMatchFilterButtonClickListener(final int buttonId, final FilterMatchStatusType status) {
		listView.onRefreshComplete();
		
		Button button = (Button)findViewById(buttonId);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (buttonId == R.id.button5) {
					listView.supportRefreshHeader = false;
				} else {
					listView.supportRefreshHeader = true;
				}
				
				matchManager.setFilterMatchStatus(status);
				updateMatchFilterButton();
				if (isDataTooOld()){
					loadAllMatch();
				}
				else{
//					List<Match> filterMatchList = matchManager.filterMatch();
					reloadListDataAndView();
				}
			}
		});
	}
    
	private MatchService getMatchService(){		
		ScoreApplication app = (ScoreApplication)getApplication();
		MatchService service = app.getRealtimeMatchService();
		return service;
	}
	
	private MatchManager getMatchManager(){
		MatchService service = getMatchService();
		return service.getMatchManager();
	}
	
	private LeagueManager getLeagueManager(){
		MatchService service = getMatchService();
		return service.getLeagueManager();
	}
	
	private void loadAllMatch() {		
		matchService.loadAllMatch(this, this);
	}

	@Override
	public void loadAllMatchFinish(ResultCodeType resultCode) {
		switch (resultCode){
			case SUCCESS:
			{
				// change filter league
				if (switchFilterScoreType || matchManager.getFilterLeagueSet().isEmpty()){
					matchManager.selectAllLeagues(matchService.getLeagueManager().findAllLeagues());
				}
//				else if (filterLeagueSet != null && filterLeagueSet.size() > 0) {
//					matchManager.setFilterLeagueSet(filterLeagueSet);
//				} 
//				else{
//					// keep current selection, no change
//				}

				this.reloadListDataAndView();
					            
	            // Call onRefreshComplete when the list has been refreshed.
	            listView.onRefreshComplete();	      
	            
	            lastLoadAllMatchDate = new Date();
			}
			break;
			
			default:
			{
				listView.onRefreshComplete();
				break;
			}
		}
		
	}
	
//	private void updateMatchContentList(List<Match> filterMatchList) {
//		matchContentList.clear();
//		for (Match match : filterMatchList){
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("HOME_TEAM_NAME", match.getAwayTeamName());
//			map.put("AWAY_TEAM_NAME", match.getHomeTeamName());
//			map.put("MATCH_ID", match.getMatchId());
//			matchContentList.add(map);
//		}
//
//	}
	
	private void updateMatchContentList(List<Match> filterMatchList) {
		List<Match> matchItemList = new ArrayList<Match>();
		if (filterMatchList != null) {
			for (Match match : filterMatchList) {
				matchItemList.add(match);
			}
		}		
		
		if (matchItemList.size() > 0)
			noMatchTextView.setVisibility(View.INVISIBLE);
		else
			noMatchTextView.setVisibility(View.VISIBLE);
		
		adapter.updateList(matchItemList);
		
		//for expandabe listview
		groups = composeListToGroup(matchItemList);
		expandableAdapter.updateList(groups);
		expandableAdapter.notifyDataSetChanged();
		int groupCount = expandableAdapter.getGroupCount();
		if (groups != null && groupCount > 0) {
			for (int i = 0; i < groupCount; i++) {
				expandableListView.expandGroup(i);
			}
		}
	}

	private void updateMatchContentList() {
		List<Match> filterMatchList = matchManager.filterMatch();
		updateMatchContentList(filterMatchList);
	}

	private void reloadListDataAndView(){
		
		stopUpdateListViewTimer();
		
		updateMatchContentList();		
		
		startUpdateListViewTimer();
	}
	
	@Override
	public void notifyScoreLiveUpdate(List<String[]> list) {
		
		Log.d(TAG, "notifyScoreLiveUpdate");
		
		if (list == null || list.size() == 0)
			return;
		
		this.reloadListDataAndView();
	}

	private void incrementFollow(){
		followCount++;
		badge.setText(followCount + "");
		badge.show();
	}
	
	private void decrementFollow(){
		if (followCount > 0) {
			followCount--;
		}
		if (followCount > 0) {
			badge.setText(followCount + "");
			badge.show();
		} else{
			badge.hide();
		}
	}
	
	public void updateFollowCountBadge(){
		if (followMatchManager == null){
			followMatchManager = FollowMatchService.getFollowMatchManager();
		}
		
		int followCount = followMatchManager.getFollowMatchList().size();
		Log.d(TAG, "<updateFollowCountBadge> count is " + followCount);
		if (followCount > 0) {
			badge.setText(String.valueOf(followCount));
			badge.show();
		} else{
			badge.hide();
		}
		
	}

	@Override
	public void followUnfollowMatch(Match match) {
		if (followMatchManager == null){
			followMatchManager = FollowMatchService.getFollowMatchManager();
		}
		
		if (match.isFollow()){
//			decrementFollow();
			followMatchManager.unfollowMatch(match);
//			Toast.makeText(this, getResources().getString(R.string.unfollow_success), Toast.LENGTH_SHORT).show();  
			ToastUtil.showMessage(this, getResources().getString(R.string.unfollow_success));
		}
		else{
//			incrementFollow();
			followMatchManager.followMatch(match);			
//			Toast.makeText(this, getResources().getString(R.string.follow_success), Toast.LENGTH_SHORT).show();  
			ToastUtil.showMessage(this, getResources().getString(R.string.follow_success));
		}

		updateFollowCountBadge();
		
		if (matchManager.getFilterMatchStatus() == FilterMatchStatusType.FOLLOWED){
			reloadListDataAndView();
		}
       
	}
	
	/*
	 * useless code below, to be deleted
	 * 
	static int RELOAD_TIMER_INTERVAL = 30*60*1000;	// 30 minutes
	Timer reloadAllMatchTimer = null; // timer to reload all match in the morning
	public void startReloadAllMatchTimer(){		
		
		stopReloadAllMatchTimer();
		
		reloadAllMatchTimer = new Timer();
		reloadAllMatchTimer.schedule(new TimerTask(){
			@Override
			public void run() {				
				Log.d(TAG, "fire reload all match data timer");			
				refreshCurrentMatchList();
			}
			
		},  RELOAD_TIMER_INTERVAL, RELOAD_TIMER_INTERVAL);
	}
	
	public void stopReloadAllMatchTimer(){
		if (reloadAllMatchTimer != null){
			reloadAllMatchTimer.cancel();
			reloadAllMatchTimer = null;
		}
	}
	*/

	@Override
	public void loadMatchDetailFinish(ResultCodeType resultCode) {
		// TODO Auto-generated method stub
		
	}
	
	public void refreshCurrentMatchList(){
		switchFilterScoreType = false;
    	loadAllMatch();		
	}

	@Override
	public void onRefresh() {
		refreshCurrentMatchList();
	}

	public Handler getUpdateFollowMatchHandler() {
		return this.updateFollowMatchHandler;
	}

	UpdateFollowMatchBroadcastReceiver receiver = new UpdateFollowMatchBroadcastReceiver(); 
	
	public void registerReceiver(){
		Log.d(TAG, "register follow match broadcast receiver");
		IntentFilter filter;
		filter = new IntentFilter(FollowMatchService.FOLLOW_MATCH_BROADCAST);
		registerReceiver(receiver, filter);
	}
	
	public class UpdateFollowMatchBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "follow match broadcast message received");
			updateFollowCountBadge();			
			if (matchManager.getFilterMatchStatus() == FilterMatchStatusType.FOLLOWED){
				reloadListDataAndView();
			}			
		}						
	}
}
