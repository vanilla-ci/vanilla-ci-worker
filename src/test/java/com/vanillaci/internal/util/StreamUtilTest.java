package com.vanillaci.internal.util;

import org.junit.*;

import java.io.*;
import java.net.*;

/**
 * @author Joel Johnson
 */
public class StreamUtilTest {
	@Test
	public void sha512Test() throws IOException {
		String sha512 = StreamUtil.getSha512(TestUtil.getResourceStream("testFile.txt"));
		Assert.assertEquals("bb5690685132f19ed329a9a38b1c4a5ad2c1c4269407410b71ba22b0d643bf4c7f74df438b5b910448ca805281b20000d4c423b9fae43ee8c8148d4d33fae60b", sha512);
	}
}
