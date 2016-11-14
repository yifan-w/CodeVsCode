package com.codevscode;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.codevscode.user.PasswordEncoderGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebCodeProblemsApplication.class)
@WebAppConfiguration
public class PasswordEncoderTest {

	@Autowired
	private PasswordEncoderGenerator encoder;
	
	@Test 
	public void testHash() {
		String password = "aoeutnhaoeutnsh";
		String encodedPassword = encoder.getHashPassword(password);
		Assert.assertTrue(encoder.authenticatePassword(password, encodedPassword));
	}
	
	@Test
	public void testEmpty() {
		String password = "";
		String encodedPassword = encoder.getHashPassword(password);
		Assert.assertTrue(encoder.authenticatePassword(password, encodedPassword));
	}
	
	@Test
	public void testNumbers() {
		String password = "3088923475892";
		String encodedPassword = encoder.getHashPassword(password);
		Assert.assertTrue(encoder.authenticatePassword(password, encodedPassword));
	}
	
	@Test
	public void testWrongPassword() {
		String password = "aonehutnao";
		String wrongPassword = "aotneuhao";
		String encodedPassword = encoder.getHashPassword(password);
		Assert.assertFalse(encoder.authenticatePassword(wrongPassword, encodedPassword));
	}
	
	@Test
	public void testSamePassword () {
		String password = "oaeuaotnehuns";
		Assert.assertFalse(encoder.authenticatePassword(password, password));
	}
}
