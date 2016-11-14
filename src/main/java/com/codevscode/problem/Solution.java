package com.codevscode.problem;

public class Solution {

	private Long id;
	private Long cid;
	private String source;
	private Long user;
	private Long problemId;
	private int elapsedTime;
	private int language;
	private Long resultId;

	public Solution() {
		super();
		this.source = "";
		this.cid = 0L;
		resultId = 0L;
	}

	public Solution(Long id, String source, Long user, Long problemId, int language) {
		this();
		this.id = id;
		this.source = source;
		this.user = user;
		this.problemId = problemId;
		this.language = language;
	}
	
	public Solution(Long id, String source, Long user, Long problemId, int language, int elapsedTime, Long cid) {
		this(id, source, user, problemId, language);
		this.elapsedTime = elapsedTime;
		this.cid = cid;
	}
	
	public Solution(Long id, String source, Long user, Long problemId, int language, int elapsedTime) {
		this(id, source, user, problemId, language);
		this.setElapsedTime(elapsedTime);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public Long getProblemId() {
		return problemId;
	}

	public void setProblemId(Long problemId) {
		this.problemId = problemId;
	}

	public int getLanguage() {
		return language;
	}
	
	public Long getCid() {
		return cid;
	}
	
	public void setCid(Long cid) {
		this.cid = cid;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public int getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(int elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public void setElapsedTime(double elapsedTime) {
		this.elapsedTime = (int) elapsedTime;
	}
	
	public void setResultId(Long resultId) {
		this.resultId = resultId;
	}
	
	public Long getResultId() {
		return resultId;
	}

	@Override
	public String toString() {
		return "Solution [id=" + id + ", cid=" + cid + ", source=" + source + ", user=" + user + ", problemId="
				+ problemId + ", elapsedTime=" + elapsedTime + ", language=" + language + "]";
	}
}
