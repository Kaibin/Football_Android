package com.orange.score.activity.entry;

import java.util.ArrayList;
import java.util.List;

import com.exchange.Public.ExchangeConstants;
import com.exchange.View.ExchangeViewManager;
import com.orange.score.R;
import com.orange.score.activity.more.AboutWebsiteActivity;
import com.orange.score.activity.more.MoreAdapter;
import com.orange.score.activity.more.FeedbackActivity;
import com.orange.score.activity.more.FinalScoreActivity;
import com.orange.score.activity.more.ScorePromptActivity;
import com.orange.score.activity.more.WeeklyScheduleActivity;
import com.orange.score.app.ScoreApplication;
import com.orange.score.constants.LanguageType;
import com.orange.score.model.config.ConfigManager;
import com.orange.score.service.UserService;
import com.orange.score.activity.common.CommonFootballActivity;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


public class MoreActivity extends CommonFootballActivity {

	public static final String TAG = "MoreActivity";
	public static final String CHECKCLIENT_URL = "http://bf.bet007.com/phone/android_ver.txt";
	public static final String UPDATECLIENT_URL = "http://bf.bet007.com/phone/down/Android";

	public static final int FINISH_SCORE = 0;
	public static final int WEEKLY_MATCH = 1;
	public static final int SET_SCORE_PROMPT = 2;
	public static final int SET_LANGUAGE = 3;
	public static final int FEEDBACK = 4;
	public static final int RECOMMEND = 5;
	public static final int ABOUT_WEBSITE = 6;
	public static final int UPDATE = 7;
	public static final int EXCHANGE = 8;
	public static final int EXIT = 9;

	private ListView listView;

	public void onCreate(Bundle savedInstanceState) {
		
    	isTopActivity = true;

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// remove titlebar
		setContentView(R.layout.select_more);
		initListView();
	}
	
    private void initListView() {
    	listView = (ListView) findViewById(R.id.listView2);
    	listView.setSelectionAfterHeaderView();
    	MoreAdapter adapter = new MoreAdapter(this, R.layout.more_list_item, getData());
		listView.setAdapter(adapter);
		listView.setFastScrollEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case FINISH_SCORE:
					finishScore();
					break;
				case WEEKLY_MATCH:
					weeklyMatch();
					break;
				case SET_SCORE_PROMPT:
					setScorePrompt();
					break;
				case SET_LANGUAGE:
					setLanguage();
					break;
				case FEEDBACK:
					feedFack();
					break;
				case RECOMMEND:
					recommend();
					break;
				case ABOUT_WEBSITE:
					aboutWebsite();
					break;
				case UPDATE:
					upadteClient();
					break;
				case EXCHANGE:
					exchange();
					break;
				case EXIT:
					exit();
					break;

				default:
					break;
				}
			}

		});
		
	}

	@Override
    protected void onResume() {
    	super.onResume();
    	listView.setSelectionAfterHeaderView();
    	ScoreApplication.currentActivity = this;
    	
//		setContentView(R.layout.select_more);
//		initListView();
		
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	listView.setSelectionAfterHeaderView();
    	ScoreApplication.currentActivity = null;
    	
    }

	
	private void setLanguage() {
		DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	Log.d(TAG, "click " + whichButton);
            	dialog.dismiss();
            	Log.i(TAG, "whichButton="+whichButton);
            	if (whichButton == 0) {
            		ConfigManager.setLanguage(0);
            		Log.i(TAG, "select LanguageType.MANDARY");
            	} else if (whichButton == 1){
            		ConfigManager.setLanguage(1);
            		Log.i(TAG, "select LanguageType.CANTONESE");
            	} else {
            		ConfigManager.setLanguage(2);
            		Log.i(TAG, "select LanguageType.CROWN");
            	}
            	

            	ScoreApplication.gotoRealtimeMatchTab(true);
            	
//            	Intent intent = new Intent();
//            	intent.setClass(MoreActivity.this, MainActivity.class);
//            	startActivity(intent);
            	
            }
        };
        LanguageType defaltLanguage = ConfigManager.getLanguage();
        Dialog dialog = new AlertDialog.Builder(MoreActivity.this)
