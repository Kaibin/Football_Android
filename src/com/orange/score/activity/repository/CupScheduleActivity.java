package com.orange.score.activity.repository;

import java.util.List;
import java.util.Timer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.activity.match.RealtimeMatchDetailActivity;
import com.orange.score.app.ScoreApplication;
import com.orange.score.model.league.League;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.repository.CupMatchType;
import com.orange.score.model.repository.RepositoryManager;
import com.orange.score.network.ResultCodeType;
import com.orange.score.service.RepositoryService;
import com.orange.score.service.RepositoryServiceCallBack;

public class CupScheduleActivity extends CommonFootballActivity implements RepositoryServiceCallBack{
	
	private static String TAG = CupScheduleActivity.class.getName();
	private static String CUP_REPOSITORY_HTML = "file:///android_asset/cupRepository.html";
	
	Bundle bundle;
	String leagueId;
	League league;
	CharSequence[] seasons;
	CharSequence[] cupMatchTypeString;
	int lastCheckedSeason = 0;
	int lastCheckedType = 0;
	List<CupMatchType> cupMatchTypelist;
	String currentCupMatchTypeId = "";
	String currentSeason = "";
	RepositoryManager repositoryManager;
	LeagueManager leagueManager;
	
	TextView txt_league_season;
	TextView txt_cup_schedule_result;
	Button btn_cup_match_type;
	Button btn_cup_group_points;
	Button btn_cup_schedule_result;
	Button btn_cup_season;
	
	WebView webView;
	String jsUrl = "";
	boolean manual_load = false;
	boolean loadingHtml = true;
	boolean loadingJs = false;
	long startLoadingTime = 0;
	public static final int MAX_LOADING_TIME = 8000;//8秒
	Handler errorHandler = new Handler();

	Timer hideDialogTimer;
	boolean hasProgress = false;
	Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.repository_cup_schedule);
		
		bundle = this.getIntent().getExtras();
		leagueId = bundle.getString("leagueId");
		repositoryManager = getRepositoryManager();
		leagueManager = getLeagueManager();
		
		txt_league_season = (TextView)findViewById(R.id.txt_cup_season);
		txt_cup_schedule_result = (TextView)findViewById(R.id.txt_cup_schedule_result);
	    btn_cup_match_type = (Button)findViewById(R.id.btn_cup_match_type);
	    btn_cup_group_points = (Button)findViewById(R.id.btn_cup_group_points);
	    btn_cup_schedule_result = (Button)findViewById(R.id.btn_cup_schedule_result);
	    btn_cup_season = (Button)findViewById(R.id.btn_cup_season);
	    txt_cup_schedule_result.setVisibility(View.GONE);
	    btn_cup_group_points.setVisibility(View.GONE);
	    btn_cup_schedule_result.setVisibility(View.GONE);
	    
	  //timer to hide progress dialog
		hideDialogTimer = new Timer();
		
	    webView = (WebView)findViewById(R.id.cup_webview);
	    webView.getSettings().setJavaScriptEnabled(true);
	    webView.getSettings().setBuiltInZoomControls(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setVerticalScrollbarOverlay(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        showDefaultWebview();
        
//        showProgressDialog();
        if (leagueId != null && leagueId.length() > 0) {
        	 league = leagueManager.findLeagueById(leagueId);
     	    if (league == null) {
     			return;
     		}
		}
	    List<String> seasonList = league.getSeasonList();
	    seasons = new CharSequence[seasonList.size()];
	    for(int i=0;i<seasonList.size();i++){
	    	seasons[i] = seasonList.get(i);
	    }
	    currentSeason = (String) seasons[0];
	    updateTitle();
	    getRepositoryService().loadGroupInfo(CupScheduleActivity.this, CupScheduleActivity.this, leagueId, currentSeason);
	  
	    btn_cup_season.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		        Dialog dialog = new AlertDialog.Builder(CupScheduleActivity.this)
		        .setTitle(R.string.repository_cup_season_prompt)
		        .setSingleChoiceItems(seasons, lastCheckedSeason, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	Log.d(TAG, "click " + whichButton);
		            	dialog.dismiss();
		            	
		            	lastCheckedSeason = whichButton;
		            	currentSeason = (String) seasons[whichButton];
		            	Log.d(TAG, "click " + currentSeason);
		            	updateTitle();
		            	manual_load = true;
		            	getRepositoryService().loadGroupInfo(CupScheduleActivity.this, CupScheduleActivity.this, leagueId, currentSeason);
		            }

		        })
		        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	Log.d(TAG, "click cancel");
		            }
		        })
		       .create();
		        dialog.show();
			
			}
		});
	    
	    
	    btn_cup_match_type.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		        Dialog dialog = new AlertDialog.Builder(CupScheduleActivity.this)
		        .setTitle(R.string.repository_match_type_prompt)
		        .setSingleChoiceItems(cupMatchTypeString, lastCheckedType, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	Log.d(TAG, "click " + whichButton);
		            	dialog.dismiss();
		            	
		            	lastCheckedType = whichButton;
		            	String selectedType = (String) cupMatchTypeString[whichButton];
		            	Log.d(TAG, "click " + selectedType);
		            	btn_cup_match_type.setText(selectedType);
		            	currentCupMatchTypeId = cupMatchTypelist.get(whichButton).getMatchTypeId();
		            	updateWebview();
		            }

		        })
		        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	Log.d(TAG, "click cancel");
		            }
		        })
		       .create();
		        dialog.show();
			
			}
		});
	    
	    btn_cup_group_points.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btn_cup_group_points.setSelected(true);
				btn_cup_schedule_result.setSelected(false);
				updateGroupPoints();
			}
		});
	    
	    btn_cup_schedule_result.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btn_cup_group_points.setSelected(false);
				btn_cup_schedule_result.setSelected(true);
				
				String title = (String) btn_cup_match_type.getText();
				if (title.indexOf("组赛") != -1) {
					updateGroupResult();
				} else {
					updateScheduleResult();
				}
				btn_cup_group_points.setSelected(false);
				btn_cup_schedule_result.setSelected(true);
			}
		});
	}
	
