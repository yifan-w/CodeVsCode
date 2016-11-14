package com.codevscode.problem;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {
	private int elapsedTime;
	@JsonProperty("time")
	private double executionTime;
	private String status;
	private int memory;
	private int score;
	private Testcase failedTestcase;
	@JsonProperty("runtime_info")
	private RuntimeInfo runtimeInfo;
	private long id;
	private long uId;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getuId() {
		return uId;
	}

	public void setuId(long uId) {
		this.uId = uId;
	}

	public Result() {
		super();
	}

	public Result(int elapsedTime, double executionTime, String status, int memory, int score,
			Testcase failedTestcase) {
		this();
		this.elapsedTime = elapsedTime;
		this.executionTime = executionTime;
		this.status = status;
		this.memory = memory;
		this.score = score;
		this.failedTestcase = failedTestcase;
	}

	public Result(int elapsedTime, double executionTime, String status, int memory, int score, Testcase failedTestcase,
			String stdout, String stderr, String cmperr) {
		this(elapsedTime, executionTime, status, memory, score, failedTestcase);
		setRuntimeInfo(new RuntimeInfo(stdout, stderr, cmperr));
	}
	
	public Result(int elapsedTime, double executionTime, int memory, int score, Long uId, String name) {
		this.elapsedTime = elapsedTime;
		this.executionTime = executionTime;
		this.memory = memory;
		this.score = score;
		this.uId = uId;
		this.name = name;
	}

	public boolean isBlockingStatus() {
		return getStatus().equals("compiling...") || getStatus().equals("waiting...")
				|| getStatus().equals("running...") || getStatus().equals("waiting for compilation...")
				|| getStatus().equals("testing...") || getStatus().equals("running judge...");
	}

	public int getElapsedTime() {
		return elapsedTime;
	}

	public Duration getElapsedTimeDuration() {
		return Duration.ofSeconds(elapsedTime);
	}

	public void setElapsedTime(int seconds) {
		elapsedTime = seconds;
	}

	public double getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(double executionTime) {
		this.executionTime = executionTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Testcase getFailedTestcase() {
		return failedTestcase;
	}

	public void setFailedTestcase(Testcase failedTestcase) {
		this.failedTestcase = failedTestcase;
	}

	public RuntimeInfo getRuntimeInfo() {
		return runtimeInfo;
	}

	public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
		this.runtimeInfo = runtimeInfo;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RuntimeInfo {
		private String stdout;
		private String stderr;
		private String cmperr;

		public RuntimeInfo() {
			super();
		}
		
		public RuntimeInfo(String stdout, String stderr, String cmperr) {
			this();
			this.stdout = stdout;
			this.stderr = stderr;
			this.cmperr = cmperr;
		}

		public String getStdout() {
			return stdout;
		}

		public void setStdout(String stdout) {
			this.stdout = stdout;
		}

		public String getStderr() {
			return stderr;
		}

		public void setStderr(String stderr) {
			this.stderr = stderr;
		}

		public String getCmperr() {
			return cmperr;
		}

		public void setCmperr(String cmperr) {
			this.cmperr = cmperr;
		}

		@Override
		public String toString() {
			return "RuntimeInfo [stdout=" + stdout + ", stderr=" + stderr + ", cmperr=" + cmperr + "]";
		}

	}
}
