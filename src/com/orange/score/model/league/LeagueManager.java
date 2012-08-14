package com.orange.score.model.league;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.orange.score.constants.ScoreType;
import com.orange.score.network.RepositoryNetworkRequest;
import com.orange.score.network.ScoreNetworkRequest;

public class LeagueManager {

	private static final String TAG = "LeagueManager";

	public static final int LEAGUE_FROM_REALTIME_MATCH = 1;
	public static final int LEAGUE_FROM_REALTIME_INDEX = 2;
	public static final int LEAGUE_FROM_REPOSITORY = 3;

	List<League> leagueList = new ArrayList<League>();
	Map<String, League> leagueMap = new HashMap<String, League>();
	Set<String> filterLeagueSet = new HashSet<String>();
	
	String totalRound;
	String currentRound;

	public void updateDataFromStringList(String[] stringList, int from) {
		if (stringList == null || stringList.length == 0)
			return;

		// clear all old data
		leagueList.clear();
		for (int i = 0; i < stringList.length; i++) {

			String[] fields = stringList[i].split(ScoreNetworkRequest.SEP_FIELD);
			League league = null;
			String leagueId;
			String leagueName;
			String isTop;
			
			switch (from) {
			case LEAGUE_FROM_REALTIME_MATCH:
				if (fields.length < ScoreNetworkRequest.INDEX_LEAGUE_COUNT) {
					Log.w(TAG, "updateDataFromStringList but field count not enough, count = " + fields.length);
					continue;
				}
				leagueId = fields[ScoreNetworkRequest.INDEX_LEAGUE_ID];
				leagueName = fields[ScoreNetworkRequest.INDEX_LEAGUE_NAME];
				isTop = fields[ScoreNetworkRequest.INDEX_LEAGUE_IS_TOP];

				league = new League(leagueId, leagueName, isTop);
				break;
				
			case LEAGUE_FROM_REPOSITORY:
				if (fields.length < RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_COUNT) {
					Log.w(TAG, "updateLeagueDataFromStringList but field count not enough, count = " + fields.length);
					continue;
				}

				leagueId = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_ID];
				String countryId = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_COUNTRY_ID];
				leagueName = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_NAME];
				String leagueShortName = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_SHORTNAME];
				String leagueType = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_TYPE];
				String seasonListString = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_SEASON_LIST];
				String[] seasonArray = seasonListString.split(",");
				List<String> seasonList = Arrays.asList(seasonArray);

			    league = new League();
				league.setLeagueId(leagueId);
				league.setCountryId(countryId);
				league.setName(leagueName);
				league.setShortName(leagueShortName);
				league.setType(Integer.parseInt(leagueType));
				league.setSeasonList(seasonList);
				break;
				
			case LEAGUE_FROM_REALTIME_INDEX:
				if (fields.length < ScoreNetworkRequest.INDEX_REALINDEX_LEAGUE_NUM) {
					Log.w(TAG, "updateOddsLeagueDataFromStringList but field count not enough, count = " + fields.length);
					continue;
				}

				leagueId = fields[ScoreNetworkRequest.INDEX_REALINDEX_LEAGUE_ID];
				leagueName = fields[ScoreNetworkRequest.INDEX_REALINDEX_LEAGUE_NAME];
				isTop = fields[ScoreNetworkRequest.INDEX_REALINDEX_LEAGUE_LEVEL];
				league = new League(leagueId, leagueName, isTop);
				break;

			default:
				break;
			}

			Log.d(TAG, "add league = " + league.toString());
			leagueList.add(league);
			leagueMap.put(league.getLeagueId(), league);
		}
	}
	
	public Set<String> getFilterLeagueSet() {
		return filterLeagueSet;
	}

	public void setFilterLeagueSet(Set<String> filterLeagueSet) {
		this.filterLeagueSet = filterLeagueSet;
	}


	public League findLeagueById(String leagueId) {
		return leagueMap.get(leagueId);
	}

	public List<League> findAllLeagues() {
		return leagueList;
	}
	
	public void setLeagueList(List<League> newList) {
		this.leagueList = newList;
	}
	
	public List<League> findAllTopLeagues() {
		List<League> list = new ArrayList<League>();
		for (League league : leagueList) {
			if (league.isTop) {
				list.add(league);
			}
		}
		return list;
	}
	
	public List<League> findZucaiLeagues() {
		List<League> list = new ArrayList<League>();
		for (League league : leagueList) {
			if (league.type == ScoreType.ZUCAI.intValue()) {
				list.add(league);
			}
		}
		return list;
	}
	
	public List<League> findJingcaiLeagues() {
		List<League> list = new ArrayList<League>();
		for (League league : leagueList) {
			if (league.type == ScoreType.JINGCAI.intValue()) {
				list.add(league);
			}
		}
		return list;
	}

	public List<League> findDanchangLeagues() {
		List<League> list = new ArrayList<League>();
		for (League league : leagueList) {
			if (league.type == ScoreType.DANCHANGE.intValue()) {
				list.add(league);
			}
		}
		return list;
	}

//	public Set<String> getSelectedLeagues() {
//		return selectedLeagueSet;
//	}
	
	public List<League> filterLeagueByCountryId(String countryId) {
		List<League> list = new ArrayList<League>();
		if (leagueList == null || leagueList.size() == 0) {
			return null;
		}
		for (League league : leagueList) {
			if (league.getCountryId().equals(countryId)) {
				list.add(league);
			}
		}
		return list;
	}

	public void updateLeagueRound(String totalRound, String currentRound) {
		this.totalRound = totalRound;
		this.currentRound = currentRound;
	}
	
	public String[] getLeagueRound() {
		String[] round = new String[]{totalRound, currentRound};
		return round;
	}

	public boolean hasLeagueData() {
		return (leagueList != null && leagueList.size() > 0);
	}

}
