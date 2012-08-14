package com.orange.score.activity.repository;

import java.util.List;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.common.android.activity.PPActivity;
import com.orange.score.R;
import com.orange.score.app.ScoreApplication;
import com.orange.score.constants.LeagueType;
import com.orange.score.model.league.League;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.repository.RepositoryManager;
import com.orange.score.service.RepositoryService;

public class RepositorySelectLeagueActivity extends CommonFootballActivity {
	private static String TAG = RepositorySelectLeagueActivity.class.getName();
	private static int DEFAULT_COLUMN = 1;
	private TableLayout tableLayout;
	private Button[] buttons;
	TextView titleTextView;
	TextView noResultTextView;

	Bundle bundle;
	String countryId;
	String countryName;
	String keyword;
	
	LeagueManager leagueManager;
	RepositoryManager repositoryManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.repository_select_league);

		tableLayout = (TableLayout) findViewById(R.id.selectLeague_tableLayout);
		tableLayout.setStretchAllColumns(true);
		titleTextView = (TextView)findViewById(R.id.repository_select_league_title);
		noResultTextView = (TextView)findViewById(R.id.repository_search_no_result);

		bundle = this.getIntent().getExtras();
		countryId = bundle.getString("countryId");
		countryName = bundle.getString("countryName");
		keyword = bundle.getString("key");
		leagueManager = getLeagueManager();
		repositoryManager= getRepositoryManager();
		
		showLeagueList();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (buttons != null && buttons.length > 0) {
			for (int i = 0; i < buttons.length; i++) {
				if (buttons[i] != null) {
					buttons[i].setTextColor(Color.BLACK);
				}
			}
		}
	}
	
	private void showLeagueList() {

		if (keyword == null) {
			//from country
			titleTextView.setText(countryName + "赛事资料库");
			List<League> leagueList = leagueManager.filterLeagueByCountryId(countryId);
			if (leagueList != null && leagueList.size() > 0){
				buildTableLayout(leagueList, DEFAULT_COLUMN);
				noResultTextView.setVisibility(View.GONE);
				return;
			}
		}
		else{
			//from search
			titleTextView.setText("搜索结果");
			List<League> leagueList = leagueManager.findAllLeagues();
			List<League> filterLeagueList = repositoryManager.getLeagueListByKey(leagueList, keyword);
			if (filterLeagueList != null && filterLeagueList.size() > 0) {
				buildTableLayout(filterLeagueList, DEFAULT_COLUMN);
				noResultTextView.setVisibility(View.GONE);
				return;
			} 
		}
		
		//search return no result
		noResultTextView.setVisibility(View.VISIBLE);
		tableLayout.setVisibility(View.GONE);
	}

	private void buildTableLayout(List<League> filterLeagueList, int numColumn) {
		if (filterLeagueList == null){
			return;
		}
		
		// set tableLayout and buttons
		int listSize = filterLeagueList.size();
		int numRow = listSize;
		buttons = new Button[numRow];
		for (int i = 0; i < numRow; i++) {
			TableRow tableRow = new TableRow(this);
			TableRow.LayoutParams params = new TableRow.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
			params.width = 1;
			params.setMargins(5, 5, 5, 5);
			tableLayout.setLayoutParams(params);
			buttons[i] = new Button(this);
			if (i >= listSize) {
				buttons[i].setLayoutParams(params);
				tableRow.addView(buttons[i]);
				buttons[i].setVisibility(View.INVISIBLE);
				continue;
			}
			League league = filterLeagueList.get(i);
			buttons[i].setGravity(Gravity.CENTER_VERTICAL);
			buttons[i].setText(league.getLeagueName());
			buttons[i].setLayoutParams(params);
			buttons[i].setSingleLine();
			buttons[i].setTextSize(14);
			buttons[i].setBackgroundResource(R.drawable.repository_league_select_button);
			buttons[i].setHeight(33);
			tableRow.addView(buttons[i]);
			tableLayout.addView(tableRow);
			
			setLeagueSelectedButtonClickListener(buttons[i], league);
		}
	}
	
	private void setLeagueSelectedButtonClickListener(final Button button, final League league){
		
		button.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				button.setTextColor(Color.WHITE);
				return false;
			}
		});
		
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				button.setTextColor(Color.WHITE);
				Intent intent = new Intent();
				if (league.getType() == LeagueType.LEAGUE.intValue()) {
					intent.setClass(RepositorySelectLeagueActivity.this, LeagueScheduleActivity.class);
				} else {
					intent.setClass(RepositorySelectLeagueActivity.this, CupScheduleActivity.class);
				}
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				Bundle bundle = new Bundle();
				bundle.putString("leagueId", league.getLeagueId());
                intent.putExtras(bundle);
                startActivity(intent);
			}
		});
	}

	private RepositoryService getRepositoryService() {
		ScoreApplication app = (ScoreApplication) getApplication();
		RepositoryService service = app.getRepositoryService();
		return service;
	}

	private LeagueManager getLeagueManager() {
		RepositoryService service = getRepositoryService();
		return service.getLeagueManager();
	}
	
	private RepositoryManager getRepositoryManager(){
		RepositoryService service = getRepositoryService();
		return service.getRepositoryManager();
	}
	
	@Override
	public void onRefresh() {
		
	}
}
