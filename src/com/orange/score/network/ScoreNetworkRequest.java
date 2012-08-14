package com.orange.score.network;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.orange.score.constants.LanguageType;
import com.orange.score.constants.ScoreType;
import com.orange.score.model.index.OddsType;

import android.util.Log;

public class ScoreNetworkRequest {

	private static final String TAG = ScoreNetworkRequest.class.getName();

	public static final String SEP_SEGMENT = "\\$\\$";
	public static final String SEP_RECORD = "\\!";
	public static final String SEP_FIELD = "\\^";
	
	private static final int LEVEL_SEGMENT = 1;
	private static final int LEVEL_RECORD = 2;
	private static final int LEVEL_FIELD = 3;

	public static final int SEGMENT_REALTIME_SERVER_TIME = 0;
	public static final int SEGMENT_REALTIME_LEAGUE = 1;
	public static final int SEGMENT_REALTIME_MATCH = 2;
	public static final int SEGMENT_REALTIME_MATCH_NUM = 3;

	public static final int INDEX_LEAGUE_NAME = 0;
	public static final int INDEX_LEAGUE_ID = 1;
	public static final int INDEX_LEAGUE_IS_TOP = 2;
	public static final int INDEX_LEAGUE_COUNT = 3;  
	
	public static final int INDEX_MATCH_ID = 0;
	public static final int INDEX_MATCH_LEAGUE_ID = 1;
	public static final int INDEX_MATCH_STATUS = 2;
	public static final int INDEX_MATCH_DATE = 3;
	public static final int INDEX_MATCH_START_DATE = 4;
	public static final int INDEX_MATCH_HOME_TEAM_NAME = 5;
	public static final int INDEX_MATCH_AWAY_TEAM_NAME = 6;
	public static final int INDEX_MATCH_HOME_TEAM_SCORE = 7;
	public static final int INDEX_MATCH_AWAY_TEAM_SCORE = 8;
	public static final int INDEX_MATCH_HOME_TEAM_FIRST_HALF_SCORE = 9;
	public static final int INDEX_MATCH_AWAY_TEAM_FIRST_HALF_SCORE = 10;
	public static final int INDEX_MATCH_HOME_TEAM_RED = 11;
	public static final int INDEX_MATCH_AWAY_TEAM_RED = 12;
	public static final int INDEX_MATCH_HOME_TEAM_YELLOW = 13;
	public static final int INDEX_MATCH_AWAY_TEAM_YELLOW = 14;
	public static final int INDEX_MATCH_CROWN_CHUPAN = 15;
	public static final int MATCH_FIELD_MIN_COUNT = 15;	
	public static final int MATCH_FIELD_MAX_COUNT = 16;	
	
	public static final int INDEX_REALTIME_SCORE_MATCHID = 0;
	public static final int INDEX_REALTIME_SCORE_STATUS = 1;
	public static final int INDEX_REALTIME_SCORE_DATE = 2;
	public static final int INDEX_REALTIME_SCORE_START_DATE = 3;
	public static final int INDEX_REALTIME_SCORE_HOME_TEAM_SCORE = 4;
	public static final int INDEX_REALTIME_SCORE_AWAY_TEAM_SCORE = 5;
	public static final int INDEX_REALTIME_SCORE_HOME_TEAM_FIRST_HALF_SCORE = 6;
	public static final int INDEX_REALTIME_SCORE_AWAY_TEAM_FIRST_HALF_SCORE = 7;
	public static final int INDEX_REALTIME_SCORE_HOME_TEAM_RED = 8;
	public static final int INDEX_REALTIME_SCORE_AWAY_TEAM_RED = 9;
	public static final int INDEX_REALTIME_SCORE_HOME_TEAM_YELLOW = 10;
	public static final int INDEX_REALTIME_SCORE_AWAY_TEAM_YELLOW = 11;
	public static final int REALTIME_SCORE_FILED_COUNT = 12;
	
