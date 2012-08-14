package com.orange.score.model.repository;

public class Continent {
	private String continentId;
	private String continentName;

	public Continent(String continentId, String name) {
		this.continentId = continentId;
		this.continentName = name;
	}

	@Override
	public String toString() {
		return "Continent [continentId=" + continentId + ", name="
				+ continentName + "]";
	}

	public String getContinentId() {
		return continentId;
	}

	public void setContinentId(String continentId) {
		this.continentId = continentId;
	}

	public String getContinentName() {
		return continentName;
	}

	public void setContinentName(String continentName) {
		this.continentName = continentName;
	}

}
