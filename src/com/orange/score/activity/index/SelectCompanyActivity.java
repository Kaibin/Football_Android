package com.orange.score.activity.index;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.app.ScoreApplication;
import com.orange.score.model.index.Company;
import com.orange.score.model.index.CompanyManager;
import com.orange.score.model.index.OddsManager;
import com.orange.score.model.index.OddsType;
import com.orange.score.service.IndexService;
import com.orange.score.utils.ToastUtil;

public class SelectCompanyActivity extends CommonFootballActivity{
	public final static String TAG = "CompanySelectActivity";
	public static final int ACTION_SELECT_COMPANY = 20120217;
	public static final String KEY_SELECTED_COMPANY = "selectedCompanyId";
	public static final String KEY_SELECTED_ODDS = "selectedOdds";
	
	private static int DEFAULT_COLUMN = 3;
	private static int DEFAULT_SELECTED_COUNT = 4;
	private OddsManager indexManager;
	private CompanyManager companyManager;
	private TableLayout tableLayout;
	private Button[] buttons;
	
	private Button yapeiButton;
	private Button oupeiButton;
	private Button daxiaoButton;
	private Button okButton;
	private Button cancelButton;
	
	private Set<Company> selectedSet = new HashSet<Company>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.realindex_select_company);
		
		indexManager = getIndexManager();
		companyManager = getCompanyManager();
		
		yapeiButton = (Button) findViewById(R.id.button_yapei);
		oupeiButton = (Button) findViewById(R.id.button_oupei);
		daxiaoButton = (Button) findViewById(R.id.button_daxiao);
		okButton = (Button)findViewById(R.id.button_ok);
		cancelButton = (Button)findViewById(R.id.button_cancel);

		tableLayout = (TableLayout) findViewById(R.id.selectCompany_tableLayout);
		tableLayout.setStretchAllColumns(true);
		
		tableLayout = (TableLayout) findViewById(R.id.selectCompany_tableLayout);
		tableLayout.setStretchAllColumns(true);
		
