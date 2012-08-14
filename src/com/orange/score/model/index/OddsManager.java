package com.orange.score.model.index;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.orange.score.constants.ScoreType;
import com.orange.score.model.league.League;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchStatusType;
import com.orange.score.network.ScoreNetworkRequest;
import com.orange.score.utils.DateUtil;

public class OddsManager {
	private static String TAG = OddsManager.class.getName();
	List<Match> matchList = new ArrayList<Match>();
	Map<String, Match>	matchMap = new HashMap<String, Match>();
	List<YapeiOdds> yapeiOddsList = new ArrayList<YapeiOdds>();
	List<OupeiOdds> oupeiOddsList = new ArrayList<OupeiOdds>();
	List<DaxiaoOdds> daxiaoOddsList = new ArrayList<DaxiaoOdds>();
	List<String> filterLeagueIdList = new ArrayList<String>();
	Map<String, Odds> oddsMap = new HashMap<String, Odds>();
	ScoreType filterScoreType = ScoreType.FIRST;
	
	public ScoreType getFilterScoreType() {
		return filterScoreType;
	}

	public void setFilterScoreType(ScoreType filterScoreType) {
		
		if (this.filterScoreType != filterScoreType){
			filterLeagueIdList.clear();
		}
		
		this.filterScoreType = filterScoreType;
	}
	
	public void setFilterLeagueIdList(List<String> filterLeagueIdList) {
		this.filterLeagueIdList = filterLeagueIdList;
	}
	
	public List<String> getFilterLeagueIdList() {
		return filterLeagueIdList;
	}
	

	public String getLeagueIdByMatchId(String matchId) {
		if (matchId == null || matchId.length() == 0) {
			return null;
		}
		for (Match match : matchList) {
			if (match.getMatchId().equals(matchId)) {
				return match.getLeagueId();
			}
		}
		return null;
	}
	
	public void addOddsToMap(Odds odds, Map<Match, List<Odds>> map) {
		List<Odds> list;
		Match match = findMatchById(odds.getMatchId());
		if (match != null && odds != null) {
			if (!map.containsKey(match)) {
				list = new ArrayList<Odds>();
				list.add(odds);
			} else {
				list = map.get(match);
				list.add(odds);
			}
			map.put(match, list);

		}
	}
	
	public Map<Match, List<Odds>> filterOddsByOddsType(OddsType oddsType) {
		
		Map<Match, List<Odds>> map = new HashMap<Match, List<Odds>>();
		switch (oddsType) {
		case YAPEI:
			for (Odds odds : yapeiOddsList) {
				if (filterLeagueIdList.contains(getLeagueIdByMatchId(odds.getMatchId()))) {
					addOddsToMap(odds, map);
				}
			}
			break;
		case OUPEI:
			for (Odds odds : oupeiOddsList) {
				if (filterLeagueIdList.contains(getLeagueIdByMatchId(odds.getMatchId()))) {
					addOddsToMap(odds, map);
				}
			}
			break;
		case DAXIAO:
			for (Odds odds : daxiaoOddsList) {
				if (filterLeagueIdList.contains(getLeagueIdByMatchId(odds.getMatchId()))) {
					addOddsToMap(odds, map);
				}
			}
			break;
			
		default:
			break;
		}
		return map;
	}

//	public void updateOddsLeagueDataFromStringList(String[] stringList) {
//		if (stringList == null || stringList.length == 0) {
//			return;
//		}
//		
//		// clear all old data
//		leagueList.clear();
//
//		for (int i = 0; i < stringList.length; i++) {
//
//			String[] fields = stringList[i].split(ScoreNetworkRequest.SEP_FIELD);
//
//			if (fields.length < ScoreNetworkRequest.INDEX_REALINDEX_LEAGUE_NUM) {
//				Log.w(TAG,
//						"updateOddsLeagueDataFromStringList but field count not enough, count = "
//								+ fields.length);
//				continue;
//			}
//
//			String leagueId = fields[ScoreNetworkRequest.INDEX_REALINDEX_LEAGUE_ID];
//			String leagueName = fields[ScoreNetworkRequest.INDEX_REALINDEX_LEAGUE_NAME];
//			String isTop = fields[ScoreNetworkRequest.INDEX_REALINDEX_LEAGUE_LEVEL];
//			League league = new League(leagueId, leagueName, isTop);
//			Log.d(TAG, "add league = " + league.toString());
//
//			leagueList.add(league);
//		}
//	}
	
