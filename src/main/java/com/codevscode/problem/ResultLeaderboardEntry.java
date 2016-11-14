package com.codevscode.problem;

import com.codevscode.user.User;

public class ResultLeaderboardEntry implements Comparable<ResultLeaderboardEntry> {
	private Result result;
	private User user;
	private Solution solution;

	public ResultLeaderboardEntry() {
		super();
	}

	public ResultLeaderboardEntry(Result result, User user, Solution solution) {
		super();
		this.result = result;
		this.user = user;
		this.solution = solution;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	@Override
	public int compareTo(ResultLeaderboardEntry entry) {
		// TODO Auto-generated method stub
		if ((entry.getResult() == null) && result != null) {
			return 1;
		} else if (result == null && entry.getResult() != null) {
			return -1;
		} else if (result == null && entry.getResult() == null) {
			return 0;
		}

		if (entry.getResult().getExecutionTime() != result.getExecutionTime()) {
			double diff = result.getExecutionTime() - entry.getResult().getExecutionTime();
			return (int) (-diff / Math.abs(diff));
		}

		if (entry.getResult().getElapsedTime() != result.getElapsedTime()) {
			return -(result.getElapsedTime() - entry.getResult().getElapsedTime());
		}

		if (entry.getResult().getMemory() != result.getMemory()) {
			return -(result.getMemory() - entry.getResult().getMemory());
		}

		return 0;
	}

}
