package com.orange.score.model.index;


public class Company {
	String companyId;
	String companyName;
	boolean haveYapei;
	boolean haveDaxiao;
	boolean haveOupei;
	
	public Company(String companyId, String companyName, String haveYapei, String haveDaxiao, String haveOupei) {
		this.companyId = companyId;
		this.companyName = companyName;
		this.haveYapei = haveYapei.equals("1") ? true : false;
		this.haveDaxiao = haveDaxiao.equals("1") ? true : false;
		this.haveOupei = haveOupei.equals("1") ? true : false;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public boolean isHaveYapei() {
		return haveYapei;
	}

	public void setHaveYapei(boolean haveYapei) {
		this.haveYapei = haveYapei;
	}

	public boolean isHaveDaxiao() {
		return haveDaxiao;
	}

	public void setHaveDaxiao(boolean haveDaxiao) {
		this.haveDaxiao = haveDaxiao;
	}

	public boolean isHaveOupei() {
		return haveOupei;
	}

	public void setHaveOupei(boolean haveOupei) {
		this.haveOupei = haveOupei;
	}

	@Override
	public String toString() {
		return "Company [companyId=" + companyId + ", companyName="
				+ companyName + ", haveYapei=" + haveYapei + ", haveDaxiao="
				+ haveDaxiao + ", haveOupei=" + haveOupei + "]";
	}
	
}
