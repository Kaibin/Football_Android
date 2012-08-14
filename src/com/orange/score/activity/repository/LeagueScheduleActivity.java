package com.orange.score.activity.repository;

import java.util.List;
import java.util.Timer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.activity.match.RealtimeMatchDetailActivity;
import com.orange.score.app.ScoreApplication;
import com.orange.score.model.league.League;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.repository.RepositoryManager;
import com.orange.score.network.ResultCodeType;
import com.orange.score.service.RepositoryService;
import com.orange.score.service.RepositoryServiceCallBack;

public class LeagueScheduleActivity extends CommonFootballActivity implements RepositoryServiceCallBack, OnTouchListener, OnGestureListener {

	private static String TAG = LeagueScheduleActivity.class.getName();
	private static String LEAGUE_REPOSITORY_HTML = "file:///android_asset/repository.html";

	private static final int FLING_MIN_DISTANCE = 60;  
    private static final int FLING_MIN_VELOCITY = 0; 
    
    private int currentSelection = 0;
    private static final int POINTS = 0;
    private static final int SCHEDULE = 1;
    private static final int RANGQIU = 2;
    private static final int DAXIAO = 3;
    private static final int SHOOTER = 4;
    
	Bundle bundle;
	String leagueId;
	League league;
	CharSequence[] seasons;
	CharSequence[] rounds;
	String currentSeason = "";
	int currentRound;
	int totalRound;
	int lastCheckedSeason = 0;
	int lastCheckedRound = 1;

	RepositoryManager repositoryManager;
	LeagueManager leagueManager;

	TextView txt_league_season;
	Button btn_league_season;
	Button btn_league_points;
	Button btn_league_schedule;
	Button btn_league_rangqiu;
	Button btn_league_daxiao;
	Button btn_league_shooter;

	RelativeLayout layout_round;
	Button btn_schedule_previous_round;
	Button btn_schedule_current_round;
	Button btn_schedule_next_round;

	WebView webView;
	String jsUrl = "";

	Handler handler = new Handler();
	Timer hideDialogTimer;
	boolean hasProgress = false;
	boolean loadingJs = false;
	long startLoadingTime = 0;
	public static final int MAX_LOADING_TIME = 8000;//8秒
	Handler errorHandler = new Handler();
	
	GestureDetector gestureDetector;  
	
