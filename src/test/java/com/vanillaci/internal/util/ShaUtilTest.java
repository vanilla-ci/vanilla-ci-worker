package com.vanillaci.internal.util;

import org.junit.*;

import java.io.*;

/**
 * @author Joel Johnson
 */
public class ShaUtilTest {
	@Test
	public void shaTest() throws IOException {
		String sha1 = ShaUtil.getSha(TestUtil.getResourceStream("testFile.txt"));
		Assert.assertEquals("27a0d9074218b8048e9fe1d89c8f70149adc20e6", sha1);
	}
}
