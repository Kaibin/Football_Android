package com.orange.score.model.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.orange.score.network.RepositoryNetworkRequest;
import com.orange.score.network.ScoreNetworkRequest;

public class CompanyManager {
	private static String TAG = OddsManager.class.getName();
	List<Company> oupeiCompanyList = new ArrayList<Company>();
	List<Company> daxiaoCompanyList = new ArrayList<Company>();
	List<Company> yapeiCompanyList = new ArrayList<Company>();
	List<String> defaultSelectedCompanyIdList = new ArrayList<String>();
	OddsType oddsType = OddsType.YAPEI;
	Map<String, Company> companyMap = new HashMap<String, Company>();
	
	private Set<Company> yapeiSelectedSet = new HashSet<Company>();
	private Set<Company> oupeiSelectedSet = new HashSet<Company>();
	private Set<Company> daxiaoSelectedSet = new HashSet<Company>();
	
	public Set<Company> getYapeiSelectedSet() {
		return yapeiSelectedSet;
	}

	public void setYapeiSelectedSet(Set<Company> yapeiSelectedSet) {
		this.yapeiSelectedSet = yapeiSelectedSet;
	}

	public Set<Company> getOupeiSelectedSet() {
		return oupeiSelectedSet;
	}

	public void setOupeiSelectedSet(Set<Company> oupeiSelectedSet) {
		this.oupeiSelectedSet = oupeiSelectedSet;
	}

	public Set<Company> getDaxiaoSelectedSet() {
		return daxiaoSelectedSet;
	}

	public void setDaxiaoSelectedSet(Set<Company> daxiaoSelectedSet) {
		this.daxiaoSelectedSet = daxiaoSelectedSet;
	}

	public List<String> getDefaultSelectedCompanyIdList() {
		return defaultSelectedCompanyIdList;
	}

	public void setDefaultSelectedCompanyIdList(
			List<String> defaultSelectedCompanyIdList) {
		this.defaultSelectedCompanyIdList = defaultSelectedCompanyIdList;
	}

	public OddsType getOddsType() {
		return oddsType;
	}

	public void setOddsType(OddsType oddsType) {
		this.oddsType = oddsType;
	}
	public void updateCompanyDataFromStringList(String[] stringList) {
		if (stringList == null || stringList.length == 0) {
			return;
		}
		
		oupeiCompanyList.clear();
		daxiaoCompanyList.clear();
		yapeiCompanyList.clear();
		companyMap.clear();
		
		for (int i = 0; i < stringList.length; i++) {
			String[] fields = stringList[i].split(RepositoryNetworkRequest.SEP_FIELD);

			if (fields.length < ScoreNetworkRequest.INDEX_REALINDEX_COUNT) {
				Log.w(TAG, "updateCompanyDataFromStringList but field count not enough, count = "
								+ fields.length);
				continue;
			}

			String companyId = fields[ScoreNetworkRequest.INDEX_REALINDEX_COMPANY_ID];
			String companyName = fields[ScoreNetworkRequest.INDEX_REALINDEX_COMPANY_NAME];
			String haveYapei = fields[ScoreNetworkRequest.INDEX_REALINDEX_COMPANY_HAVE_YAPEI];
			String haveOupei = fields[ScoreNetworkRequest.INDEX_REALINDEX_COMPANY_HAVE_OUPEI];
			String haveDaxiao = fields[ScoreNetworkRequest.INDEX_REALINDEX_COMPANY_HAVE_DAXIAO];
			Company company = new Company(companyId, companyName, haveYapei, haveDaxiao, haveOupei);
			Log.d(TAG, "add company = " + company.toString());
			classifyCompany(company);
			companyMap.put(companyId, company);
		}
		//设置默认选中的四家亚赔公司
		defaultSelectedCompanyIdList.clear();
		for (int i = 0; i < 4; i++) {
			defaultSelectedCompanyIdList.add(yapeiCompanyList.get(i).getCompanyId());
		}
	}
	
	private void classifyCompany(Company company) {
		if (company.haveYapei) {
			yapeiCompanyList.add(company);
		}
		if (company.haveDaxiao) {
			daxiaoCompanyList.add(company);
		}
		if (company.haveOupei) {
			oupeiCompanyList.add(company);
		}
	}
	
	public List<Company> getYapeiCompanyList() {
		return yapeiCompanyList;
	}
	
	public List<Company> getDaxiaoCompanyList() {
		return daxiaoCompanyList;
	}
	
	public List<Company> getOupeiCompanyList() {
		return oupeiCompanyList;
	}
	
	public String getCompanyNameById(String companyId) {
		return companyMap.get(companyId).companyName;
	}
}
