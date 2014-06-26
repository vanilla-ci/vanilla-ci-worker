package com.vanillaci.internal.util;

import java.io.*;
import java.net.*;

/**
 * @author Joel Johnson
 */
public class TestUtil {
	public static InputStream getResourceStream(String name) {
		URL resource = TestUtil.class.getClassLoader().getResource("testFile.txt");
		if (resource == null) {
			throw new RuntimeException("Unknown resource " + name);
		}

		try {
			return resource.openStream();
		} catch (IOException e) {
			throw new RuntimeException("Unable to open stream for " + resource.toString());
		}
	}
}