//		yapeiButton.setSelected(true);
//		buildTableLayout(companyManager.getYapeiCompanyList(), DEFAULT_COLUMN);
//		for (int k = 0; k < DEFAULT_SELECTED_COUNT; k++) {
//			buttons[k].performClick();
//		}
		
		initInputSelectedList();
		
		setOddsFilterButtonClickListener(R.id.button_yapei,OddsType.YAPEI);
		setOddsFilterButtonClickListener(R.id.button_oupei,OddsType.OUPEI);
		setOddsFilterButtonClickListener(R.id.button_daxiao,OddsType.DAXIAO);
		setOkButtonClickListener();
		setCancelButtonClickListener();
	}
	
	private void initInputSelectedList() {
		switch (companyManager.getOddsType()) {
		case YAPEI:
			yapeiButton.setSelected(true);
			buildTableLayout(companyManager.getYapeiCompanyList(), DEFAULT_COLUMN);
			if (companyManager.getYapeiSelectedSet().size() == 0) {
				if (buttons != null && buttons.length > 0) {
					for (int k = 0; k < DEFAULT_SELECTED_COUNT; k++) {
						buttons[k].performClick();
					}
				}
			}
			break;
		case OUPEI:
			oupeiButton.setSelected(true);
			buildTableLayout(companyManager.getOupeiCompanyList(), DEFAULT_COLUMN);
			break;
		case DAXIAO:
			daxiaoButton.setSelected(true);
			buildTableLayout(companyManager.getDaxiaoCompanyList(), DEFAULT_COLUMN);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		ScoreApplication.currentActivity = this;
	}

	@Override
	protected void onPause() {
		super.onPause();
		ScoreApplication.currentActivity = null;
	}
	
	
	private void setCancelButtonClickListener() {
		cancelButton.setBackgroundResource(R.drawable.league_bottom_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SelectCompanyActivity.this.finish();
			}
		});
	}

	private void setOkButtonClickListener() {
		okButton.setBackgroundResource(R.drawable.league_bottom_button);
		OnClickListener okClickListener = new OnClickListener() {
			public void onClick(View v) {
				if (selectedSet == null || selectedSet.size() == 0){
//					Toast.makeText(getApplicationContext(), "至少选择一间赔率公司", Toast.LENGTH_LONG).show();
					ToastUtil.showMessage(getApplicationContext(), "至少选择一间赔率公司");
					return;
				}
				Intent intent = new Intent();
				ArrayList<String> selectedList = getSelectedCompanyIdArray();
				Log.d(TAG, "selectedCompany="+selectedList.toString());
				Bundle bundle = new Bundle();
				bundle.putStringArrayList(KEY_SELECTED_COMPANY, selectedList);
				intent.putExtras(bundle);
				SelectCompanyActivity.this.setResult(ACTION_SELECT_COMPANY, intent);
				SelectCompanyActivity.this.finish();
			}

		};
		okButton.setOnClickListener(okClickListener);		
	}
	
	private ArrayList<String> getSelectedCompanyIdArray() {
		ArrayList<String> retList = new ArrayList<String>();
		Object[] companyList = selectedSet.toArray();
		for (int i=0; i<companyList.length; i++){
			retList.add(((Company)companyList[i]).getCompanyId());
		}		
		return retList;
	}
	

	private void updateOddsFilterButton(int buttonId) {
		int id[] = {R.id.button_yapei, R.id.button_oupei, R.id.button_daxiao};
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

	private void setOddsFilterButtonClickListener(final int buttonId, final OddsType oddsType) {
		Button button = (Button)findViewById(buttonId);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				companyManager.setOddsType(oddsType);
				updateOddsFilterButton(buttonId);
				removeTableLayout();
				selectedSet.clear();
				switch (buttonId) {
				case R.id.button_yapei:
					buildTableLayout(companyManager.getYapeiCompanyList(), DEFAULT_COLUMN);
					break;
				case R.id.button_oupei:
					buildTableLayout(companyManager.getOupeiCompanyList(), DEFAULT_COLUMN);
					break;
				case R.id.button_daxiao:
					buildTableLayout(companyManager.getDaxiaoCompanyList(), DEFAULT_COLUMN);
					break;
				default:
					break;
				}

			}
		});
	}

	private void buildTableLayout(List<Company> companyList, int numColumn) {
		if (companyList == null) {
			return;
		}
		
		// set tableLayout and buttons
		int listSize = companyList.size();
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
			params.setMargins(15, 15, 15, 15);			

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
				Company company = companyList.get(index);
				buttons[index].setText(company.getCompanyName());
				buttons[index].setLayoutParams(params);
				buttons[index].setSingleLine();
				buttons[index].setTextSize(12);
				buttons[index].setBackgroundResource(R.drawable.league_button);
				buttons[index].setHeight(33);
				tableRow.addView(buttons[index]);
				setCompanySelectedButtonClickListener(buttons[index], company);
				switch (companyManager.getOddsType()) {
				case YAPEI:
					if(companyManager.getYapeiSelectedSet().contains(company)) {
						buttons[index].performClick();
					}
					break;
				case OUPEI:
					if(companyManager.getOupeiSelectedSet().contains(company)) {
						buttons[index].performClick();
					}
					break;
				case DAXIAO:
					if(companyManager.getDaxiaoSelectedSet().contains(company)) {
						buttons[index].performClick();
					}
					break;

				default:
					break;
				}
			}
			tableLayout.addView(tableRow);
		}
	}
	
	private void setCompanySelectedButtonClickListener(final Button button, final Company company) {
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (button.isSelected()) {
					selectedSet.remove(company);
					switch (companyManager.getOddsType()) {
					case YAPEI:
						companyManager.getYapeiSelectedSet().remove(company);
						break;
					case OUPEI:
						companyManager.getOupeiSelectedSet().remove(company);
						break;
					case DAXIAO:
						companyManager.getDaxiaoSelectedSet().remove(company);
						break;
					default:
						break;
					}
				} else {
					if (selectedSet != null && selectedSet.size() == DEFAULT_SELECTED_COUNT){
//						Toast.makeText(getApplicationContext(), "最多只能选择四个", Toast.LENGTH_LONG).show();
						ToastUtil.showMessage(getApplicationContext(), "最多只能选择四个");
						return;
					}
					selectedSet.add(company);
					switch (companyManager.getOddsType()) {
					case YAPEI:
						companyManager.getYapeiSelectedSet().add(company);
						break;
					case OUPEI:
						companyManager.getOupeiSelectedSet().add(company);
						break;
					case DAXIAO:
						companyManager.getDaxiaoSelectedSet().add(company);
						break;
					default:
						break;
					}
				}		
				button.setSelected(!button.isSelected());				
				updateButtonTextColor();
			}
		});
	}
	
	private void updateButtonTextColor() {
		for (Button button : buttons) {
			if (button == null) {
				return;
			}
			if(button.isSelected()) {
				button.setTextColor(getResources().getColor(R.color.select_league_button_color));
			} else {
				button.setTextColor(Color.BLACK);
			}
		}	
	}

	private void removeTableLayout() {
		tableLayout.removeAllViewsInLayout();
	}

	private IndexService getIndexService(){
		ScoreApplication app = (ScoreApplication)getApplication();
		IndexService service = app.getIndexService();
		return service;
	}
	
	private OddsManager getIndexManager(){
		IndexService service = getIndexService();
		return service.getOddsManager();
	}
	
	private CompanyManager getCompanyManager(){
		IndexService service = getIndexService();
		return service.getCompanyManager();
	}

}
