package com.codevscode.problem;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import junit.framework.Assert;

public class SphereEngineApiWrapperTest3 {
	public static final Logger log = LoggerFactory.getLogger(SphereEngineApiWrapperTest3.class);

	@SuppressWarnings("deprecation")
	@Test
	//JUnit test for create problem testcase with invalid source.
	public void test1() {
		SphereEngineApiWrapper apiWrapper = new SphereEngineApiWrapper();
		Assert.assertEquals(apiWrapper.createProblemTestcase("PRONOTF", "test_name", "test_body",12D),-1);
	}
	
}
