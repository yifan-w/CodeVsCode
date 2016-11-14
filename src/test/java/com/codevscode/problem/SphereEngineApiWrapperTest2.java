package com.codevscode.problem;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.Assert;

public class SphereEngineApiWrapperTest2 {

	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for create problem with empty problem code
	public void test1() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		Assert.assertEquals(apiWrapper.createProblem("", "test_name", "test_body")
				,"400 Empty problem code");
	}
	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for create problem with empty problem name;
	public void test2() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		//have to rename the new_code;
		String new_code = "NEWCODE1";
		Assert.assertEquals(apiWrapper.createProblem(new_code, "", "test_body")
				,"400 Empty problem name");
	}
	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for create problem with invalid problem code
	public void test3() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		Assert.assertEquals(apiWrapper.createProblem("---", "test_name", "test_body")
				,"400 Problem code is invalid");
	}
	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for create problem with used problem code.
	public void test4() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		apiWrapper.createProblem("NOTUNIQUE", "test_name", "test_body");
		Assert.assertEquals(apiWrapper.createProblem("NOTUNIQUE", "test_name", "test_body")
				,"400 Problem code already used");
	}

}
