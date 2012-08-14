package com.orange.score.activity.match;

import java.util.Timer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.common.util.StringUtil;
import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.android.service.FollowMatchService;
import com.orange.score.app.ScoreApplication;
import com.orange.score.model.match.FilterMatchStatusType;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchManager;
import com.orange.score.network.ImageNetworkRequest;
import com.orange.score.network.ResultCodeType;
import com.orange.score.service.MatchService;
import com.orange.score.service.MatchServiceCallBack;
import com.orange.score.utils.DataUtils;

public class RealtimeMatchDetailActivity extends CommonFootballActivity implements MatchServiceCallBack, OnTouchListener, OnGestureListener {
	private final String TAG = "RealtimeMatchDetail";
	private static final String MATCH_DETAIL_HTML = "file:///android_asset/match_detail.html";
	public final String IMAGE_URL_PREFIX = "http://info.bet007.com/image/team/";
	
	private static final int FLING_MIN_DISTANCE = 60;  
    private static final int FLING_MIN_VELOCITY = 0; 
    
    public static boolean EXIT_APPLICATION = false;
    
    private int currentSelection = 0;
    private static final int EVENT = 0;
    private static final int LINEUP = 1;
    private static final int ANALYSIS = 2;
    private static final int YAPEI = 3;
    private static final int OUPEI = 4;
    private static final int DAXIAO = 5;

	private WebView webView;
	private String JavaScriptUrl = "";
	private Bundle bundle;
	
	ImageView homeImageView;
	ImageView awayImageView;
	TextView homeNameView;
	TextView awayeNameView;
	TextView homeLeagueView;
	TextView awayLeagueView;
	ImageView vsImageView;
	TextView scoreView;	
	TextView dateView;
	TextView statusView;
	
	private String matchId;
	private String leagueName;
	private String homeTeamName;
	private String awayTeamName;
	private String homeTeamScore;
	private String awayTeamScore;
	private String statusString;
	private String date;
	private String homeTeamRank;
	private String awayTeamRank;
	private String homeImageUrl;
	private String awayImageUrl;
	private boolean isStarted;

	private Button btnEvent = null;
	private Button btnLineup = null;
	private Button btnAnalysis = null;
	private Button btnYapei = null;
	private Button btnOupei = null;
	private Button btnOverunder = null;
	
	private Button btnRefresh = null;
	
	Timer hideDialogTimer;
	Handler mHandler = new Handler();
	Handler errorHandler = new Handler();
	boolean hasProgress = false;
	boolean loadingJs = false;
	boolean loadingHtml = true;
	long startLoadingTime = 0;
	public static final int MAX_LOADING_TIME = 8000;//8秒钟
	
	MatchManager matchManager;
	MatchService matchService;
	Match match;
	
	GestureDetector gestureDetector; 

