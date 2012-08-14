package com.orange.score.model.repository;

public class CupMatchType {
	String matchTypeId;
	String matchTypeName;
	String isCurrentType;

	
	public CupMatchType(String matchTypeId, String matchTypeName,
			String isCurrentType) {
		super();
		this.matchTypeId = matchTypeId;
		this.matchTypeName = matchTypeName;
		this.isCurrentType = isCurrentType;
	}

	@Override
	public String toString() {
		return "[CupMatchType:" + matchTypeId + "matchTypeName:"
				+ matchTypeName + "isCurrentType:" + isCurrentType + "]";
	}

	public String getMatchTypeId() {
		return matchTypeId;
	}

	public void setMatchTypeId(String matchTypeId) {
		this.matchTypeId = matchTypeId;
	}

	public String getMatchTypeName() {
		return matchTypeName;
	}

	public void setMatchTypeName(String matchTypeName) {
		this.matchTypeName = matchTypeName;
	}

	public String getIsCurrentType() {
		return isCurrentType;
	}

	public void setIsCurrentType(String isCurrentType) {
		this.isCurrentType = isCurrentType;
	}

}
