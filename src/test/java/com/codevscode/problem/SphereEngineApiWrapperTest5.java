package com.codevscode.problem;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.Assert;

public class SphereEngineApiWrapperTest5 {

	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for fetch submission detail with testcase number 1
	public void test1() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		Assert.assertNotNull(apiWrapper.fetchSubmissionDetails(1));
	}
}
