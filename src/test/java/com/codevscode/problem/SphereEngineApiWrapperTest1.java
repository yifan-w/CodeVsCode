package com.codevscode.problem;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.Assert;

public class SphereEngineApiWrapperTest1 {

	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for testConnection();
	public void test() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		Assert.assertTrue(apiWrapper.testConnection());
	}

}
