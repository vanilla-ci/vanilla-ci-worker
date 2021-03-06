package com.vanillaci.internal.util;

import java.io.*;
import java.security.*;
import java.util.*;

/**
 * Contains various methods for easily calculating a Sha1 on a file or stream.
 *
 * @author Joel Johnson
 */
public class ShaUtil {
	public static String getSha(File pluginFile) throws IOException {
		try(InputStream inputStream = new FileInputStream(pluginFile)) {
			return ShaUtil.getSha(inputStream);
		}
	}

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
