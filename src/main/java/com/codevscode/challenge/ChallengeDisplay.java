package com.codevscode.challenge;

public class ChallengeDisplay {
	private Long cId;
	private String usersString;
	private String problemTitle;
	private int userScore;

	public ChallengeDisplay() {
		this.usersString = null;
		this.problemTitle = null;
		this.cId = null;
	}

	public String getUsersString() {
		return usersString;
	}

	public void setUsersString(String usersString) {
		this.usersString = usersString;
	}

	public String getProblemTitle() {
		return problemTitle;
	}

	public void setProblemTitle(String problemTitle) {
		this.problemTitle = problemTitle;
	}
	
	public int getUserScore() {
		return userScore;
	}
	
	public void setUserScore(int userScore) {
		this.userScore = userScore;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}
}