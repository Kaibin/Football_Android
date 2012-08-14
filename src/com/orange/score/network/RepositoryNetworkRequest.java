package com.orange.score.network;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import android.util.Log;

import com.orange.score.constants.LanguageType;

public class RepositoryNetworkRequest {
	private static final String TAG = ScoreNetworkRequest.class.getName();

	public static final String SEP_SEGMENT = "\\$\\$";
	public static final String SEP_RECORD = "\\!";
	public static final String SEP_FIELD = "\\^";
	
	private static final int LEVEL_SEGMENT = 1;
	private static final int LEVEL_RECORD = 2;
	private static final int LEVEL_FIELD = 3;
	
	public static final int SEGMENT_REPOSITORY_CONTINENT = 0;
	public static final int SEGMENT_REPOSITORY_COUNTRY = 1;
	public static final int SEGMENT_REPOSITORY_LEAGUE = 2;
	public static final int SEGMENT_REPOSITORY_NUM = 3;
	
	public static final int INDEX_REPOSITORY_CONTINENT_ID = 0;
	public static final int INDEX_REPOSITORY_CONTINENT_NAME = 1;
	public static final int INDEX_REPOSITORY_CONTINENT_COUNT = 2;
	
	public static final int INDEX_REPOSITORY_COUNTRY_ID = 0;
	public static final int INDEX_REPOSITORY_COUNTRY_CONTINENT_ID = 1;
	public static final int INDEX_REPOSITORY_COUNTRY_NAME = 2;
	public static final int INDEX_REPOSITORY_COUNTRY_COUNT = 3;

	public static final int INDEX_REPOSITORY_LEAGUE_ID = 0;
	public static final int INDEX_REPOSITORY_LEAGUE_COUNTRY_ID = 1;
	public static final int INDEX_REPOSITORY_LEAGUE_NAME = 2;
	public static final int INDEX_REPOSITORY_LEAGUE_SHORTNAME = 3;
	public static final int INDEX_REPOSITORY_LEAGUE_TYPE = 4;
	public static final int INDEX_REPOSITORY_LEAGUE_SEASON_LIST = 5;
	public static final int INDEX_REPOSITORY_LEAGUE_COUNT = 6;

	public static final int SEGMENT_REPOSITORY_CUP_SCHEDULE_GROUP_INFO = 0;
	public static final int SEGMENT_REPOSITORY_CUP_SCHEDULE_GROUP_POINTS = 1;
	public static final int SEGMENT_REPOSITORY_CUP_SCHEDULE_MATCH_RESULT = 2;
	public static final int SEGMENT_REPOSITORY_CUP_SCHEDULE_COUNT = 3;
	
	public static final int INDEX_REPOSITORY_CUP_GROUP_LEAGUE_ID = 0;
	public static final int INDEX_REPOSITORY_CUP_GROUP_SEASON = 1;
	public static final int INDEX_REPOSITORY_CUP_GROUP_GROUP_ID = 2;
	public static final int INDEX_REPOSITORY_CUP_GROUP_COUNT = 3;
	
	public static final int SEGMENT_REPOSITORY_LEAGUE_SCHEDULE_TOTAL_ROUND = 0;
	public static final int SEGMENT_REPOSITORY_LEAGUE_SCHEDULE_ROUND_DETAIL = 1;
	public static final int SEGMENT_REPOSITORY_LEAGUE_SCHEDULE_COUNT = 2;
	
	public static final int INDEX_REPOSITORY_LEAGUE_SCHEDULE_CURRENT_ROUND = 0;

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
	
	public static String[] getAllRepository(LanguageType mandary){
		String url = String.format("http://bf.bet007.com/phone/InfoIndex.aspx?lang=%d",mandary.intValue());
		return scoreHttpGet(url, LEVEL_SEGMENT);		
	}
	
	public static String[] getRepositoryGroupInfo(String leagueId, String season) {
		String url = String.format("http://bf.bet007.com/Phone/cupsaicheng.aspx?id=%s&season=%s", leagueId, season);
		return scoreHttpGet(url, LEVEL_SEGMENT);
	}
	
	public static String[] getRepositoryRoundInfo(String leagueId, String season) {
		String url = String.format("http://bf.bet007.com/phone/SaiCheng.aspx?ID=%s&Season=%s", leagueId, season);
		return scoreHttpGet(url, LEVEL_SEGMENT);
	}
}
