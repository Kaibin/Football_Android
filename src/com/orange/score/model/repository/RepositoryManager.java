package com.orange.score.model.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.R.bool;
import android.util.Log;

import com.orange.score.constants.FilterContinentType;
import com.orange.score.model.league.League;
import com.orange.score.network.RepositoryNetworkRequest;

public class RepositoryManager {

	private static String TAG = RepositoryManager.class.getName();
	List<Continent> continentList = new ArrayList<Continent>();
	List<Country> countryList = new ArrayList<Country>();
	List<League> leagueList = new ArrayList<League>();
	List<CupMatchType> matchTypesList = new ArrayList<CupMatchType>();
	FilterContinentType filterContinentType = FilterContinentType.EUROPE;

	public void updateContinentDataFromStringList(String[] stringList) {
		if (stringList == null || stringList.length == 0)
			return;

		// clear all old data
		continentList.clear();

		for (int i = 0; i < stringList.length; i++) {

			String[] fields = stringList[i]
					.split(RepositoryNetworkRequest.SEP_FIELD);

			if (fields.length < RepositoryNetworkRequest.INDEX_REPOSITORY_CONTINENT_COUNT) {
				Log.w(TAG,
						"updateContinentDataFromStringList but field count not enough, count = "
								+ fields.length);
				continue;
			}

			String continentId = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_CONTINENT_ID];
			String name = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_CONTINENT_NAME];

			Continent continent = new Continent(continentId, name);
			Log.d(TAG, "add continent = " + continent.toString());

			continentList.add(continent);
			// leagueMap.put(leagueId, league);
		}
	}

	public void updateCountryDataFromStringList(String[] stringList) {
		if (stringList == null || stringList.length == 0)
			return;

		// clear all old data
		countryList.clear();

		for (int i = 0; i < stringList.length; i++) {

			String[] fields = stringList[i]
					.split(RepositoryNetworkRequest.SEP_FIELD);

			if (fields.length < RepositoryNetworkRequest.INDEX_REPOSITORY_COUNTRY_COUNT) {
				Log.w(TAG,
						"updateCountryDataFromStringList but field count not enough, count = "
								+ fields.length);
				continue;
			}

			String countryId = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_COUNTRY_ID];
			String continentId = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_COUNTRY_CONTINENT_ID];
			String countryName = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_COUNTRY_NAME];

			Country country = new Country(countryId, countryName, continentId);
			Log.d(TAG, "add country = " + country.toString());
			countryList.add(country);
		}

	}

	public void updateLeagueDataFromStringList(String[] stringList) {
		if (stringList == null || stringList.length == 0)
			return;
		
		leagueList.clear();
		for (int i = 0; i < stringList.length; i++) {

			String[] fields = stringList[i]
					.split(RepositoryNetworkRequest.SEP_FIELD);

			if (fields.length < RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_COUNT) {
				Log.w(TAG,
						"updateLeagueDataFromStringList but field count not enough, count = "
								+ fields.length);
				continue;
			}

			String leagueId = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_ID];
			String countryId = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_COUNTRY_ID];
			String leagueName = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_NAME];
			String leagueShortName = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_SHORTNAME];
			String leagueType = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_TYPE];
			String seasonListString = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_LEAGUE_SEASON_LIST];
			String[] seasonArray = seasonListString.split(",");
			List<String> seasonList = Arrays.asList(seasonArray);

			League league = new League();
			league.setLeagueId(leagueId);
			league.setCountryId(countryId);
			league.setName(leagueName);
			league.setShortName(leagueShortName);
			league.setType(Integer.parseInt(leagueType));
			league.setSeasonList(seasonList);
			Log.d(TAG, "add league = " + league.toString());
			leagueList.add(league);
		}
	}

	public List<Continent> findAllContinent() {
		return continentList;
	}
	
	public List<League> findAllLeagues() {
		return leagueList;
	}

	public void setFilterContinent(FilterContinentType whichContinent) {
		filterContinentType = whichContinent;
	}

	public FilterContinentType getFilterContinent() {
		return filterContinentType;
	}

	public List<Country> filterCountry() {
		List<Country> list = new ArrayList<Country>();
		if (countryList == null || countryList.size() == 0) {
			return null;
		}
		for (Country country : countryList) {
			if (Integer.parseInt(country.continentId) == getFilterContinent()
					.intValue()) {
				list.add(country);
			}
		}
		return list;
	}
	
	public Country getCountryById(String countryId) {
		for (Country country : countryList) {
			if (country.getCountryId().endsWith(countryId)) {
				return country;
			}
		}
		return null;
	}

	public void updateGroupInfoFromStringList(String[] stringList) {
		if (stringList == null || stringList.length == 0)
			return;

		// clear all old data
		matchTypesList.clear();

		for (int i = 0; i < stringList.length; i++) {

			String[] fields = stringList[i]
					.split(RepositoryNetworkRequest.SEP_FIELD);

			if (fields.length < RepositoryNetworkRequest.INDEX_REPOSITORY_CUP_GROUP_COUNT) {
				Log.w(TAG,
						"updateGroupInfoFromStringList but field count not enough, count = "
								+ fields.length);
				continue;
			}

			String leagueId = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_CUP_GROUP_LEAGUE_ID];
			String season = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_CUP_GROUP_SEASON];
			String groupId = fields[RepositoryNetworkRequest.INDEX_REPOSITORY_CUP_GROUP_GROUP_ID];

			CupMatchType cupMatchType = new CupMatchType(leagueId, season,groupId);
			Log.d(TAG, "add CupMatchType = " + cupMatchType.toString());

			matchTypesList.add(cupMatchType);
			// leagueMap.put(leagueId, league);

		}
	}
	
	public List<CupMatchType> getCupMatchTypeList() {
		return matchTypesList;
	}
	
	public List<League> getLeagueListByKey(List<League> leagueList, String key) {
		if (leagueList == null || leagueList.size() == 0) {
			return null;
		}
		key = key.trim();
		List<League> list = new ArrayList<League>();
		
		for (League league : leagueList) {
			//search in league name
			if (findKeyInContent(key, league.getLeagueName())) {
				list.add(league);
				continue;
			}
			//search in country name
			Country country = getCountryById(league.getCountryId());
			if (country == null || country.getCountryName() == null || country.getCountryName().length() == 0) {
				continue;
			}
			if (findKeyInContent(key, country.getCountryName())) {
				list.add(league);
				continue;
			}
		}
		
		return list;
	}
	
	private boolean findKeyInContent(String key, String content){
		int count = 0;
		for (int i = 0; i < key.length(); i++) {
			String k = key.substring(i,i+1);
			if (content.contains(k)) {
				count ++;
			}
		}
		if (count == key.length()) {
			return true;
		}
		return false;
	}
}