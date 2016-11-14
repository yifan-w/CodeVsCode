package com.codevscode.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2681590656858899598L;

	public ResourceNotFoundException() {
		super("Resource could not be found");
	}
	
	public ResourceNotFoundException(String resource) {
		super(resource + " could not be found");
	}
}
