package com.orange.score.activity.more;


import com.orange.score.R;
import com.orange.score.activity.common.CommonFootballActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;  
import android.preference.PreferenceManager;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class ScorePromptActivity extends PreferenceActivity implements  
	OnPreferenceClickListener,  OnPreferenceChangeListener{
	
	private static final String TAG = "ScorePromptActivity";
	private EditTextPreference refleshTimePref;
	private CheckBoxPreference soundPref;
	private CheckBoxPreference vibratePref;
	private CheckBoxPreference pushPref;
	private CheckBoxPreference autolockPref;
	private SharedPreferences sharedPreferences;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.more_prompt_preference);
		
		// let the screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		getListView().setBackgroundColor(getResources().getColor(R.color.white));
		getListView().setDividerHeight(0);
		
		refleshTimePref = (EditTextPreference) findPreference("refleshTime_editTextPref");
		soundPref = (CheckBoxPreference) findPreference("sound_checkboxPref");
		vibratePref = (CheckBoxPreference) findPreference("vibrate_checkboxPref");
		pushPref = (CheckBoxPreference) findPreference("push_checkboxPref");
		autolockPref = (CheckBoxPreference) findPreference("autoLock_checkboxPref");
		// editText只允许输入数字
		EditText refleshEditText = (EditText)refleshTimePref.getEditText(); 
		refleshEditText.setKeyListener(DigitsKeyListener.getInstance(false,true));
		refleshEditText.setHint("比分实时刷新时间间隔，最小10秒");
		
		// 设置监听器
		refleshTimePref.setOnPreferenceChangeListener(this);
		refleshTimePref.setOnPreferenceClickListener(this);
		soundPref.setOnPreferenceChangeListener(this);
		soundPref.setOnPreferenceClickListener(this);
		vibratePref.setOnPreferenceChangeListener(this);
		vibratePref.setOnPreferenceClickListener(this);
		pushPref.setOnPreferenceChangeListener(this);
		pushPref.setOnPreferenceClickListener(this);
		autolockPref.setOnPreferenceChangeListener(this);
		autolockPref.setOnPreferenceClickListener(this);
		initPref();
		// 将该activity加入退出列表中
		CommonFootballActivity.activityList.add(this);
	}
	
	private void initPref() {		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);  
		String oldValue = sharedPreferences.getString("refleshTime_editTextPref", "20");
		Log.i("refleshTime_editTextPref value=", oldValue);
		refleshTimePref.setSummary(oldValue+"秒");
	}
	
	
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		Log.i(TAG, "onPreferenceClick----->"+String.valueOf(preference.getKey()));
		if (preference == refleshTimePref) {
			String value = sharedPreferences.getString("refleshTime_editTextPref", "20");
			refleshTimePref.getEditText().setText(value);
		}
		return true;
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object objValue) {
		Log.i(TAG, "onPreferenceChange----->"+String.valueOf(preference.getKey()));  
		if (preference == refleshTimePref) {
			String oldValue = sharedPreferences.getString("refleshTime_editTextPref", "20");
			String newValue = refleshTimePref.getEditText().getText().toString().trim();
			if (newValue.equals("")) {
				newValue = "20";
			} 
			int value = Integer.valueOf(newValue);
			if (value >= 60*60) {
				newValue = "3600";
				Toast.makeText(getApplicationContext(), "刷新时间间隔最大不超过3600秒", Toast.LENGTH_SHORT).show();
			}
			if (value < 10) {
				newValue = "10";
				Toast.makeText(getApplicationContext(), "刷新时间间隔最少为10秒", Toast.LENGTH_SHORT).show();
			}
 
			sharedPreferences.edit().putString("refleshTime_editTextPref", newValue).commit();
            Log.i(TAG, "oldValue="+oldValue+",newValue="+newValue); 
			refleshTimePref.setSummary(newValue + "秒");
			// false 则不将新值写入sharedPreference文件
			return false;
			
		}
		return true;
	}	
	
}
