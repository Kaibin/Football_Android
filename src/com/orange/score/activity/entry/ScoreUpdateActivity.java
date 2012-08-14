package com.orange.score.activity.entry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.activity.entry.RealtimeMatchActivity.UpdateFollowMatchBroadcastReceiver;
import com.orange.common.util.DateUtil;
import com.orange.score.R;
import com.orange.score.android.service.FollowMatchService;
import com.orange.score.android.service.ScoreUpdateService;
import com.orange.score.app.ScoreApplication;
import com.orange.score.model.match.FilterMatchStatusType;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchManager;
import com.orange.score.model.match.ScoreUpdate;
import com.orange.score.network.ScoreNetworkRequest;
import com.orange.score.service.LiveUpdateChangeCallBack;
import com.orange.score.service.MatchService;

public class ScoreUpdateActivity extends CommonFootballActivity{

	protected static final String TAG = "ScoreUpdateActivity";

	TextView noMatchTextView;
	TextView dateTextView;
	
	MatchManager matchManager;
	MatchService matchService;
	
	Button editButton;
	Button doneButton;
	Button clearButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
    	isTopActivity = true;

		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_update);        
        
        initServicesAndManagers();
        
        // init list view && its adapter
        initListView();
        
        // init no match title view
        noMatchTextView = (TextView)findViewById(R.id.no_score_update_view);
        updateNoMatchTextView();
        
        dateTextView = (TextView)findViewById(R.id.score_update_current_date);
		
        // init buttons on title bar
		initEditButton();
		initClearButton();
		initDoneButton();
		
		updateButtons();
		
//		initSelectLeagueButton();
		
		// init button click listeners
//		setMatchFilterButtonClickListener(R.id.button1, FilterMatchStatusType.ALL);
//		setMatchFilterButtonClickListener(R.id.button2, FilterMatchStatusType.NOT_STARTED);
//		setMatchFilterButtonClickListener(R.id.button3, FilterMatchStatusType.ONGOING);
//		setMatchFilterButtonClickListener(R.id.button4, FilterMatchStatusType.FINISH);
//		setMatchFilterButtonClickListener(R.id.button5, FilterMatchStatusType.FOLLOWED);
//		updateMatchFilterButton();
//				

//		test();
		
		registerReceiver();
    }
	


	@Override
	public void onResume(){
		super.onResume();
		
		// update date time
		updateDateTextView();
		
		updateScoreUpdateListView();
		
    	ScoreApplication.currentActivity = this;

	}
	
	@Override
	public void onDestroy(){
		unregisterReceiver(receiver);
		super.onDestroy();		
	}
	
    @Override
    protected void onPause() {
    	super.onPause();
    	ScoreApplication.currentActivity = null;
    }
		
	private void updateDateTextView() {
		Date date = new Date();
		String dateString = DateUtil.dateToChineseStringByFormat(date, "yyyy年MM月dd日 E");
		dateString = dateString.replaceAll("周", "星期");
		dateTextView.setText(dateString);
	}

	private void initEditButton() {
		editButton = (Button) findViewById(R.id.button_edit_score_update);
		editButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "click edit button");
				//test();
				
				adapter.turnOnEditMode();
				updateButtons();
				
				updateScoreUpdateListView();
			}
		});     
	}

	private void initDoneButton() {
		doneButton = (Button) findViewById(R.id.button_edit_score_done);
		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "click done button");
				
				adapter.turnOffEditMode();
				updateButtons();
				
				updateScoreUpdateListView();
			}
		});     	
    }

	private void initClearButton() {
		clearButton = (Button) findViewById(R.id.button_edit_score_clear);
		clearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "click clear button");
				ScoreUpdateService.clearData();
				updateScoreUpdateListView();
			}
		});     	
	}
	
	private void updateButtons() {
		if (adapter.editMode){
			doneButton.setVisibility(View.VISIBLE);
			clearButton.setVisibility(View.VISIBLE);
			editButton.setVisibility(View.GONE);
		}
		else{
			doneButton.setVisibility(View.GONE);
			clearButton.setVisibility(View.GONE);
			editButton.setVisibility(View.VISIBLE);
		}
		
		if (scoreUpdateList.size() > 0){
			editButton.setEnabled(true);
		}
		else{
			editButton.setEnabled(false);
		}

	}
	
	List<ScoreUpdate> scoreUpdateList;
	ListView listView;
	ScoreUpdateListAdapter adapter;
	
	private void initializeAdapter () {
		scoreUpdateList = ScoreUpdateService.getScoreUpdateList();
        adapter = new ScoreUpdateListAdapter(scoreUpdateList, this);
	}
	
	private void initListView(){
        listView = (ListView) findViewById(R.id.score_update_list_view); 
		initializeAdapter();
		listView.setAdapter(adapter);		
		
		listView.setOnScrollListener(new OnScrollListener() {
	            @Override
	            public void onScrollStateChanged(AbsListView view, int scrollState) {
	                
	            }
	            
	            @Override
	            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	            	adapter.onScroll(firstVisibleItem, visibleItemCount);
	            }
	        });
    }
		
	private void initServicesAndManagers() {
		ScoreApplication app = (ScoreApplication)getApplication();
		MatchService service = app.getRealtimeMatchService();		
		matchService = app.getRealtimeMatchService();
		matchManager = service.getMatchManager();
	}


