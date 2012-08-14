package com.orange.score.activity.entry;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.common.android.activity.PPActivity;
import com.orange.score.R;
import com.orange.score.activity.repository.RepositorySelectLeagueActivity;
import com.orange.score.app.ScoreApplication;
import com.orange.score.constants.FilterContinentType;
import com.orange.score.model.repository.Country;
import com.orange.score.model.repository.RepositoryManager;
import com.orange.score.network.ResultCodeType;
import com.orange.score.service.FootballMenuRefreshAction;
import com.orange.score.service.RepositoryService;
import com.orange.score.service.RepositoryServiceCallBack;

public class RepositoryActivity extends CommonFootballActivity implements RepositoryServiceCallBack{
	private static String TAG = RepositoryActivity.class.getName();
	private static int DEFAULT_COLUMN = 4;
	private TableLayout tableLayout;
	private Button[] buttons;
	RepositoryManager repositoryManager;
	
	Button searchButton;
	EditText searchTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
    	isTopActivity = true;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repository);
		
		// init button click listeners
		setContinentFilterButtonClickListener(R.id.button_internation, FilterContinentType.INTERNATION);
		setContinentFilterButtonClickListener(R.id.button_europe, FilterContinentType.EUROPE);
		setContinentFilterButtonClickListener(R.id.button_america, FilterContinentType.AMERICA);
		setContinentFilterButtonClickListener(R.id.button_asia, FilterContinentType.ASIA);
		setContinentFilterButtonClickListener(R.id.button_africa, FilterContinentType.AFRICA);
		setContinentFilterButtonClickListener(R.id.button_oceania, FilterContinentType.OCEANIA);
		
		searchButton = (Button)findViewById(R.id.btn_search);
		searchTxt = (EditText)findViewById(R.id.txt_search_key);
		setSearchButtonClickListener();
		
		//init tableLayout
		tableLayout = (TableLayout) findViewById(R.id.selectCountry_tableLayout);
		tableLayout.setStretchAllColumns(true);
		
		
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	removeTableLayout();
    	getRepositoryService().loadRepository(this, this);
		repositoryManager = getRepositoryManager();
		updateContinentFilterButton();

    	ScoreApplication.currentActivity = this;
    	if (buttons != null && buttons.length > 0) {
    		for (int i = 0; i < buttons.length; i++) {
    			if (buttons[i] != null) {
    				buttons[i].setTextColor(getResources().getColor(R.color.odds_item_chupan_color));
    			}
    		}
		}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	ScoreApplication.currentActivity = null;
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
//    	exitApp();
    }
	
	//the callback function
	@Override
	public void loadAllRepositoryFinish(ResultCodeType success) {
		buildTableLayout(repositoryManager.filterCountry(), DEFAULT_COLUMN);
	}
	
	//when build new tableLayout, you need delete the old one
	private void removeTableLayout() {
		tableLayout.removeAllViewsInLayout();
	}
	
	private void buildTableLayout(List<Country> filterCountryList, int numColumn) {
		if (filterCountryList == null)
			return;
		
		// set tableLayout and buttons
		int listSize = filterCountryList.size();
		int numRow = 0;
		if (listSize % DEFAULT_COLUMN == 0) {
			numRow = listSize / DEFAULT_COLUMN;
		} else {
			numRow = listSize / DEFAULT_COLUMN + 1;
		}
		buttons = new Button[DEFAULT_COLUMN * numRow];
		for (int i = 0; i < numRow; i++) {
			TableRow tableRow = new TableRow(this);
			TableRow.LayoutParams params = new TableRow.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
			params.width = 1;
			params.setMargins(5, 5, 5, 5);			

			tableLayout.setLayoutParams(params);
			for (int j = 0; j < numColumn; j++) {
				int index = i * DEFAULT_COLUMN + j;
				buttons[index] = new Button(this);
				if (index >= listSize) {
					buttons[index].setLayoutParams(params);
					tableRow.addView(buttons[index]);
					buttons[index].setVisibility(View.INVISIBLE);
					continue;
				}
				Country country = filterCountryList.get(index);
				buttons[index].setText(country.getCountryName());
				buttons[index].setTextColor(getResources().getColor(R.color.odds_item_chupan_color));
				buttons[index].setLayoutParams(params);
				buttons[index].setSingleLine();
				buttons[index].setTextSize(12);
				buttons[index].setBackgroundResource(R.drawable.league_button);
				buttons[index].setHeight(33);
				tableRow.addView(buttons[index]);
				setCountrySelectedButtonClickListener(buttons[index], country.getCountryId(), country.getCountryName());
			}
			tableLayout.addView(tableRow);
		}
	}
	
	private void setSearchButtonClickListener() {
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String key = searchTxt.getText().toString();
				Intent intent = new Intent();
				intent.setClass(RepositoryActivity.this, RepositorySelectLeagueActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("key", key);
                intent.putExtras(bundle);
                startActivity(intent);
				
			}
		});
	}
	
	private void setContinentFilterButtonClickListener(int buttonId, final FilterContinentType whichContinent) {
		Button button = (Button)findViewById(buttonId);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RepositoryManager manager = getRepositoryManager();
				manager.setFilterContinent(whichContinent);
				List<Country> filterCountryList = manager.filterCountry();
				updateCountryList(filterCountryList);
				updateContinentFilterButton();
			}
		});
	}
	
	private void setCountrySelectedButtonClickListener(final Button button, final String countryId, final String countryName){
		
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
				intent.setClass(RepositoryActivity.this, RepositorySelectLeagueActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("countryId", countryId);
				bundle.putString("countryName", countryName);
                intent.putExtras(bundle);
                startActivity(intent);
			}
		});
	}
    
	protected void updateCountryList(List<Country> filterCountryList) {
		removeTableLayout();
		buildTableLayout(repositoryManager.filterCountry(), DEFAULT_COLUMN);
	}

    private void updateContinentFilterButton(){
    	int buttonId = R.id.button1;
    	FilterContinentType status = repositoryManager.getFilterContinent();
    	switch (status){
    	case INTERNATION:    		
    		buttonId = R.id.button_internation;
    		break;
    	case EUROPE:
    		buttonId = R.id.button_europe;
    		break;
    	case AMERICA:
    		buttonId = R.id.button_america;
    		break;
    	case ASIA:
    		buttonId = R.id.button_asia;
    		break;
    	case AFRICA:
    		buttonId = R.id.button_africa;
    		break;
    	case OCEANIA:
    		buttonId = R.id.button_oceania;
    		break;    		
    	}
    	
    	int id[] = {R.id.button_internation, R.id.button_europe, R.id.button_america, R.id.button_asia, R.id.button_africa, R.id.button_oceania};
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
    
	private RepositoryService getRepositoryService(){
		ScoreApplication app = (ScoreApplication)getApplication();
		RepositoryService service = app.getRepositoryService();
		return service;
	}
	
	private RepositoryManager getRepositoryManager(){
		RepositoryService service = getRepositoryService();
		return service.getRepositoryManager();
	}

	@Override
	public void loadGroupInfoFinish(ResultCodeType success) {
		
	}

	@Override
	public void loadRoundInfoFinish(ResultCodeType success) {
		
	}

	@Override
	public void onRefresh() {
		removeTableLayout();
		getRepositoryService().loadRepository(this, this);
		
	}
	
}
