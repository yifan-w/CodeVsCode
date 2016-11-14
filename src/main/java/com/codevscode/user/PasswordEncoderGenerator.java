package com.codevscode.user;

import javax.annotation.PostConstruct;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderGenerator {

	private BCryptPasswordEncoder passwordEncoder;

	@PostConstruct
	public void init() {
		passwordEncoder = new BCryptPasswordEncoder();
	}

	/**
	 * generates a salted password with the salt included
	 * 
	 * @param password
	 *            raw password the user enters
	 * @return a salted password with the salt included
	 */
	public String getHashPassword(String password) {
		String hashedPassword = passwordEncoder.encode(password);
		return hashedPassword;
	}

	/**
	 * checks a raw password against an encoded password
	 * 
	 * @param rawPassword
	 *            the plain text password entered by the user to log in
	 * @param encodedPassword
	 *            the encoded password saved by the database
	 * @return true if it matches, false if it doesn't
	 */
	public boolean authenticatePassword(String rawPassword, String encodedPassword) {

		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
}
