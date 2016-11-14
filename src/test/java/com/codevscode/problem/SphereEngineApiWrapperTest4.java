package com.codevscode.problem;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.Assert;

public class SphereEngineApiWrapperTest4 {

	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for creat submission with 0 compilercode;
	public void test1() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		Assert.assertEquals(apiWrapper.createSubmission("PRONOTF",0,"test_source"),-1);
	}
	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for creat submission with -1 compilercode;

	public void test2() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		Assert.assertEquals(apiWrapper.createSubmission("TEST",-1,"test_source"),-1);
	}
	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for creat submission with 1000 compilercode;

	public void test3() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		Assert.assertEquals(apiWrapper.createSubmission("TEST",1000,"test_source"),-1);
	}
	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for creat submission with empty source code;

	public void test4() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		Assert.assertEquals(apiWrapper.createSubmission("TEST",1,""),-1);
	}

}