	public void updateOddsMatchDataFromStringList(String[] stringList, LeagueManager leagueManager) {
		if (stringList == null || stringList.length == 0) {
			return;
		}
		
		matchList.clear();
		matchMap.clear();
		
		for (int i = 0; i < stringList.length; i++) {
			String[] fields = stringList[i].split(ScoreNetworkRequest.SEP_FIELD);

			if (fields.length < ScoreNetworkRequest.INDEX_REALINDEX_MATCH_NUM) {
				Log.w(TAG,
						"updateOddsMatchDataFromStringList but field count not enough, count = "
								+ fields.length);
				continue;
			}

			String matchId = fields[ScoreNetworkRequest.INDEX_REALINDEX_MATCH_ID];
			String leagueId = fields[ScoreNetworkRequest.INDEX_REALINDEX_MATCH_LEAGUE_ID];
			String matchTime = fields[ScoreNetworkRequest.INDEX_REALINDEX_MATCH_TIME];
			String homeTeamName = fields[ScoreNetworkRequest.INDEX_REALINDEX_MATCH_HOME_NAME];
			String awayTeamName = fields[ScoreNetworkRequest.INDEX_REALINDEX_MATCH_AWAY_NAME];
			String status = fields[ScoreNetworkRequest.INDEX_REALINDEX_MATCH_STATUS];
			String homeScore = fields[ScoreNetworkRequest.INDEX_REALINDEX_MATCH_HOME_SCORE];
			String awayScore = fields[ScoreNetworkRequest.INDEX_REALINDEX_MATCH_AWAY_SCORE];
			
			Match match = new Match();
			match.setMatchId(matchId);
			match.setLeagueId(leagueId);
			match.setHomeTeamName(homeTeamName);
			match.setAwayTeamName(awayTeamName);
			match.setMatchTime(matchTime);
			match.setDate(matchTime);
			match.setStatus(status);
			match.setHomeTeamScore(homeScore);
			match.setAwayTeamScore(awayScore);
			Log.d(TAG, "add match = " + match.toString());

			matchList.add(match);
			matchMap.put(matchId, match);
			
			// update league match info (currently it's match counter)
			League league = leagueManager.findLeagueById(leagueId);
			if (league != null){
				match.setLeagueName(league.name);
				league.increaseMatchCount();
			}
			
		}
	}
	
