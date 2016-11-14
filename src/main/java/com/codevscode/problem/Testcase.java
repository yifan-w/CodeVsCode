package com.codevscode.problem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Testcase {
	private String input;
	private String output;
	private Double timelimit;
	private Integer testcaseNumber;

	public Testcase() {
		this.input = "";
		this.output = "";
		this.timelimit = 1.0;
		this.testcaseNumber = 0;
	}
	
	public Testcase(int testcaseNumber) {
		this();
		this.testcaseNumber = testcaseNumber;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Double getTimelimit() {
		return timelimit;
	}

	public void setTimelimit(Double timelimit) {
		timelimit = Math.max(1.0, Math.min(5.0, timelimit));
		this.timelimit = timelimit;
	}

	public int getTestcaseNumber() {
		return testcaseNumber;
	}

	public void setTestcaseNumber(Integer testcasenumber) {
		this.testcaseNumber = testcasenumber;
	}

}