	boolean refreshFlag = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.repository_league_schedule);

		bundle = this.getIntent().getExtras();
		leagueId = bundle.getString("leagueId");
		repositoryManager = getRepositoryManager();
		leagueManager = getLeagueManager();

		txt_league_season = (TextView) findViewById(R.id.txt_league_season);
		btn_league_season = (Button) findViewById(R.id.btn_league_season);
		btn_league_points = (Button) findViewById(R.id.btn_league_points);
		btn_league_schedule = (Button) findViewById(R.id.btn_league_schedule);
		btn_league_rangqiu = (Button) findViewById(R.id.btn_league_rangqiu);
		btn_league_daxiao = (Button) findViewById(R.id.btn_league_daxiao);
		btn_league_shooter = (Button) findViewById(R.id.btn_league_shooter);

		layout_round = (RelativeLayout)findViewById(R.id.repository_league_round);
		layout_round.setVisibility(View.GONE);
		btn_schedule_previous_round = (Button) findViewById(R.id.btn_schedule_previous_round);
		btn_schedule_current_round = (Button) findViewById(R.id.btn_schedule_current_round);
		btn_schedule_next_round = (Button) findViewById(R.id.btn_schedule_next_round);

		//timer to hide progress dialog
		hideDialogTimer = new Timer();
	        
		webView = (WebView) findViewById(R.id.league_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setVerticalScrollBarEnabled(true);
		webView.setVerticalScrollbarOverlay(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		showDefaultWebview();
		
//		showProgressDialog(); 
		
		//add gestureDetector for webview
//		gestureDetector = new GestureDetector(this);  
//		webView.setOnTouchListener(this);  
//		webView.setLongClickable(true); 

		league = leagueManager.findLeagueById(leagueId);

		List<String> seasonList = league.getSeasonList();
		seasons = new CharSequence[seasonList.size()];
		for (int i = 0; i < seasonList.size(); i++) {
			seasons[i] = seasonList.get(i);
		}
		currentSeason = (String) seasons[0];
		updateTitle();
		getRepositoryService().loadRoundInfo(LeagueScheduleActivity.this,LeagueScheduleActivity.this, leagueId, currentSeason);
		
		setSeasonButtonOnClickListener();
		setPointsButtonOnClickListener();
		setScheduleButtonOnClickListener();
		setRangqiuButtonOnClickListener();
		setDaxiaoButtonOnClickListener();
		setShooterButtonOnClickListener();
		setPreviousRoundButtonOnClickListener();
		setCurrentRoundButtonOnClickListener();
		setNextRoundButtonOnClickListener();
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
		webView.loadUrl(LEAGUE_REPOSITORY_HTML);
		super.onResume();
	}
	
	@Override
    protected void onPause() {
    	super.onPause();
    	LeagueScheduleActivity.this.hideDialog();
		hasProgress = false;
		loadingJs = false;
		resetLeagueButtons();
		currentSelection = POINTS;
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

	private void setSeasonButtonOnClickListener() {
		btn_league_season.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(LeagueScheduleActivity.this)
						.setTitle(R.string.repository_cup_season_prompt)
						.setSingleChoiceItems(seasons, lastCheckedSeason,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Log.d(TAG, "click " + whichButton);
										dialog.dismiss();
										
										lastCheckedSeason = whichButton;
										currentSeason = (String) seasons[whichButton];
										loadingJs = true;
										getRepositoryService().loadRoundInfo(LeagueScheduleActivity.this,LeagueScheduleActivity.this, leagueId, currentSeason);
										layout_round.setVisibility(View.GONE);
										updateTitle();
										resetSelection();
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

	private void setPointsButtonOnClickListener() {
		btn_league_points.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentSelection == POINTS && !refreshFlag) {
					return;
				}
				refreshFlag = false;
				loadingJs = true;
				layout_round.setVisibility(View.GONE);
				resetLeagueButtons();
				btn_league_points.setSelected(true);
				currentSelection = POINTS;
				jsUrl = getJsUrl("displayJifen", "true","'" + league.getLeagueId() + "'", "'" + currentSeason + "'", "0");
				showProgressDialog();
				webView.loadUrl(jsUrl);
			}
		});
	}

	private void setScheduleButtonOnClickListener() {
		btn_league_schedule.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (currentSelection == SCHEDULE && !refreshFlag) {
					return;
				}
				refreshFlag = false;
				loadingJs = true;
				layout_round.setVisibility(View.VISIBLE);
				resetLeagueButtons();
				btn_league_schedule.setSelected(true);
				currentSelection = SCHEDULE;
				jsUrl = getJsUrl("displaySchedule", "true", "'"+league.getLeagueId() +"'", "'"+currentSeason +"'", "'"+currentRound +"'","0");
				Log.d(TAG, "show dialog");
				showProgressDialog();
				webView.loadUrl(jsUrl);	
			}
		});
	}

	private void setRangqiuButtonOnClickListener() {
		btn_league_rangqiu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (currentSelection == RANGQIU && !refreshFlag) {
					return;
				}
				refreshFlag = false;
				loadingJs = true;
				layout_round.setVisibility(View.GONE);
				resetLeagueButtons();
				btn_league_rangqiu.setSelected(true);
				currentSelection = RANGQIU;
				jsUrl = getJsUrl("displayRangqiu", "true", "'"+league.getLeagueId() +"'", "'"+currentSeason +"'", "0");
				showProgressDialog();
				webView.loadUrl(jsUrl);				
			}
		});
	}

	private void setDaxiaoButtonOnClickListener() {
		btn_league_daxiao.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (currentSelection == DAXIAO && !refreshFlag) {
					return;
				}
				refreshFlag = false;
				loadingJs = true;
				layout_round.setVisibility(View.GONE);
				resetLeagueButtons();
				btn_league_daxiao.setSelected(true);
				currentSelection = DAXIAO;
				jsUrl = getJsUrl("displayDaxiao", "true", "'"+league.getLeagueId() +"'", "'"+currentSeason +"'", "0");
				showProgressDialog();
				webView.loadUrl(jsUrl);				
			}
		});
	}

	private void setShooterButtonOnClickListener() {
		btn_league_shooter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (currentSelection == SHOOTER && !refreshFlag) {
					return;
				}
				refreshFlag = false;
				loadingJs = true;
				layout_round.setVisibility(View.GONE);
				resetLeagueButtons();
				btn_league_shooter.setSelected(true);
				currentSelection = SHOOTER;
				jsUrl = getJsUrl("displayScorer", "true", "'"+league.getLeagueId() +"'", "'"+currentSeason +"'", "0");
				showProgressDialog();
				webView.loadUrl(jsUrl);				
			}
		});
	}

	private void setPreviousRoundButtonOnClickListener() {
		btn_schedule_previous_round.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!hasPerviousRoundData()) {
					return;
				}
				loadingJs = true;
				currentRound = currentRound - 1;
				lastCheckedRound = currentRound;
				btn_schedule_current_round.setText("第" + currentRound + "轮");
				updateSchedule();
			}
		});
	}

	private void setNextRoundButtonOnClickListener() {
		btn_schedule_next_round.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!hasNextRoundData()) {
					return;
				}
				loadingJs = true;
				currentRound = currentRound + 1;
				lastCheckedRound = currentRound;
				btn_schedule_current_round.setText("第" + currentRound + "轮");
				updateSchedule();
			}
		});
	}
	
	private void setCurrentRoundButtonOnClickListener() {
		btn_schedule_current_round.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(LeagueScheduleActivity.this)
		        .setTitle(R.string.repository_match_type_prompt)
		        .setSingleChoiceItems(rounds, lastCheckedRound, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	Log.d(TAG, "click " + whichButton);
		            	dialog.dismiss();
		            	
		            	lastCheckedRound = whichButton;
		            	String selectedRound = (String) rounds[whichButton];
		            	Log.d(TAG, "click " + selectedRound);
		            	btn_schedule_current_round.setText(selectedRound);
		            	currentRound = whichButton + 1;
		            	
		            	loadingJs = true;
		            	updateSchedule();
		            	
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
	}
	
	private boolean hasPerviousRoundData() {
		if (currentRound == 1) {
			Toast.makeText(this, "往上没有数据了", Toast.LENGTH_SHORT).show();
			return false;
		} 
		return true;
	}
	
	private boolean hasNextRoundData() {
		if (currentRound == totalRound) {
			Toast.makeText(this, "往下没有数据了", Toast.LENGTH_SHORT).show();
			return false;
		} 
		return true;
	}


	private void resetLeagueButtons() {
		btn_league_daxiao.setSelected(false);
		btn_league_points.setSelected(false);
		btn_league_rangqiu.setSelected(false);
		btn_league_schedule.setSelected(false);
		btn_league_shooter.setSelected(false);
	}
	
	@Override
	public void loadRoundInfoFinish(ResultCodeType success) {
		updateCurrentRoundButton();
	}
	
	private void updateCurrentRoundButton(){
		LeagueManager leagueManager = getLeagueManager();
		String[] round = leagueManager.getLeagueRound();
		totalRound = Integer.parseInt(round[0]);
		if (currentSeason.equals(seasons[0])) {
			currentRound = Integer.parseInt(round[1]);
		} else {
			currentRound = 1;
		}
		lastCheckedRound = currentRound - 1;
		btn_schedule_current_round.setText("第" + currentRound + "轮");
		
		rounds = new CharSequence[totalRound];
	    for (int i = 1; i <= totalRound; i++) {
	    	rounds[i-1] = "第" + i + "轮";
		}
	}
	
	private void resetSelection() {
		resetLeagueButtons();
		btn_league_points.setSelected(true);
		currentSelection = 0;
		jsUrl = getJsUrl("displayJifen", "true","'" + league.getLeagueId() + "'", "'" + currentSeason + "'", "0");
		showProgressDialog();
		webView.loadUrl(jsUrl);
		
	}
	
	private void updateSchedule() {
		jsUrl = getJsUrl("displaySchedule", "true", "'"+league.getLeagueId() +"'", "'"+currentSeason +"'", "'"+currentRound +"'","0");
		showProgressDialog();
		webView.loadUrl(jsUrl);				
	}

	private void updateTitle() {
		txt_league_season.setText(league.getShortName() + "  " + currentSeason + "赛季");
	}

	private String getJsUrl(String function, String... args) {
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:");
		sb.append(function);
		sb.append("(");
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
			if (i != args.length - 1) {
				sb.append(",");
			}
		}
		sb.append(")");

		Log.i(TAG, "loadUrl: " + sb.toString());
		return sb.toString();
	}
	
    private void showDefaultWebview() {
    	
    	webView.addJavascriptInterface(new Object(){
    	//invoked in repository.js like this : window.repository.showWebview(); 
       	 public void showWebview() {   
                handler.post(new Runnable() {   
                    public void run() {
                    	btn_league_points.setSelected(true);
                    	jsUrl = getJsUrl("displayJifen", "true","'" + league.getLeagueId() + "'", "'" + currentSeason + "'", "0");
                    	webView.loadUrl(jsUrl);
                    	loadingJs = true;
                    }   
                });   
            }   
       }, "repository");
	}

	private RepositoryService getRepositoryService() {
		ScoreApplication app = (ScoreApplication) getApplication();
		RepositoryService service = app.getRepositoryService();
		return service;
	}

	private RepositoryManager getRepositoryManager() {
		RepositoryService service = getRepositoryService();
		return service.getRepositoryManager();
	}

	private LeagueManager getLeagueManager() {
		RepositoryService service = getRepositoryService();
		return service.getLeagueManager();
	}

	@Override
	public void loadAllRepositoryFinish(ResultCodeType success) {
	}

	@Override
	public void loadGroupInfoFinish(ResultCodeType success) {
	}
	
	@Override
	public void onRefresh() {
//		resetLeagueButtons();
//		currentSelection = POINTS;
//		showProgressDialog();
//		webView.loadUrl(LEAGUE_REPOSITORY_HTML);
		refreshFlag = true;
		switch (currentSelection) {
		case SHOOTER:
			btn_league_shooter.performClick();
			break;
		case DAXIAO:
			btn_league_daxiao.performClick();
			break;
		case RANGQIU:
			btn_league_rangqiu.performClick();
			break;
		case SCHEDULE:
			btn_league_schedule.performClick();
			break;
		case POINTS:
			btn_league_points.performClick();
		default:
			break;
		}
	}
	
	private void showProgressDialog() {
		startLoadingTime = System.currentTimeMillis();
		LeagueScheduleActivity.this.showProgressDialog("", "加载数据中...");
		startHideDialogTimer();
		hasProgress = true;
	}
	
	private class HideProgressTask extends java.util.TimerTask{
        @Override
        public void run() {
        	if (webView.getProgress() == 100 && hasProgress && loadingJs) {
        		LeagueScheduleActivity.this.hideDialog();
        		stopHideDialogTimer();
        		hasProgress = false;
        		loadingJs = false;
			} else {
				long now = System.currentTimeMillis();
				if (now - startLoadingTime > MAX_LOADING_TIME) {
					LeagueScheduleActivity.this.hideDialog();
					stopHideDialogTimer();
					webView.stopLoading();
					hasProgress = false;
					loadingJs = false;
					Log.w(TAG, "network speed too slow!");
					errorHandler.post(new Runnable() {
						public void run() {
							Toast.makeText(LeagueScheduleActivity.this, "你的网速太慢啦，加载失败，请刷新重新加载！", Toast.LENGTH_SHORT).show();

						}
					});
				}
			}
        }
    }

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e1.getX()-e2.getX() > FLING_MIN_DISTANCE  && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
            // Fling left
			Log.d(TAG, "向左手势");
            setLeftFlingGuestureAction();
        } else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE  && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
            // Fling right   
        	Log.d(TAG, "向右手势");
            setRightFlingGuestureAction();
        }   
        return false;   
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d(TAG,"touch");  
        return gestureDetector.onTouchEvent(event); 
	}
	
	private void setRightFlingGuestureAction() {
		switch (currentSelection) {
		case POINTS:
			btn_league_schedule.performClick();
			break;
		case SCHEDULE:
			btn_league_rangqiu.performClick();
			break;
		case RANGQIU:
			btn_league_daxiao.performClick();
			break;
		case DAXIAO:
			btn_league_shooter.performClick();
			break;
		default:
			break;
		}
	}
	
	private void setLeftFlingGuestureAction() {
		switch (currentSelection) {
		case SHOOTER:
			btn_league_daxiao.performClick();
			break;
		case DAXIAO:
			btn_league_rangqiu.performClick();
			break;
		case RANGQIU:
			btn_league_schedule.performClick();
			break;
		case SCHEDULE:
			btn_league_points.performClick();
			break;
		default:
			break;
		}
	}
}