	public void updateOddsPeilvDataFromStringList(String[] stringList, OddsType oddsType) {
		if (stringList == null || stringList.length == 0) {
			return;
		}
		
		yapeiOddsList.clear();
		oupeiOddsList.clear();
		daxiaoOddsList.clear();
		
		for (String string : stringList) {
			String[] fields = string.split(ScoreNetworkRequest.SEP_FIELD);
			if (fields.length < ScoreNetworkRequest.INDEX_REALINDEX_ODDS_FIELD_NUM) {
				Log.w(TAG,
						"updateOddsPeilvDataFromStringList but field count not enough, count = "
								+ fields.length);
				continue;
			}
			
			String matchId = fields[ScoreNetworkRequest.INDEX_REALINDEX_ODDS_MATCH_ID];
			String companyId = fields[ScoreNetworkRequest.INDEX_REALINDEX_ODDS_COMPANY_ID];
			String oddsId = fields[ScoreNetworkRequest.INDEX_REALINDEX_ODDS_ID];
			String chupan;
			String instantOdds;
			switch (oddsType) {
			case YAPEI:
				chupan = fields[ScoreNetworkRequest.INDEX_REALINDEX_YAPEI_CHUPAN];
				String homeTeamChupan = fields[ScoreNetworkRequest.INDEX_REALINDEX_YAPEI_HOME_TEAM_CHUPAN];
				String awayTemaChupan = fields[ScoreNetworkRequest.INDEX_REALINDEX_YAPEI_AWAY_TEMA_CHUPAN];
				instantOdds = fields[ScoreNetworkRequest.INDEX_REALINDEX_YAPEI_INSTANT_CHUPAN];
				String homeTeamOdds = fields[ScoreNetworkRequest.INDEX_REALINDEX_YAPEI_HOME_TEAM_ODDS];
				String awayTemaOdds = fields[ScoreNetworkRequest.INDEX_REALINDEX_YAPEI_AWAY_TEMA_ODDS];
				YapeiOdds yapeiOdds = new YapeiOdds(matchId, companyId, oddsId, 
													chupan, homeTeamChupan, awayTemaChupan, 
													instantOdds, homeTeamOdds, awayTemaOdds);
				Log.d(TAG, "add yapeiOdds = " + yapeiOdds.toString());
				yapeiOddsList.add(yapeiOdds);
				break;
			case OUPEI:
				String homeWinChupei = fields[ScoreNetworkRequest.INDEX_REALINDEX_OUPEI_HOME_WIN_CHUPEI];
				String drawWinChupei = fields[ScoreNetworkRequest.INDEX_REALINDEX_OUPEI_DRAW_CHUPEI];
				String awayWinChupei = fields[ScoreNetworkRequest.INDEX_REALINDEX_OUPEI_AWAY_WIN_CHUPEI];
				String homeWinInstantOdds = fields[ScoreNetworkRequest.INDEX_REALINDEX_OUPEI_HOME_WIN_INSTANT];
				String drawInstantOdds = fields[ScoreNetworkRequest.INDEX_REALINDEX_OUPEI_DRAW_INSTANT];
				String awayWinInstantOdds = fields[ScoreNetworkRequest.INDEX_REALINDEX_OUPEI_AWAY_WIN_INSTANT];
				OupeiOdds oupeiOdds = new OupeiOdds(matchId, companyId, oddsId, 
													homeWinChupei, drawWinChupei, awayWinChupei, 
													homeWinInstantOdds, drawInstantOdds, awayWinInstantOdds);
				Log.d(TAG, "add oupeiOdds = " + oupeiOdds.toString());
				oupeiOddsList.add(oupeiOdds);
				break;
			case DAXIAO:
				chupan = fields[ScoreNetworkRequest.INDEX_REALINDEX_DAXIAO_CHUPAN];
				String bigChupan = fields[ScoreNetworkRequest.INDEX_REALINDEX_DAXIAO_BIG_CHUPAN];
				String smallChupan = fields[ScoreNetworkRequest.INDEX_REALINDEX_DAXIAO_SMALL_CHUPAN];
				instantOdds = fields[ScoreNetworkRequest.INDEX_REALINDEX_DAXIAO_INSTANT_CHUPAN];
				String bigOdd = fields[ScoreNetworkRequest.INDEX_REALINDEX_DAXIAO_BIG_ODDS];
				String smallOdds = fields[ScoreNetworkRequest.INDEX_REALINDEX_DAXIAO_SMALL_ODDS];
				DaxiaoOdds daxiaoOdds = new DaxiaoOdds(matchId, companyId, oddsId, 
														chupan, bigChupan, smallChupan, 
														instantOdds, bigOdd, smallOdds);
				Log.d(TAG, "add daxiaoOdds = " + daxiaoOdds.toString());
				daxiaoOddsList.add(daxiaoOdds);
				break;

			default:
				break;
			}
		}
	}

	public Match findMatchById(String matchId) {
		return matchMap.get(matchId);
	}
	
	public boolean canDisplayOdds(Match match, String date) {
		if (!isChineseTodayString(date)) {
			return true;
		}
		if (match == null) {
			return false;
		}
		if (match.getStatus() == MatchStatusType.FINISH) {
			return false;
		}
		return true;
	}
	