	public static final int INDEX_REALTIME_MATCH_HOME_NAME = 0;
	public static final int INDEX_REALTIME_MATCH_AWAY_NAME = 1;
	public static final int INDEX_REALTIME_MATCH_HOME_CANTONESE_NAME = 2;
	public static final int INDEX_REALTIME_MATCH_AWAY_CANTONESE_NAME = 3;
	public static final int INDEX_REALTIME_MATCH_STATUS = 4;
	public static final int INDEX_REALTIME_MATCH_TIME = 5;
	public static final int INDEX_REALTIME_MATCH_HOME_RANK = 6;
	public static final int INDEX_REALTIME_MATCH_AWAY_RANK = 7;
	public static final int INDEX_REALTIME_MATCH_HOME_IMAGE_URL = 8;
	public static final int INDEX_REALTIME_MATCH_AWAY_IMAGE_URL = 9;
	public static final int INDEX_REALTIME_MATCH_HOME_SCORE = 10;
	public static final int INDEX_REALTIME_MATCH_AWAY_SCORE = 11;
	public static final int INDEX_REALTIME_MATCH_HAS_LINEUP = 12;
	public static final int INDEX_REALTIME_MATCH_FIELD_MIN_COUNT = 12;
	public static final int INDEX_REALTIME_MATCH_FIELD_MAX_COUNT = 13;
	
	public static final int INDEX_REALINDEX_COMPANY_ID = 0;
	public static final int INDEX_REALINDEX_COMPANY_NAME = 1;
	public static final int INDEX_REALINDEX_COMPANY_HAVE_YAPEI = 2;
	public static final int INDEX_REALINDEX_COMPANY_HAVE_DAXIAO = 3;
	public static final int INDEX_REALINDEX_COMPANY_HAVE_OUPEI = 4;
	public static final int INDEX_REALINDEX_COUNT = 5;
	
	public static final int SEGMENT_REALINDEX_LEAGUE= 0;
	public static final int SEGMENT_REALINDEX_MATCH = 1;
	public static final int SEGMENT_REALINDEX_ODDS = 2;
	public static final int SEGMENT_REALINDEX_ODDS_COUNT = 3;
	
	public static final int INDEX_REALINDEX_LEAGUE_ID = 0;
	public static final int INDEX_REALINDEX_LEAGUE_NAME = 1;
	public static final int INDEX_REALINDEX_LEAGUE_LEVEL = 2;
	public static final int INDEX_REALINDEX_LEAGUE_NUM = 3;
	
	public static final int INDEX_REALINDEX_MATCH_ID = 0;
	public static final int INDEX_REALINDEX_MATCH_LEAGUE_ID = 1;
	public static final int INDEX_REALINDEX_MATCH_TIME = 2;
	public static final int INDEX_REALINDEX_MATCH_HOME_NAME = 3;
	public static final int INDEX_REALINDEX_MATCH_AWAY_NAME = 4;
	public static final int INDEX_REALINDEX_MATCH_STATUS = 5;
	public static final int INDEX_REALINDEX_MATCH_HOME_SCORE = 6;
	public static final int INDEX_REALINDEX_MATCH_AWAY_SCORE = 7;
	public static final int INDEX_REALINDEX_MATCH_NUM = 8;
	
	public static final int INDEX_REALINDEX_ODDS_MATCH_ID = 0;
	public static final int INDEX_REALINDEX_ODDS_COMPANY_ID = 1;
	public static final int INDEX_REALINDEX_ODDS_ID = 2;
	public static final int INDEX_REALINDEX_YAPEI_CHUPAN = 3;
	public static final int INDEX_REALINDEX_YAPEI_HOME_TEAM_CHUPAN = 4;
	public static final int INDEX_REALINDEX_YAPEI_AWAY_TEMA_CHUPAN = 5;
	public static final int INDEX_REALINDEX_YAPEI_INSTANT_CHUPAN = 6;
	public static final int INDEX_REALINDEX_YAPEI_HOME_TEAM_ODDS = 7;
	public static final int INDEX_REALINDEX_YAPEI_AWAY_TEMA_ODDS = 8;
	public static final int INDEX_REALINDEX_ODDS_FIELD_NUM = 9;
	
	public static final int INDEX_REALINDEX_DAXIAO_CHUPAN = 3;
	public static final int INDEX_REALINDEX_DAXIAO_BIG_CHUPAN = 4;
	public static final int INDEX_REALINDEX_DAXIAO_SMALL_CHUPAN = 5;
	public static final int INDEX_REALINDEX_DAXIAO_INSTANT_CHUPAN = 6;
	public static final int INDEX_REALINDEX_DAXIAO_BIG_ODDS = 7;
	public static final int INDEX_REALINDEX_DAXIAO_SMALL_ODDS = 8;
	
