package com.orange.score.activity.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.orange.common.android.widget.PullToRefreshExpandableListView;
import com.orange.common.android.widget.PullToRefreshExpandableListView.OnRefreshListener;
import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.activity.index.SelectCompanyActivity;
import com.orange.score.activity.league.SelectLeagueActivity;
import com.orange.score.app.ScoreApplication;
import com.orange.score.constants.ScoreType;
import com.orange.score.model.index.CompanyManager;
import com.orange.score.model.index.Odds;
import com.orange.score.model.index.OddsGroup;
import com.orange.score.model.index.OddsGroupTitle;
import com.orange.score.model.index.OddsManager;
import com.orange.score.model.league.League;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchManager;
import com.orange.score.model.match.MatchStatusType;
import com.orange.score.network.ResultCodeType;
import com.orange.score.service.IndexService;
import com.orange.score.service.IndexServiceCallBack;
import com.orange.score.service.OddsUpdateChangeCallBack;
import com.orange.score.utils.DateUtil;

public class RealtimeIndexActivity2 extends CommonFootballActivity implements IndexServiceCallBack, ExpandableListView.OnGroupClickListener, OddsUpdateChangeCallBack{
	private static final String TAG = "RealtimeIndexActivity";
	public static String DATE_FORMAT = "yyyy-MM-dd";
	Button buttonFilterCompany;
	Button buttonFilterLeague;
	Button buttonLookBack;
	Button buttonFilterScoreType;
	TextView noOddsTextView;
	
	MatchManager matchManager;
	IndexService indexService;
	OddsManager oddsManager;
	CompanyManager companyManager;
	LeagueManager leagueManager;
	
	CharSequence[] dateList;
	int currentDateIndex = 0;
	String date;
	String companyId = null;
	Map<Match, List<Odds>> matchOddsMap = new HashMap<Match, List<Odds>>();
	List<League> allLeagues = new ArrayList<League>();
	List<League> topLeagues = new ArrayList<League>();
	
