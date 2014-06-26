package com.vanillaci.internal.util;

import java.io.*;
import java.security.*;
import java.util.*;

/**
 * @author Joel Johnson
 */
public class StreamUtil {
	public static String getSha(InputStream inputStream) throws IOException {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			return getHash(inputStream, messageDigest);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getHash(InputStream inputStream, MessageDigest messageDigest) throws IOException {
		final byte[] buffer = new byte[1024];
		for (int read = 0; (read = inputStream.read(buffer)) != -1;) {
			messageDigest.update(buffer, 0, read);
		}

		// Convert the byte to hex format
		try (Formatter formatter = new Formatter()) {
			for (final byte b : messageDigest.digest()) {
				formatter.format("%02x", b);
			}
			return formatter.toString();
		}
	}
}
