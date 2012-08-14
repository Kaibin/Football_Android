package com.orange.score.activity.league;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.R.integer;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.common.android.activity.PPActivity;
import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.app.ScoreApplication;
import com.orange.score.model.league.League;
import com.orange.score.model.league.LeagueManager;

public class SelectLeagueActivity extends CommonFootballActivity {

	public static final String KEY_LEAGUE_FROM = "league_from";
	public static final String KEY_SELECTED_LEAGUE = "selected_league";
	public static final String SELECTLEAGUE_TAG = "SelectLeagueActivity";
	public static final int ACTION_SELECT_LEAGUE = 20120109;
	private static final int LEAGUE_BUTTON_HEIGHT = 28;
	private static final int COLUMN_MARGIN = 3;
	protected static final String TAG = "SelectLeagueActivity";
	
	private static Set<League> selectedSet = new HashSet<League>();
	private static Set<String> inputSelectedSet = new HashSet<String>();
//	private HashMap<String, League> leagueNameMap = new HashMap<String, League>();
	private List<League> leagueList;
	private TableLayout tableLayout;
	private TextView textView;
	private Button[] buttons;
	private Button allSelect_button;
	private Button noSelect_button;
	private Button topSelect_button;
	private int listSize;
	private int hiddenMatchCnt;
	private int allMatchCnt;
	private int topMatchCnt;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//remove titlebar
		setContentView(R.layout.select_league);
		
		initInputSelectedList();

		tableLayout = (TableLayout) findViewById(R.id.selectLeague_tableLayout);
		tableLayout.setStretchAllColumns(true);
		textView = (TextView) findViewById(R.id.textview_hiddenMatch);