	private void initView() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.realtime_match_detail);
		
		btnRefresh = (Button)this.findViewById(R.id.refresh);
		
		homeImageView = (ImageView) findViewById(R.id.homeImage);
		awayImageView = (ImageView) findViewById(R.id.awayImage);
		homeNameView = (TextView) findViewById(R.id.homeName);
		awayeNameView = (TextView) findViewById(R.id.awayName);
	     homeLeagueView = (TextView) findViewById(R.id.homeLeague);
		awayLeagueView = (TextView) findViewById(R.id.awayLeague);
		vsImageView = (ImageView) findViewById(R.id.vsImage);
		scoreView = (TextView) findViewById(R.id.score);
		dateView = (TextView) findViewById(R.id.date);
		statusView = (TextView) findViewById(R.id.status);


		btnEvent = (Button) this.findViewById(R.id.btnEvent);
		btnLineup = (Button) this.findViewById(R.id.btnLineup);
		btnAnalysis = (Button) this.findViewById(R.id.btnAnalysis);
		btnYapei = (Button) this.findViewById(R.id.btnYapei);
		btnOupei = (Button) this.findViewById(R.id.btnOupei);
		btnOverunder = (Button) this.findViewById(R.id.btnOverunder);

		webView = (WebView) findViewById(R.id.matchView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setVerticalScrollBarEnabled(true);
		webView.setVerticalScrollbarOverlay(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		
		//add gestureDetector for webview
//		gestureDetector = new GestureDetector(this);  
//		webView.setOnTouchListener(this);  
//		webView.setLongClickable(true); 
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//timer to hide progress dialog
		hideDialogTimer = new Timer();
		
		matchService = getMatchService();
		matchManager = getMatchManager();
		
		initView();
		
		setRefreshButtonListener();
		setDetailButtonsListener();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (RealtimeMatchDetailActivity.EXIT_APPLICATION) {
			this.finish();
			return;
		}
		loadingHtml = true;
		resetButtons();
		loadMatchDetaiWebView();
		loadMatchDetail();
	}
	
	@Override
    protected void onPause() {
    	super.onPause();
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
	protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    	setIntent(intent);
	}
	
//	@Override
//	public void onBackPressed() {
//		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(intent);
//	}
	
//	private void setWebViewProgressDialog() {
//		webView.setWebViewClient(new WebViewClient() {
//			@Override
//			public void onPageFinished(WebView webview, String url) {
//
//				showDefaultMatchDetail();
//
//				RealtimeMatchDetailActivity.this.hideDialog();
//			}
//
//			@Override
//			public void onPageStarted(WebView webview, String url,
//					Bitmap favicon) {
//				RealtimeMatchDetailActivity.this
//						.showProgressDialog("", "加载数据中");
//			}
//		});
//	}

	private void showDefaultMatchDetail() {

		webView.addJavascriptInterface(new Object() {
			// invoked in match_detail.js like this :
			// window.matchDetail.clickDetailButton();
			public void clickDetailButton() {
				mHandler.post(new Runnable() {
					public void run() {
						if (!isStarted) {
							btnAnalysis.performClick();
						} else {
							btnEvent.performClick();
						}
						loadingHtml = false;

					}
				});
			}
		}, "matchDetail");
	}

	private void resetButtons() {
		btnAnalysis.setSelected(false);
		btnEvent.setSelected(false);
		btnLineup.setSelected(false);
		btnOupei.setSelected(false);
		btnOverunder.setSelected(false);
		btnYapei.setSelected(false);
	}

	private MatchService getMatchService() {
		ScoreApplication app = (ScoreApplication) getApplication();
		MatchService service = app.getRealtimeMatchService();
		return service;
	}

	private MatchManager getMatchManager() {
		MatchService service = getMatchService();
		return service.getMatchManager();
	}

	private void loadMatchDetail() {

		bundle = this.getIntent().getExtras();
		matchId = bundle.getString("matchId");

		if (StringUtil.isEmpty(matchId)) {
			Log.e(TAG, "Get matchId from RealtimeMatchActivity is empty.");
			return;
		}

		Log.i(TAG, "Load Realtime MatchDetail of  matchId: " + matchId);
		
		if (matchManager.getFilterMatchStatus() == FilterMatchStatusType.FOLLOWED){
			match = FollowMatchService.getFollowMatchManager().findMatchById(matchId);
		}
		else{
			match = matchManager.findMatchById(matchId);
		}
		
		if (match == null){
			Log.e(TAG, "MatchId: " + matchId + " not found!");
			this.finish();
			return;
		}
		
		homeTeamName = match.getHomeTeamName();
		int index = homeTeamName.indexOf("(");
		if (index != -1) {
			homeTeamName = homeTeamName.substring(0, index);
		}
		awayTeamName = match.getAwayTeamName();
		homeTeamScore = match.getHomeTeamScore();
		awayTeamScore = match.getAwayTeamScore();
		leagueName = (String) match.getLeagueName();
		date = match.getDate();
		statusString = DataUtils.toMatchStatusString(match.getStatus());
		isStarted = DataUtils.isStarted(match.getStatus());
		
		homeNameView.setText(homeTeamName);
		awayeNameView.setText(awayTeamName);
		scoreView.setText(homeTeamScore + " : " + awayTeamScore);
		dateView.setText(DataUtils.fatmatDate(date));
		statusView.setText(statusString);

		if (!isStarted) {
			vsImageView.setVisibility(View.VISIBLE);
			scoreView.setVisibility(View.INVISIBLE);
//			dateView.setVisibility(View.INVISIBLE);
//			statusView.setVisibility(View.INVISIBLE);

		} else {
			vsImageView.setVisibility(View.INVISIBLE);
			scoreView.setVisibility(View.VISIBLE);
//			dateView.setVisibility(View.VISIBLE);
//			statusView.setVisibility(View.VISIBLE);
		}
		
		matchService.loadMatchDetail(matchId, this);
	}

	@Override
	public void loadAllMatchFinish(ResultCodeType resultCode) {

	}

	@Override
	public void loadMatchDetailFinish(ResultCodeType resultCode) {
		switch (resultCode) {
		case SUCCESS:
			setMatchLogoAndRank();
			break;
		default:
			break;
		}
	}

	private void setMatchLogoAndRank() {
		homeImageUrl = IMAGE_URL_PREFIX + match.getHomeTeamImageUrl();
		awayImageUrl = IMAGE_URL_PREFIX + match.getAwayTeamImageUrl();
		homeTeamRank = match.getHomeTeamRank();
		awayTeamRank = match.getAwayTeamRank();
		
		setTeamImage(homeImageView, homeImageUrl);
		setTeamImage(awayImageView, awayImageUrl);
		
		if (homeTeamRank != null && homeTeamRank.length() > 0) {
			int index = homeTeamRank.indexOf("(");
			if (index != -1) {
				homeTeamRank = homeTeamRank.substring(0, index);
			}
			homeLeagueView.setText("[" + homeTeamRank + "]");
		} else {
			homeLeagueView.setText("");
		}
		
		if (awayTeamRank != null && awayTeamRank.length() > 0) {
			awayLeagueView.setText("[" + awayTeamRank + "]");
		} else {
			awayLeagueView.setText("");
		}
	}

	private String setLeagueAndRank(String league, String rank) {
		String ret = "";
		if (!StringUtil.isEmpty(league)) {
			ret = league;
		}

		if (!StringUtil.isEmpty(rank)) {
			ret += rank;
		}
		return "[" + ret + "]";
	}

	private void setTeamImage(ImageView imageView, String url) {
		if (!StringUtil.isEmpty(url)) {
			imageView.setTag(url);
			new ImageNetworkRequest().execute(imageView);
		}
	}

	private void loadMatchDetaiWebView() {

//		setWebViewProgressDialog();
		showDefaultMatchDetail();
		
		showProgressDialog();
		webView.loadUrl(MATCH_DETAIL_HTML);
		
	}

	private String getJsUrl(String function, String... args) {
		// String ret = "javascript:displayMatchEvent(true, '587978', 0);";
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
	
	private void setRefreshButtonListener() {
		btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (currentSelection) {
				case DAXIAO:
					btnOverunder.performClick();
					break;
				case OUPEI:
					btnOupei.performClick();
					break;
				case YAPEI:
					btnYapei.performClick();
					break;
				case ANALYSIS:
					btnAnalysis.performClick();
					break;
				case LINEUP:
					btnLineup.performClick();
					break;
				case EVENT:
					btnEvent.performClick();
					break;
				default:
					break;
				}
			}
		});
	}

	private void setDetailButtonsListener() {
		btnEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (isValidMatchId(matchId)) {
						loadingJs = true;
						JavaScriptUrl = getJsUrl("displayMatchEvent", "true",matchId, "0");
						if (!loadingHtml) {
							showProgressDialog();
						}
						webView.loadUrl(JavaScriptUrl);
						resetButtons();
						btnEvent.setSelected(true);
						currentSelection = EVENT;
					} else {
						Log.w(TAG, "matchId not valid!");
					}
				} catch (Exception e) {
					Log.w(TAG, "Error ..." + e.getMessage());
				}
			}
		});

		btnLineup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (isValidMatchId(matchId)) {
						loadingJs = true;
						JavaScriptUrl = getJsUrl("displayLineup", "true",matchId, "0");
						showProgressDialog();
						webView.loadUrl(JavaScriptUrl);
						resetButtons();
						btnLineup.setSelected(true);
						currentSelection = LINEUP;
					} else {
						Log.w(TAG, "matchId not valid!");
					}

				} catch (Exception e) {
					Log.w(TAG, "Error ..." + e.getMessage());
				}
			}
		});

		btnAnalysis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (isValidMatchId(matchId)) {
						int index = homeTeamName.indexOf("(");
						if (index != -1) {
							homeTeamName = homeTeamName.substring(0, index);
						}
						loadingJs = true;
						JavaScriptUrl = getJsUrl("displayAnalysis", "true", matchId, "'" + homeTeamName + "'", "'" + awayTeamName + "'", "0");
						if (!loadingHtml) {
							showProgressDialog();
						}
						webView.loadUrl(JavaScriptUrl);
						resetButtons();
						btnAnalysis.setSelected(true);
						currentSelection = ANALYSIS;
					} else {
						Log.w(TAG, "matchId not valid!");
					}

				} catch (Exception e) {
					Log.w(TAG, "Error ..." + e.getMessage());
				}
			}
		});

		btnYapei.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (isValidMatchId(matchId)) {
						loadingJs = true;
						JavaScriptUrl = getJsUrl("displayYapeiDetail", "true", matchId, "0");
						showProgressDialog();
						webView.loadUrl(JavaScriptUrl);
						resetButtons();
						btnYapei.setSelected(true);
						currentSelection = YAPEI;
					} else {
						Log.w(TAG, "matchId not valid!");
					}
				} catch (Exception e) {
					Log.w(TAG, "Error ..." + e.getMessage());
				}
			}
		});

		btnOupei.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (isValidMatchId(matchId)) {
						loadingJs = true;
						JavaScriptUrl = getJsUrl("displayOupeiDetail", "true", matchId, "0");
						showProgressDialog();
						webView.loadUrl(JavaScriptUrl);
						resetButtons();
						btnOupei.setSelected(true);
						currentSelection = OUPEI;
					} else {
						Log.w(TAG, "matchId not valid!");
					}
				} catch (Exception e) {
					Log.w(TAG, "Error ..." + e.getMessage());
				}
			}
		});

		btnOverunder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (isValidMatchId(matchId)) {
						loadingJs = true;
						JavaScriptUrl = getJsUrl("displayOverunder", "true", matchId, "0");
						showProgressDialog();
						webView.loadUrl(JavaScriptUrl);
						resetButtons();
						btnOverunder.setSelected(true);
						currentSelection = DAXIAO;
					} else {
						Log.w(TAG, "matchId not valid!");
					}
				} catch (Exception e) {
					Log.w(TAG, "Error ..." + e.getMessage());
				}
			}
		});

	}

	private boolean isValidMatchId(String matchId) {
		if (matchId != null && matchId.length() > 0)
			return true;
		else
			return false;
	}
	
	private void showProgressDialog() {
		startLoadingTime = System.currentTimeMillis();
		RealtimeMatchDetailActivity.this.showProgressDialog("", "加载数据中...");
		startHideDialogTimer();
		hasProgress = true;
	}
	
	private class HideProgressTask extends java.util.TimerTask{
        @Override
        public void run() {
        	if (webView.getProgress() == 100 && hasProgress && loadingJs) {
        		RealtimeMatchDetailActivity.this.hideDialog();
				stopHideDialogTimer();
        		hasProgress = false;
        		loadingJs = false;
			} else {
				long now = System.currentTimeMillis();
				if (now - startLoadingTime > MAX_LOADING_TIME) {
					RealtimeMatchDetailActivity.this.hideDialog();
					stopHideDialogTimer();
					webView.stopLoading();
					hasProgress = false;
					loadingJs = false;
					Log.w(TAG, "network speed too slow!");
					errorHandler.post(new Runnable() {
						public void run() {
							Toast.makeText(RealtimeMatchDetailActivity.this, "你的网速太慢啦，加载失败，请刷新重新加载！", Toast.LENGTH_SHORT).show();

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
            setRigthFlingGuestureAction();
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
	
	private void setRigthFlingGuestureAction() {
		switch (currentSelection) {
		case EVENT:
			btnLineup.performClick();
			break;
		case LINEUP:
			btnAnalysis.performClick();
			break;
		case ANALYSIS:
			btnYapei.performClick();
			break;
		case YAPEI:
			btnOupei.performClick();
			break;
		case OUPEI:
			btnOverunder.performClick();
		default:
			break;
		}
	}
	
	private void setLeftFlingGuestureAction() {
		switch (currentSelection) {
		case DAXIAO:
			btnOupei.performClick();
			break;
		case OUPEI:
			btnYapei.performClick();
			break;
		case YAPEI:
			btnAnalysis.performClick();
			break;
		case ANALYSIS:
			btnLineup.performClick();
			break;
		case LINEUP:
			btnEvent.performClick();
		default:
			break;
		}
	}
	
	@Override
	public void onRefresh() {
		loadingHtml = true;
		loadMatchDetaiWebView();
		loadMatchDetail();
	}

}
