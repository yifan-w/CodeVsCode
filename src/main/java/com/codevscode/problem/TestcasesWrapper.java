package com.codevscode.problem;

import java.util.ArrayList;
import java.util.List;

public class TestcasesWrapper {
	private List<Testcase> list;

	public TestcasesWrapper() {
		super();
		this.list = new ArrayList<>();
	}

	public List<Testcase> getList() {
		return list;
	}

	public void setList(List<Testcase> list) {
		this.list = list;
	}
}