		// set tableLayout and buttons
		listSize = leagueList.size();
		int numRow = 0;
		int numColumn = 4;
		if (listSize % 4 == 0) {
			numRow = listSize / 4;
		} else {
			numRow = listSize / 4 + 1;
		}
		buttons = new Button[4 * numRow];
		for (int i = 0; i < numRow; i++) {
			TableRow tableRow = new TableRow(this);
			TableRow.LayoutParams params = new TableRow.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
			params.width = 1;
			params.setMargins(COLUMN_MARGIN, COLUMN_MARGIN, COLUMN_MARGIN, COLUMN_MARGIN);

			tableLayout.setLayoutParams(params);
			for (int j = 0; j < numColumn; j++) {
				int index = i * 4 + j;
				buttons[index] = new Button(this);
				if (index >= listSize) {
					// 这是为了按钮一样大而设置虚拟按钮，使每一排的按钮都为4个
					buttons[index].setLayoutParams(params);
					tableRow.addView(buttons[index]);
					buttons[index].setVisibility(View.INVISIBLE);
					buttons[index].setLayoutParams(params);
					buttons[index].setHeight(LEAGUE_BUTTON_HEIGHT);
					buttons[index].setBackgroundResource(R.drawable.league_button);
					continue;
				}
				League league = leagueList.get(index);
//				leagueNameMap.put(league.name, league);
				int intMatchCount = league.getMatchCount();
				if (league.isTop) {
					topMatchCnt += intMatchCount;
				}
				hiddenMatchCnt += intMatchCount;
				allMatchCnt += intMatchCount;
				String matchCount = String.valueOf(intMatchCount);
				String leagueMessage = league.getLeagueName();
				buttons[index].setText(leagueMessage);
				buttons[index].setLayoutParams(params);
				buttons[index].setSingleLine(true);
				buttons[index].setTextSize(13);
				buttons[index].setBackgroundResource(R.drawable.league_button);
				buttons[index].setHeight(LEAGUE_BUTTON_HEIGHT);
				buttons[index].setTag(league);
					
				// get select leagues
				if (inputSelectedSet.contains(league.leagueId)) {
					hiddenMatchCnt -= league.getMatchCount();
					buttons[index].setSelected(true);
				}
				tableRow.addView(buttons[index]);
			}
			tableLayout.addView(tableRow);
		}
		setHiddenTextView(hiddenMatchCnt);
		updateButtonTextColor();

		
		/*OnLongClickListener onLongClickListener = new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
//				Button button = (Button) view;
//				if (button.isSelected()) {
//					button.setTextColor(getResources().getColor(R.color.select_league_button_color));
//				} else {
//					button.setTextColor(Color.BLACK);
//				}
				updateButtonTextColor();
				return false;
			}
		};*/
		// add click listenetr for Button Array
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				Button button = (Button) view;
//				if (button.isSelected()) {
//					button.setTextColor(getResources().getColor(R.color.select_league_button_color));
//				} else {
//					button.setTextColor(Color.BLACK);
//				}
				updateButtonTextColor();
				League league = (League)button.getTag();
				if (league == null){
					Log.e(TAG, "League of button not found!");
					return;
				}
				int matchCount = league.matchCount;
				if (button.isSelected()) {
					selectedSet.remove(league);
					hiddenMatchCnt -= matchCount;
				} else {
					selectedSet.add(league);
					hiddenMatchCnt += matchCount;
				}		
				button.setSelected(!button.isSelected());
				setHiddenTextView(hiddenMatchCnt);
				updateButtonTextColor();
			}	
			
		};
		for (int i = 0; i < listSize; i++) {
			buttons[i].setOnClickListener(clickListener);
//			buttons[i].setOnLongClickListener(onLongClickListener);
		}
				
		// click OK button and retrun selected leagues
		Button ok_button = (Button)findViewById(R.id.selectLeague_ok);
		ok_button.setBackgroundResource(R.drawable.league_bottom_button);
		OnClickListener okClickListener = new OnClickListener() {
			public void onClick(View v) {
				
				if (selectedSet == null || selectedSet.size() == 0){
					Toast.makeText(getApplicationContext(), "至少要选择一个赛事", Toast.LENGTH_LONG).show();
					return;
				}
				
				Intent intent = new Intent();
				ArrayList<String> selectedList = getSelectedLeagueIdArray();
				Log.i(SELECTLEAGUE_TAG, "selectedLeagues="+selectedList.toString());
				Bundle bundle = new Bundle();
				bundle.putStringArrayList(SelectLeagueActivity.KEY_SELECTED_LEAGUE,
						selectedList);
				intent.putExtras(bundle);
				SelectLeagueActivity.this.setResult(ACTION_SELECT_LEAGUE, intent);
				SelectLeagueActivity.this.finish();
			}

		};
		ok_button.setOnClickListener(okClickListener);
		
		// set all select button
		allSelect_button = (Button)findViewById(R.id.selectLeague_allSelect);
		allSelect_button.setBackgroundResource(R.drawable.league_bottom_button);
		OnClickListener allClickListener = new OnClickListener() {
			public void onClick(View view) {
				for(int i=0; i<listSize; i++) {
					buttons[i].setSelected(true);
//					buttons[i].setTextColor(getResources().getColor(R.color.select_league_button_color));
				}
				for (int i=0; i<listSize; i++){
					League league = leagueList.get(i);
					selectedSet.add(league);
				}
				Button button = (Button) view;
				button.setSelected(true);
				noSelect_button.setSelected(false);
				topSelect_button.setSelected(false);
				hiddenMatchCnt = 0;
				setHiddenTextView(hiddenMatchCnt);
				updateButtonTextColor();
			}
		};
		allSelect_button.setOnClickListener(allClickListener);		
		
		// set all no select button
		noSelect_button = (Button)findViewById(R.id.selectLeague_noSelect);
		noSelect_button.setBackgroundResource(R.drawable.league_bottom_button);
		OnClickListener noClickListener = new OnClickListener() {
			public void onClick(View view) {
				for(int i=0; i<listSize; i++){
					buttons[i].setSelected(false);
//					buttons[i].setTextColor(Color.BLACK);
				}
				selectedSet.clear();
				Button button = (Button) view;
				button.setSelected(true);
				allSelect_button.setSelected(false);
				topSelect_button.setSelected(false);
				setHiddenTextView(allMatchCnt);
				hiddenMatchCnt = allMatchCnt;
				updateButtonTextColor();
			}		
		};
		noSelect_button.setOnClickListener(noClickListener);
		
		// set topLeague button
		topSelect_button = (Button)findViewById(R.id.selectLeague_topLeague);
		topSelect_button.setBackgroundResource(R.drawable.league_bottom_button);
		OnClickListener topLeagueClickListener = new OnClickListener() {
			public void onClick(View view) {
				Button button = (Button) view;
				button.setSelected(true);
				noSelect_button.setSelected(false);
				allSelect_button.setSelected(false);
				for (int i=0; i<listSize; i++){
					League league = leagueList.get(i);
					if (league.isTop) {
						buttons[i].setSelected(true);
//						buttons[i].setTextColor(R.color.select_league_button_color));
						selectedSet.add(league);
					} else {
						buttons[i].setSelected(false);
//						buttons[i].setTextColor(Color.BLACK);
						selectedSet.remove(league);
					}		
				}
				setHiddenTextView(allMatchCnt-topMatchCnt);
				updateButtonTextColor();
			}
		};
		topSelect_button.setOnClickListener(topLeagueClickListener);	
		
	}
	
	private void initInputSelectedList() {
		// get data from RealTimeMatchActivity
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		int from = bundle.getInt(SelectLeagueActivity.KEY_LEAGUE_FROM);

		ScoreApplication application = (ScoreApplication) getApplication();
		LeagueManager leagueManager = application.getLeagueManager(from);

		// calculate total match count
		leagueList = leagueManager.findAllLeagues();
		totalMatchCount = calculateAllMatchCount(leagueList);
		Log.d(TAG, "total league match count = " + totalMatchCount);

		// create default set
		ArrayList<String> inputList = bundle
				.getStringArrayList(SelectLeagueActivity.KEY_SELECTED_LEAGUE);
		inputSelectedSet.clear();
		if (inputList != null) {
			inputSelectedSet.addAll(inputList);	
			
			// init selected set
			int count = inputList.size();
			selectedSet.clear();
			for (int i=0; i<count; i++){
				League league = leagueManager.findLeagueById(inputList.get(i));
				selectedSet.add(league);
			}
		}
		
		

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (buttons != null && buttons.length > 0) {
    		for (int i = 0; i < buttons.length; i++) {
//    			if (buttons[i] != null) {
//    				buttons[i].setTextColor(Color.BLACK);
//    			}
    			updateButtonTextColor();
    		}
		}
	}

	int totalMatchCount;

	private int calculateAllMatchCount(List<League> leagueList){
		int count = 0;
		int size = leagueList.size();
		for (int i=0; i<size; i++){
			count += leagueList.get(i).matchCount;
		}
		
		return count;
	}	
	
	private int getCurrentHiddenMatchCount(){
		Object[] leagueList = selectedSet.toArray();
		if (leagueList == null)
			return 0;

		int count = 0;
		for (int i=0; i<leagueList.length; i++){
			if (leagueList[i] == null) {
				continue;
			}
			count += ((League)leagueList[i]).matchCount;
		}
		
		return totalMatchCount - count;
	}
	
	private void setHiddenTextView(int hiddenMatchCnt) {
		int count = getCurrentHiddenMatchCount();
		textView.setText(String.valueOf(count));
	}

	private ArrayList<String> getSelectedLeagueIdArray() {
		ArrayList<String> retList = new ArrayList<String>();
		Object[] leagueList = selectedSet.toArray();
		for (int i=0; i<leagueList.length; i++){
			retList.add(((League)leagueList[i]).leagueId);
		}		
		return retList;
	}
	
	private void updateButtonTextColor() {
		for (Button button : buttons) {
			if(button.isSelected()) {
				button.setTextColor(getResources().getColor(R.color.select_league_button_color));
			} else {
				button.setTextColor(Color.BLACK);
			}
		}	
				
	}

}
