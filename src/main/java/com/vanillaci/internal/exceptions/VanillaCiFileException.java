package com.vanillaci.internal.exceptions;

/**
 * @author Joel Johnson
 */
public class VanillaCiFileException extends RuntimeException {
	public VanillaCiFileException(String message) {
		super(message);
	}

	public VanillaCiFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