	Set<String> selectedSet = new HashSet<String>();
	private List<OddsGroup> groups;
	private RealtimeIndexOddsListAdapter adapter;
//	private ExpandableListView listView;
	private PullToRefreshExpandableListView listView;
	boolean firstLoad = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
    	isTopActivity = true;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.realindex2);
		
		matchManager = getMatchManager();
		indexService = getIndexService();
		oddsManager = indexService.getOddsManager();
		companyManager = indexService.getCompanyManager();
		leagueManager = indexService.getLeagueManager();
		date = DateUtil.dateToStringByFormat(new Date(),DATE_FORMAT);
		
		//加载赔率公司数据
		loadCompanyData();
		
		buttonFilterCompany = (Button) findViewById(R.id.button_filter_company_type);
		buttonFilterLeague = (Button) findViewById(R.id.button_filter_league);
		buttonLookBack = (Button) findViewById(R.id.button_lookback);
		buttonFilterScoreType = (Button) findViewById(R.id.button_filter_score_type);
		
		
		setFilterCompanyButtonOnClickListener();
		setFilterLeagueButtonOnClickListener();
		setLookBackButtonOnClickListener();
		setFilterLevelButtonOnClickListener();
		
		initListView();
		
		 // init no match title view
        noOddsTextView = (TextView)findViewById(R.id.no_real_odds_view);
        updateNoOddsTextView();
		
		indexService.addOddsLiveUpdateObserver(this);
	}
	
	private void updateNoOddsTextView() {
		if (groups.size() > 0){
			noOddsTextView.setVisibility(View.GONE);	
		}
		else{
			noOddsTextView.setVisibility(View.VISIBLE);
		}			
	}

	private void initListView(){
		listView = (PullToRefreshExpandableListView) findViewById(R.id.oddslist);
		groups = new ArrayList<OddsGroup>();
		adapter = new RealtimeIndexOddsListAdapter(this, groups, companyManager);
		listView.setAdapter(adapter);
		listView.setOnGroupClickListener(this);
		listView.setFastScrollEnabled(true);
		listView.setDividerHeight(0);
		listView.setOnRefreshListener(new OnRefreshListener(){  
			  
            @Override  
            public void onRefresh() {  
                Log.d(TAG, "pull to refresh!");
        		loadAllOddsData();
            }
        });
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
		button.setText(getScoreTypeString(oddsManager.getFilterScoreType()));
	}

	
	private void setFilterLevelButtonOnClickListener() {
		buttonFilterScoreType.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//清空选中的联赛
				selectedSet.clear();
//				int currentLevelIndex = oddsManager.getFilterScoreType().intValue();
				int currentLevelIndex = buttonIndexFromScoreType(oddsManager.getFilterScoreType());
				Dialog dialog = new AlertDialog.Builder(
						RealtimeIndexActivity2.this)
						.setTitle("请选择赛事类型")
						.setSingleChoiceItems(R.array.match_filter_types, currentLevelIndex,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Log.d(TAG, "click " + whichButton);
										dialog.dismiss();
										
										ScoreType scoreType = buttonIndexToScoreType(whichButton);		            	
						            	if (scoreType != oddsManager.getFilterScoreType()){
						            		// update data and reload match
						            		oddsManager.setFilterScoreType(scoreType);
						            		
						            		// update UI
						            		updateScoreTypeButton();
						            		loadAllOddsData();
						            		
						            	}
						            	
						            	
									}

								})
						.setNegativeButton(R.string.alert_dialog_cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Log.d(TAG, "click cancel");
									}
								}).create();
				dialog.show();

			}
		});
		
	}
	
	private void setFilterCompanyButtonOnClickListener() {
		buttonFilterCompany.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//清空选中的联赛
				selectedSet.clear();
				
				Intent intent = new Intent();
                intent.setClass(RealtimeIndexActivity2.this, SelectCompanyActivity.class);
                startActivityForResult(intent, SelectCompanyActivity.ACTION_SELECT_COMPANY);
			}
		});
	}
	
	private void setFilterLeagueButtonOnClickListener() {
		buttonFilterLeague.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
                intent.setClass(RealtimeIndexActivity2.this, SelectLeagueActivity.class);
                
                Bundle bundle = new Bundle();
                bundle.putInt(SelectLeagueActivity.KEY_LEAGUE_FROM, LeagueManager.LEAGUE_FROM_REALTIME_INDEX);
                ArrayList<String> arrayList = new ArrayList<String>();
                //设置选中的联赛
                arrayList.addAll(leagueManager.getFilterLeagueSet());
                bundle.putStringArrayList(SelectLeagueActivity.KEY_SELECTED_LEAGUE, arrayList);
                intent.putExtras(bundle);
                startActivityForResult(intent, SelectLeagueActivity.ACTION_SELECT_LEAGUE);
				
			}
		});
		
	}

	private void setLookBackButtonOnClickListener() {
		buttonLookBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//清空选中的联赛
				selectedSet.clear();
				
				Dialog dialog = new AlertDialog.Builder(
						RealtimeIndexActivity2.this)
						.setTitle("历史回查")
						.setSingleChoiceItems(getDateList(), currentDateIndex,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Log.d(TAG, "click " + whichButton);
										dialog.dismiss();
										currentDateIndex = whichButton;
										date = (String) dateList[whichButton];
										loadAllOddsData();
									}

								})
						.setNegativeButton(R.string.alert_dialog_cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Log.d(TAG, "click cancel");
									}
								}).create();
				dialog.show();

			}
		});
	}
	
	private CharSequence[] getDateList() {
		String[] dateArray = new String[7];
		TimeZone timeZone = TimeZone.getTimeZone("GMT+0800");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(new Date());
		Date previousDate;
		String previousDateString;
		
		for (int i = 0; i < dateArray.length; i++) {
			if (i== 0) {
				calendar.add(Calendar.DAY_OF_MONTH, 0);
			} else {
				calendar.add(Calendar.DAY_OF_MONTH, -1);
			}
			previousDate = calendar.getTime();
			previousDateString = DateUtil.dateToStringByFormat(previousDate, DATE_FORMAT);
			dateArray[i] = previousDateString;
		}
		dateList = dateArray;
		return dateArray;
	}
	
	private void setCompanyIdFromList(List<String> list){
		companyId = list.toString();
		companyId = companyId.substring(1, companyId.length()-1).replace(" ", "");
		Log.d(TAG, "load Odds data of company: " + companyId);
	}
	
	private void loadAllOddsData() {
		//加载完公司数据后加载赔率数据
		indexService.loadOddsData(this, this, date, companyId);
	}
	
	private void loadCompanyData() {
		indexService.loadCompanyData(this, this);
	}
	
	@Override
	public void loadAllCompanyFinish(ResultCodeType resultCode) {
		switch (resultCode) {
		case SUCCESS: 
			setCompanyIdFromList(companyManager.getDefaultSelectedCompanyIdList());
			loadAllOddsData();
			listView.onRefreshComplete();
			break;
		default:
			break;
		}
	}

	@Override
	public void loadOddsDataFinish(ResultCodeType resultCode) {
		switch (resultCode) {
		case SUCCESS:
			topLeagues = leagueManager.findAllTopLeagues();
			allLeagues = leagueManager.findAllLeagues();
			// for refresh 
			if (selectedSet != null && selectedSet.size() > 0) {
				List<String> selectedlist = new ArrayList<String>();
				selectedlist.addAll(selectedSet);
				oddsManager.setFilterLeagueIdList(selectedlist);
			} 
			else {
				switch (oddsManager.getFilterScoreType()) {
				case FIRST:
					selectTopLeagues();
					break;
				case ALL:
					selectAllLeagues();
					break;
				case DANCHANGE:
					selectAllLeagues();
					break;
				case JINGCAI:
					selectAllLeagues();
					break;
				case ZUCAI:
					selectAllLeagues();
				default:
					break;
				}
			}
			this.reloadListDataAndView();
			break;
		default:
			break;
		}
	}
	
	private void selectAllLeagues() {
		List<String> filterLeagueIdList = new ArrayList<String>();
		for (League league : allLeagues) {
			filterLeagueIdList.add(league.getLeagueId());
		}
		Set<String> selectedSet = new HashSet<String>();
		selectedSet.addAll(filterLeagueIdList);
		leagueManager.setLeagueList(allLeagues);
		leagueManager.setFilterLeagueSet(selectedSet);
		oddsManager.setFilterLeagueIdList(filterLeagueIdList);
	}
	
	private void selectTopLeagues() {
		List<String> filterLeagueIdList2 = new ArrayList<String>();
		for (League league : topLeagues) {
			filterLeagueIdList2.add(league.getLeagueId());
		}
		Set<String> selectedSet2 = new HashSet<String>();
		selectedSet2.addAll(filterLeagueIdList2);
		leagueManager.setLeagueList(topLeagues);
		leagueManager.setFilterLeagueSet(selectedSet2);
		oddsManager.setFilterLeagueIdList(filterLeagueIdList2);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null){
			Log.d(TAG, "onActivityResult but data is null");
			return;				
		}
		switch (requestCode) {
		case SelectLeagueActivity.ACTION_SELECT_LEAGUE:{
			// handle select league result (return a league id list)
			ArrayList<String> selectedList = null;
			selectedList = data.getStringArrayListExtra(SelectLeagueActivity.KEY_SELECTED_LEAGUE);
			if (selectedList == null){
				Log.d(TAG, "onActivityResult but select league list is null");
				return;
			}
			
			Log.d(TAG, "onActivityResult, select league list is " + selectedList);
//			Set<String> selectedSet = new HashSet<String>();
			selectedSet.clear();
			selectedSet.addAll(selectedList);
			leagueManager.setFilterLeagueSet(selectedSet);
			oddsManager.setFilterLeagueIdList(selectedList);
			this.reloadListDataAndView();
		}
			break;
		case SelectCompanyActivity.ACTION_SELECT_COMPANY:{
			//handle companyId here
			ArrayList<String> selectedList = null;
			selectedList = data.getStringArrayListExtra(SelectCompanyActivity.KEY_SELECTED_COMPANY);
			if (selectedList == null) {
				Log.d(TAG, "onActivityResult but select company list is null");
				return;
			}
			Log.d(TAG, "onActivityResult, select company list is " + selectedList);
			setCompanyIdFromList(selectedList);
			loadAllOddsData();
			this.reloadListDataAndView();
		}
			break;

		default:
			break;
		}
	}
	
	private void reloadListDataAndView() {
		
		if (firstLoad) {
			//默认一级赛事
			List<String> filterLeagueIdList = new ArrayList<String>();
			for (League league : topLeagues) {
				filterLeagueIdList.add(league.getLeagueId());
			}
			oddsManager.setFilterLeagueIdList(filterLeagueIdList);
			//设置筛选的联赛
			leagueManager.setLeagueList(topLeagues);
			Set<String> filterSet = new HashSet<String>();
			filterSet.addAll(filterLeagueIdList);
			leagueManager.setFilterLeagueSet(filterSet);

			firstLoad = false;
		}
		
		matchOddsMap = oddsManager.filterOddsByOddsType(companyManager.getOddsType());
		updateMatchOddsMap();
		updateNoOddsTextView();
		adapter.notifyDataSetChanged();
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map.Entry[] getSortedMapEntry(Map map) {
		Set set = map.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);

		Arrays.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Match  match1 = (Match)((Map.Entry) arg0).getKey();
				Match  match2 = (Match)((Map.Entry) arg1).getKey();
				return match1.compareTo(match2);
			}
		});

		return entries;
	}
	
	public void updateMatchOddsMap() {
		groups.clear();
		Map.Entry[] entries = getSortedMapEntry(matchOddsMap);
		for (int i = 0; i < entries.length; i++) {
			Match match = (Match) entries[i].getKey();
			if(!oddsManager.canDisplayOdds(match, date)){
//				Log.d(TAG, "match of match id: " + match.getMatchId() + " not to display because match status: " + match.getStatus());
				continue;
			}
			List<Odds> group_childrenList =  (List<Odds>) entries[i].getValue();
			OddsGroup oddsGroup = new OddsGroup(match, group_childrenList);
			groups.add(oddsGroup);
		}
		
		//expand all groups
		int groupCount = adapter.getGroupCount();
		for (int i = 0; i < groupCount; i++) {
			listView.expandGroup(i);
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		listView.onRefreshComplete();
		ScoreApplication.currentActivity = this;
	}

	@Override
	protected void onPause() {
		super.onPause();
		ScoreApplication.currentActivity = null;
	}
	
	private MatchManager getMatchManager(){
		IndexService service = getIndexService();
		return service.getMatchManager();
	}
	
	private IndexService getIndexService(){		
		ScoreApplication app = (ScoreApplication)getApplication();
		IndexService service = app.getIndexService();
		return service;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
//		if (listView.isGroupExpanded(groupPosition)) {
//			v.findViewById(R.id.title_row).setVisibility(View.GONE);
//		} else {
//			v.findViewById(R.id.title_row).setVisibility(View.VISIBLE);
//		}
		
		return false;
	}
	
	@Override
	public void onRefresh() {
		super.onRefresh();
		loadAllOddsData();
	}

	@Override
	public void notifyOddsUpdate(Set<Odds> set) {
		Log.d(TAG, "notifyOddsUpdate");
		if (set == null || set.size() == 0) {
			return;
		}
		reloadListDataAndView();
	}
}