	private boolean isChineseTodayString(String date) {
	    String format = "yyyy-MM-dd";
		String todayString = DateUtil.dateToStringByFormat(new Date(), format);
		if (date.equals(todayString)) {
			return true;
		}
		return false;
	}
	
	public Odds filterOdds(String matchId, String companyId, OddsType oddsType) {
		switch (oddsType) {
		case YAPEI:
			for (YapeiOdds yapeiOdds : yapeiOddsList) {
				if (yapeiOdds.getCompanyId().equals(companyId) && yapeiOdds.getMatchId().equals(matchId)) {
					return yapeiOdds;
				}
			}
			break;
		case OUPEI:
			for (OupeiOdds oupeiOdds : oupeiOddsList) {
				if (oupeiOdds.getCompanyId().equals(companyId) && oupeiOdds.getMatchId().equals(matchId)) {
					return oupeiOdds;
				}
			}
			break;
		case DAXIAO:
			for (DaxiaoOdds daxiaoOdds : daxiaoOddsList) {
				if (daxiaoOdds.getCompanyId().equals(companyId) && daxiaoOdds.getMatchId().equals(matchId)) {
					return daxiaoOdds;
				}
			}
			break;

		default:
			break;
		}
		return null;
	}
	
	public Set<Odds> getOddsUpdateSet(List<String[]> list, OddsType oddsType) {
		if (list == null || list.size() == 0) {
			return null;
		}
		Set<Odds> set = new HashSet<Odds>();
		for (int i = 0; i < list.size(); i++) {
			String[] fields = list.get(i);
			if (fields.length < ScoreNetworkRequest.INDEX_REALTIME_ODDS_CHANGE_FIELD_NUM) {
				Log.w(TAG, "getOddsUpdateSet but field count not enough, count = "
								+ fields.length);
				continue;
			}
			
			String matchId = fields[ScoreNetworkRequest.INDEX_REALTIME_ODDS_MATCH_ID];
			String companyId = fields[ScoreNetworkRequest.INDEX_REALTIME_ODDS_COMPANY_ID];
			String homeTeamOdds = fields[ScoreNetworkRequest.INDEX_REALTIME_ODDS_INSTANT_HOME];
			String awayTeamOdds = fields[ScoreNetworkRequest.INDEX_REALTIME_ODDS_INSTANT_AWAY];
			String pankou = fields[ScoreNetworkRequest.INDEX_REALTIME_ODDS_INSTANT_PANKOU];
			Odds odds = filterOdds(matchId, companyId, oddsType);
			if (odds == null) {
//				Log.d(TAG, "not in current list, no need for odds update:" + "oddsType = " + oddsType  +  ",matchId = " + matchId + ",companyId = " + companyId);
				continue;
			}
			switch (oddsType) {
			case YAPEI:
				((YapeiOdds)odds).updateOdds(homeTeamOdds, awayTeamOdds, pankou);
				break;
			case OUPEI:
				((OupeiOdds)odds).updateOdds(homeTeamOdds, awayTeamOdds, pankou);
				break;
			case DAXIAO:
				((DaxiaoOdds)odds).updateOdds(homeTeamOdds, awayTeamOdds, pankou);
				break;
			default:
				break;
			}
			if (odds.homeTeamOddsFlag != 0 || odds.awayTeamOddsFlag !=0 || odds.pankouFlag != 0) {
//				Match match = findMatchById(odds.matchId);
//				Log.d(TAG, match.getHomeTeamName() + ":" + match.getAwayTeamName());
				set.add(odds);
			}
		}
		Log.d(TAG, "total update odds count = " + set.size());
		//test yapeiOdds data
//		YapeiOdds dd = (YapeiOdds) filterOdds("568687", "1", OddsType.YAPEI);
//		dd.updateOdds("0.6", "1.2", "-1.25");
//		set.add(dd);
		return set;
	}
}
