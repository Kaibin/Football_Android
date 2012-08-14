package com.orange.score.activity.more;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.orange.score.activity.common.CommonFootballActivity;
import com.orange.score.R;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchManager;
import com.orange.score.network.ResultCodeType;
import com.orange.score.service.WeeklyMatchService;
import com.orange.score.service.WeeklyMatchServiceCallBack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class WeeklyScheduleActivity extends CommonFootballActivity 
	implements WeeklyMatchServiceCallBack{
	
	private Button dateButton;
	private ListView listView = null;
	public static final String TAG = "WeeklyScheduleActivity";
	private String[] dates = new String[7];
	private String selectDateString;
	private Date selectDate;
	private WeeklyMatchService weeklyMatchService;
	private MatchManager matchManager;
	private WeeklyScheduleAdapter adapter;
	private Button beforeDateButton;
	private Button afterDateButton;
	private TextView textView;
	private String[] dayofWeek = {"日","一","二","三","四","五","六"};
	private int pageSize = 20;
	private TimeZone china=TimeZone.getTimeZone("GMT+08:00");
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// remove titlebar
		setContentView(R.layout.more_weeklyschedule);
		textView = (TextView) findViewById(R.id.textVive_date);
		initCalendar();
		initButton();
		initServicesAndManagers();
		initListView();
		loadAllMatch();
	}
	
	private void initButton() {
		dateButton = (Button) findViewById(R.id.finishscore_selectDate);
		beforeDateButton = (Button) findViewById(R.id.date_before);
		afterDateButton = (Button) findViewById(R.id.date_after);
		dateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setDate();
			}
		});
		
		beforeDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*selectDate = minusOneDay(selectDateString);
				selectDateString = parseDate2String(selectDate);
				updateTextView();
				updateButton();
				loadAllMatch();*/
				updateBeforeMatch();
			}
		});
		// after setOnclickListener make work
//		updateButton();
		
		afterDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*selectDate = addOneDay(selectDateString);
				selectDateString = parseDate2String(selectDate);
				updateTextView();
				updateButton();
				loadAllMatch();*/
				updateAfterMatch();
			}
		});	
	}
	
	private void initListView() {
		listView = (ListView) findViewById(R.id.finishScore_listView);
		listView.setFastScrollEnabled(true);
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

	private void initializeAdapter() {
		List<Match> list = matchManager.getUnfinishedMatch();
		adapter = new WeeklyScheduleAdapter(list, this);
		adapter.setPageSize(pageSize);
	}

	private void initServicesAndManagers() {
		weeklyMatchService = this.getweeklyMatchService();
		matchManager = weeklyMatchService.getMatchManager();
	}

	private WeeklyMatchService getweeklyMatchService() {
		return new WeeklyMatchService();
	}

	private void loadAllMatch() {
		weeklyMatchService.loadWeeklyMatch(this, this, selectDateString);
	}

	private void initCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(china);
		dates[0] = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()); 
		for (int i = 1; i < 7; i++) {
			cal.add(Calendar.DATE, 1);
			dates[i] = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		}
		//显示当天比赛
		selectDateString = dates[0];
		selectDate = parseString2Date(selectDateString);
		updateTextView();
		Log.i(TAG, "curentDate="+selectDateString);
	}
	
	private Date addOneDay(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(dateString);
			Calendar calendar = Calendar.getInstance();
		    calendar.setTime(date);
		    calendar.add(Calendar.DATE, 1);
		    return calendar.getTime();
		} catch (ParseException e) {
			Log.e(TAG, e.toString());
			return null;
		}	
	}
	
	private Date minusOneDay(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(dateString);
			Calendar calendar = Calendar.getInstance();
		    calendar.setTime(date);
		    calendar.add(Calendar.DATE, -1);
		    return calendar.getTime();
		} catch (ParseException e) {
			Log.e(TAG, e.toString());
			return null;
		}	
	}
	
	private Date parseString2Date(String dateString) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
		} catch (ParseException e) {
			Log.e(TAG, e.toString());
			return null;			
		}
	}
	
	private String parseDate2String(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	private void updateTextView() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(selectDate);
		String day = dayofWeek[selectDate.getDay()];
		String str = selectDateString.concat(" ").concat("星期").concat(day);
		textView.setText(str);
	}
	
	private int validateDate() {
		// 如果当前选择时间比现在时间早，返回-1，比一个星期后时间晚返回1，在现在和一个星期之间返回0
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.DATE, 6);
		Date date1 = calendar1.getTime();
		Date date2 = calendar2.getTime();
		String day1 = parseDate2String(date1);
		String day2 = parseDate2String(date2);
		Log.i(TAG, day1+","+day2+","+selectDateString);
		int temp = 0;
		if(selectDateString.compareTo(day1) <= 0 ) {
			temp = -1;
		} else if (selectDateString.compareTo(day2) >= 0) {
			temp = 1;
		} else {
			temp = 0;
		}
		return temp;
	}
	
	private void setDate() {
		DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				selectDateString = dates[whichButton];
				selectDate = parseString2Date(selectDateString);
				updateTextView();
				Log.i(TAG, "click " + selectDateString);
				loadAllMatch();
			}
		};

		Dialog dialog = new AlertDialog.Builder(WeeklyScheduleActivity.this)
				// .setIcon(R.drawable.alert_dialog_icon)
				.setTitle("选择日期")
				.setSingleChoiceItems(dates, -1, cl)
				.setNegativeButton(R.string.alert_dialog_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Log.d(TAG, "click cancel");
							}
						}).create();
		;
		dialog.show();
	}

	/*private void updateButton() {
		int temp = validateDate();
		if (temp < 0) {
			beforeDateButton.setClickable(false);
			afterDateButton.setClickable(true);
			Toast.makeText(getApplicationContext(), "已经到最前一页，请往翻", Toast.LENGTH_LONG);
		} else if (temp == 0) {
			beforeDateButton.setClickable(true);
			afterDateButton.setClickable(true);
			Toast.makeText(getApplicationContext(), "已经到最后一页，请往前翻", Toast.LENGTH_LONG);
		} else {
			beforeDateButton.setClickable(true);
			afterDateButton.setClickable(false);
		}
	}*/
	
	private void updateBeforeMatch() {
		int temp = validateDate();
		if (temp < 0) {
			Toast.makeText(getApplicationContext(), "已经到达最前页，请往后翻", Toast.LENGTH_LONG).show();
		} else {
			selectDate = minusOneDay(selectDateString);
			selectDateString = parseDate2String(selectDate);
			updateTextView();
			loadAllMatch();
		}
	}
	
	private void updateAfterMatch() {
		int temp = validateDate();
		if (temp > 0) {
			Toast.makeText(getApplicationContext(), "已经到达最后页，请往前翻", Toast.LENGTH_LONG).show();
		} else {
			selectDate = addOneDay(selectDateString);
			selectDateString = parseDate2String(selectDate);
			updateTextView();
			loadAllMatch();
		}
	}
	

	@Override
	public void loadAllMatchFinish(ResultCodeType resultCode) {
		switch (resultCode) {
		case SUCCESS: {
			List<Match>	unfinishedMatchList = new ArrayList<Match>();
			unfinishedMatchList = matchManager.getUnfinishedMatch();
			adapter.updateList(unfinishedMatchList);
			// 网络加载数据完，装载数据到adapter
			Log.i(TAG, "<loadAllMatchFinish> unfinshedMatchList.size="+unfinishedMatchList.size());
			adapter.notifyDataSetChanged();
		}
			break;
		}
	}
	
	@Override
	public void onRefresh() {
		loadAllMatch();
	}
	
}
