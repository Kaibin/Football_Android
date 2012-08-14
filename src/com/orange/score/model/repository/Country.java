package com.orange.score.model.repository;

public class Country {
	String countryId;
	String countryName;
	String continentId;

	public Country(String countryId, String countryName, String continentId) {
		super();
		this.countryId = countryId;
		this.countryName = countryName;
		this.continentId = continentId;
	}

	@Override
	public String toString() {
		return "Country [countryId=" + countryId + "continentId ="
				+ continentId + ", countrynName=" + countryName + "]";
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getContinentId() {
		return continentId;
	}

	public void setContinentId(String continentId) {
		this.continentId = continentId;
	}

}