//      .setIcon(R.drawable.alert_dialog_icon)
        .setTitle(R.string.setlanguage_filter_type_prompt)
        .setSingleChoiceItems(R.array.setlanguage_filter_types, defaltLanguage.intValue(), cl)
        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	Log.d(TAG, "click cancel");
            }
        })
       .create();;
       dialog.show();
		
	}

	private void feedFack() {
		Intent intent = new Intent();
		intent.setClass(MoreActivity.this, FeedbackActivity.class);
		startActivity(intent);
	}

	private void recommend() {
		DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /* User clicked on a radio button do some stuff */
            	Log.d(TAG, "click " + whichButton);
            	dialog.dismiss();
            	String message = "朋友，我正在用球探彩客网的比分客户端看即时比分、赔率、分析数据，感觉很不错，下载地址是:"
            			.concat(UPDATECLIENT_URL);
            	if (whichButton == 0) {
            		// send message
            		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"));
            		intent.putExtra("sms_body", message);
            		startActivity(intent);
            	} else {
            		// send email
            		Intent emailIntent = new Intent(Intent.ACTION_SEND);
            		emailIntent.setType("plain/text");
            		emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[] {""});
            		emailIntent.putExtra(Intent.EXTRA_SUBJECT,"推荐球探彩客网的比分客户端");
            		emailIntent.putExtra(Intent.EXTRA_TEXT,message);
            		startActivity(Intent.createChooser(emailIntent, message));
            	}
            }
        };
        
		Dialog dialog = new AlertDialog.Builder(MoreActivity.this)
//      .setIcon(R.drawable.alert_dialog_icon)
        .setTitle(R.string.recommend_filter_type_prompt)
        .setSingleChoiceItems(R.array.recommend_filter_types, -1, cl)
 //		.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
//		            public void onClick(DialogInterface dialog, int whichButton) {
//		                /* User clicked Yes so do some stuff */
//		            }
//		        })
        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	Log.d(TAG, "click cancel");
            }
        })
       .create();;
       dialog.show();
		
	}

	private void aboutWebsite() {
		Intent intent = new Intent();
		intent.setClass(MoreActivity.this, AboutWebsiteActivity.class);
		startActivity(intent);
	}

	private List<String> getData() {
		List<String> data = new ArrayList<String>();
		data.add("完场比分");
		data.add("一周赛程");
		data.add("比分提示及推送设置");
		data.add("语言简繁设置");
		data.add("信息反馈");
		data.add("推荐给好友");
		data.add("关于球探网");
		data.add("客户端更新");
		data.add("更多精品应用");
		data.add("退出客户端");
		return data;
	}

	private void upadteClient() {
		UserService service = new UserService();
		service.updateClient(this, CHECKCLIENT_URL, UPDATECLIENT_URL);
	}
	

	private void exchange() {
		// TODO Auto-generated method stub
	    ExchangeViewManager exchangeViewManager = new ExchangeViewManager();
	    exchangeViewManager.addView(this, null, ExchangeConstants.type_list_curtain);
		
	}


	private void exit() {

		AlertDialog.Builder builder = new AlertDialog.Builder(MoreActivity.this);
		builder.setTitle(R.string.confirmExit);

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						exitApp();
						NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
						manager.cancel(R.drawable.icon);
					}
				});

		builder.setNegativeButton(R.string.cancl,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

					}

				});
		builder.create().show();
	}
	
	
	
	protected void weeklyMatch() {
		Intent intent = new Intent();
		intent.setClass(MoreActivity.this, WeeklyScheduleActivity.class);
		startActivity(intent);
	}

	protected void finishScore() {
		Intent intent = new Intent();
		intent.setClass(MoreActivity.this, FinalScoreActivity.class);
		startActivity(intent);
	}

	protected void setScorePrompt() {
		Intent intent = new Intent();
		intent.setClass(MoreActivity.this, ScorePromptActivity.class);
		startActivity(intent);
	}


}