//	public static final int INDEX_REALTIME_SCORE_MATCHID = 0;
//	public static final int INDEX_REALTIME_SCORE_STATUS = 1;
//	public static final int INDEX_REALTIME_SCORE_DATE = 2;
//	public static final int INDEX_REALTIME_SCORE_START_DATE = 3;
//	public static final int INDEX_REALTIME_SCORE_HOME_TEAM_SCORE = 4;
//	public static final int INDEX_REALTIME_SCORE_AWAY_TEAM_SCORE = 5;
//	public static final int INDEX_REALTIME_SCORE_HOME_TEAM_FIRST_HALF_SCORE = 6;
//	public static final int INDEX_REALTIME_SCORE_AWAY_TEAM_FIRST_HALF_SCORE = 7;
//	public static final int INDEX_REALTIME_SCORE_HOME_TEAM_RED = 8;
//	public static final int INDEX_REALTIME_SCORE_AWAY_TEAM_RED = 9;
//	public static final int INDEX_REALTIME_SCORE_HOME_TEAM_YELLOW = 10;
//	public static final int INDEX_REALTIME_SCORE_AWAY_TEAM_YELLOW = 11;
	
	private void test(){
		List<String[]> list = new ArrayList<String[]>();
		String[] strings1 = {"664575","0","20120202163000","20120202163000","0","1","1","0","0","0","0","0"};
		String[] strings2 = {"664575","0","20120202163000","20120202163000","0","0","0","0","1","0","0","0"};
		String[] strings3 = {"664575","0","20120202163000","20120202163000","0","0","0","0","0","0","0","1"};
		
		list.add(strings1);
		list.add(strings2);
		list.add(strings3);
		
//		notifyScoreLiveUpdate(list);
	}
	
	private void updateNoMatchTextView(){
		if (scoreUpdateList.size() > 0){
			noMatchTextView.setVisibility(View.GONE);	
		}
		else{
			noMatchTextView.setVisibility(View.VISIBLE);
		}		
	}
	
	private void updateScoreUpdateListView(){
		adapter.updateList(scoreUpdateList);
		
		updateNoMatchTextView();

		updateButtons();		
		adapter.notifyDataSetChanged();
	}



	@Override
	public void onRefresh() {
		matchService.startLiveScoreUpdateTimer(true);
	}

	
	ScoreUpdateBroadcastReceiver receiver = new ScoreUpdateBroadcastReceiver(); 
	
	public void registerReceiver(){
		Log.d(TAG, "register score update broadcast receiver");
		IntentFilter filter;
		filter = new IntentFilter(ScoreUpdateService.SCORE_UPDATE_BROADCAST);
		registerReceiver(receiver, filter);
	}
	
	public class ScoreUpdateBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "score update broadcast message received");
			updateScoreUpdateListView();
		}						
	}

}