//	@Override
//	public void onBackPressed() {
//		Intent intent = new Intent(getApplicationContext(), RepositorySelectLeagueActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(intent);
//	}
	
	@Override
	protected void onResume() {
		showProgressDialog();
		webView.loadUrl(CUP_REPOSITORY_HTML);
		manual_load = false;
		loadingHtml = true;
		super.onResume();
	}

	@Override
    protected void onPause() {
    	super.onPause();
    	CupScheduleActivity.this.hideDialog();
		hasProgress = false;
		loadingJs = false;
    	stopHideDialogTimer();
    }
	
    private void stopHideDialogTimer(){
    	if (hideDialogTimer != null){
    		hideDialogTimer.cancel();
    		hideDialogTimer = null;
    	}    	
    }
    
    private void startHideDialogTimer() {
    	hideDialogTimer = new Timer();
		hideDialogTimer.schedule(new HideProgressTask(), 1000, 1000);
	}

	@Override
	public void loadGroupInfoFinish(ResultCodeType success) {
		updateTitle();
		updateCupMatchType();
		if (manual_load) {
			updateWebview();
		}
	}
	
	private void updateTitle() {
    	txt_league_season.setText(league.getShortName() + "  " + currentSeason + "赛季");
	}
	
	private void updateCupMatchType() {
		cupMatchTypelist = repositoryManager.getCupMatchTypeList();
		for (CupMatchType matchType : cupMatchTypelist) {
			if (matchType.getIsCurrentType().equalsIgnoreCase("TRUE")) {
				btn_cup_match_type.setText(matchType.getMatchTypeName());
				this.currentCupMatchTypeId = matchType.getMatchTypeId();
			}
		}
		
		cupMatchTypeString = new CharSequence[cupMatchTypelist.size()];
	    for (int i = 0; i < cupMatchTypeString.length; i++) {
			cupMatchTypeString[i] = cupMatchTypelist.get(i).getMatchTypeName();
		}
	}
	
	private void updateWebview() {
		String title = (String) btn_cup_match_type.getText();
		if (title.indexOf("组赛") != -1) {
			btn_cup_group_points.setVisibility(View.VISIBLE);
			btn_cup_schedule_result.setVisibility(View.VISIBLE);
			txt_cup_schedule_result.setVisibility(View.GONE);
			btn_cup_group_points.setSelected(true);
			updateGroupPoints();
		} else {
			btn_cup_group_points.setVisibility(View.GONE);
			btn_cup_schedule_result.setVisibility(View.GONE);
			txt_cup_schedule_result.setVisibility(View.VISIBLE);
			updateScheduleResult();
		}
	}
	
	private void updateGroupPoints(){
		loadingJs = true;
		jsUrl = getJsUrl("displayCupGroupPoints", "true", "'"+league.getLeagueId() +"'", "'"+currentSeason +"'", "'"+currentCupMatchTypeId +"'", "0");
		if (!loadingHtml) {
			showProgressDialog();
		}
		webView.loadUrl(jsUrl);
	}
	
	private void updateGroupResult(){
		loadingJs = true;
		jsUrl = getJsUrl("displayCupGroupResult", "true", "'"+league.getLeagueId() +"'", "'"+currentSeason +"'", "'"+currentCupMatchTypeId +"'", "0");
		if (!loadingHtml) {
			showProgressDialog();
		}
		webView.loadUrl(jsUrl);
	}
	
	private void updateScheduleResult(){
		loadingJs = true;
		jsUrl = getJsUrl("displayCupScheduleResult", "true", "'"+league.getLeagueId() +"'", "'"+currentSeason +"'", "'"+currentCupMatchTypeId +"'", "0");
		if (!loadingHtml) {
			showProgressDialog();
		}
		webView.loadUrl(jsUrl);
	}
	
    private String getJsUrl(String function, String...args) {
      StringBuilder sb = new StringBuilder();
      sb.append("javascript:");
      sb.append(function);
      sb.append("(");
      for (int i=0;i<args.length;i++) {
          sb.append(args[i]);
          if(i != args.length - 1) {
              sb.append(",");
          }
      }
      sb.append(")");
      
      Log.i(TAG, "loadUrl: " + sb.toString());
      return sb.toString();
  }
    
    private void showDefaultWebview() {
    	
    	webView.addJavascriptInterface(new Object(){
    	//invoked in cupRepository.js like this : window.cupRepository.showWebview(); 
       	 public void showWebview() {   
                handler.post(new Runnable() {   
                    public void run() {
                    	updateWebview();
                    	loadingHtml = false;
                    }   
                });   
            }   
       }, "cupRepository");
	}
    
    @Override
	public void loadAllRepositoryFinish(ResultCodeType success) {
	}
	
	@Override
	public void loadRoundInfoFinish(ResultCodeType success) {
	}
	
	@Override
	public void onRefresh() {
		btn_cup_group_points.setSelected(false);
		btn_cup_schedule_result.setSelected(false);
		loadingHtml = true;
		showProgressDialog();
		webView.loadUrl(CUP_REPOSITORY_HTML);
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
	
	private LeagueManager getLeagueManager() {
		RepositoryService service = getRepositoryService();
		return service.getLeagueManager();
	}
	
	private void showProgressDialog() {
		startLoadingTime = System.currentTimeMillis();
		CupScheduleActivity.this.showProgressDialog("", "加载数据中...");
		startHideDialogTimer();
		hasProgress = true;
	}
	
	private class HideProgressTask extends java.util.TimerTask{
        @Override
        public void run() {
        	if (webView.getProgress() == 100 && hasProgress && (loadingHtml || loadingJs)) {
        		CupScheduleActivity.this.hideDialog();
        		stopHideDialogTimer();
        		hasProgress = false;
        		loadingJs = false;
			} else {
					long now = System.currentTimeMillis();
					if (now - startLoadingTime > MAX_LOADING_TIME) {
						CupScheduleActivity.this.hideDialog();
						stopHideDialogTimer();
						webView.stopLoading();
						hasProgress = false;
						loadingJs = false;
						Log.w(TAG, "network speed too slow!");
						errorHandler.post(new Runnable() {
							public void run() {
								Toast.makeText(CupScheduleActivity.this, "你的网速太慢啦，加载失败，请刷新重新加载！", Toast.LENGTH_SHORT).show();

							}
						});
					}
			}
        }
    }
}
