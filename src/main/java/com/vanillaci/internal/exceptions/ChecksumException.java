package com.vanillaci.internal.exceptions;

/**
 * @author Joel Johnson
 */
public class ChecksumException extends RuntimeException {
	public ChecksumException(String path, String expectedHash, String actualHash) {
		super("Checksum failed for '" + path + "'. Expected <" + expectedHash + "> but was <" + actualHash + ">.");
	}
}