	public static final int INDEX_REALINDEX_OUPEI_HOME_WIN_CHUPEI = 3;
	public static final int INDEX_REALINDEX_OUPEI_DRAW_CHUPEI = 4;
	public static final int INDEX_REALINDEX_OUPEI_AWAY_WIN_CHUPEI = 5;
	public static final int INDEX_REALINDEX_OUPEI_HOME_WIN_INSTANT = 6;
	public static final int INDEX_REALINDEX_OUPEI_DRAW_INSTANT = 7;
	public static final int INDEX_REALINDEX_OUPEI_AWAY_WIN_INSTANT = 8;
	
	public static final int INDEX_REALTIME_ODDS_MATCH_ID = 0;
	public static final int INDEX_REALTIME_ODDS_COMPANY_ID = 1;
	public static final int INDEX_REALTIME_ODDS_INSTANT_PANKOU = 2;
	public static final int INDEX_REALTIME_ODDS_INSTANT_HOME = 3;
	public static final int INDEX_REALTIME_ODDS_INSTANT_AWAY = 4;
	public static final int INDEX_REALTIME_ODDS_CHANGE_FIELD_NUM = 5;
	
	public static String httpGet(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		ResponseHandler<String> strRespHandler = new BasicResponseHandler();

		String response = null;
		try {
			Log.d(TAG, "Send http request: " + url);
			response = httpclient.execute(httpGet, strRespHandler);
			Log.d(TAG, "Get http response: " + response);									
		} catch (Exception e) {
			Log.e(TAG, "catch exception while send http request " + e.toString(), e);
		}

		return response;
	}
	
	public static String[] scoreHttpGet(String url, int level){
		String response = httpGet(url);
		if (response == null)
			return null;
		
		String[] list = null;
		if (level == LEVEL_SEGMENT)
			list = response.split(SEP_SEGMENT);
		else if (level == LEVEL_RECORD)
			list = response.split(SEP_RECORD);
		else if (level == LEVEL_FIELD)
			list = response.split(SEP_FIELD);
		else
			Log.e(TAG, "Score Http Get But incorrect Level");
		
		return list;
	}
		
	public static String[] getAllMatch(LanguageType mandary, ScoreType all){
		String url = String.format("http://bf.bet007.com/phone/schedule.aspx?lang=%d&type=%d", 
				mandary.intValue(), all.intValue());
		return scoreHttpGet(url, LEVEL_SEGMENT);		
	}
	 
	public static String[] getFinishMatch(LanguageType languageType, String date){
		String url = String.format("http://bf.bet007.com/phone/scheduleByDate.aspx?lang=%d&date=%s", 
				languageType.intValue(), date);
		Log.i(TAG, "url="+url);
		return scoreHttpGet(url, LEVEL_SEGMENT);
	}

	public static String[] getMatchLiveChange(){
		String url = String.format("http://bf.bet007.com/phone/LiveChange.aspx");
		return scoreHttpGet(url, LEVEL_RECORD);				
	}
	
	public static String[] getMatchDetail(String matchId){
		String url = String.format("http://bf.bet007.com/phone/ScheduleDetail.aspx?ID=%s",matchId);
		return scoreHttpGet(url, LEVEL_FIELD);				
	}
	
	public static String[] getIndexCompany() {
		String url = String.format("http://bf.bet007.com/phone/Company.aspx");
		return scoreHttpGet(url, LEVEL_RECORD);
	}

	public static String[] getOddsData(String date, String companyID, LanguageType languageType, ScoreType socreType, OddsType oddsTpye ) {
		String url = String.format("http://bf.bet007.com/phone/Odds.aspx?Date=%s&companyID=%s&lang=%s&type=%s&odds=%s", date, companyID, languageType.intValue(), socreType.intValue(), oddsTpye.intValue());
		return scoreHttpGet(url, LEVEL_SEGMENT);
	}

	public static String[] getOddsLiveChange(OddsType oddsType) {
		String url = String.format("http://bf.bet007.com/phone/OddsChange.aspx?odds=%s", oddsType.intValue());
		return scoreHttpGet(url, LEVEL_RECORD);
	}
	
}
