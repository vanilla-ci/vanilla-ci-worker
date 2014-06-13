package com.vanillaci.internal.util;

import org.junit.*;

/**
 * @author Joel Johnson
 */
public class StringUtilTest {
	private static final long MILLI = 1;
	private static final long SECOND = MILLI * 1000;
	private static final long MINUTE = SECOND * 60;
	private static final long HOUR = MINUTE * 60;

	@Test
	public void millisToClockFormat_basic() {
		long value = (HOUR * 3) + (MINUTE * 5) + (SECOND * 7) + (MILLI * 15);
		String clockFormat = StringUtil.millisToClockFormat(value);
		assert "3:05:07.015".equals(clockFormat);
	}

	@Test
	public void millisToClockFormat_zero() {
		long value = 0;
		String clockFormat = StringUtil.millisToClockFormat(value);
		assert "0:00:00.000".equals(clockFormat);
	}
}
