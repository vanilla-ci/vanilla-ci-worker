package com.vanillaci.internal.util;

/**
 * @author Joel Johnson
 */
public class StringUtil {
	/**
	 * Takes the given amount of milliseconds and converts it to a string format that resembles a digital clock:
	 *
	 * e.g.: <code>0:00:00.000</code>
	 *
	 */
	public static String millisToClockFormat(long millis) {
		long seconds = millis / 1000;
		millis = millis % 1000;

		long minutes = seconds / 60;
		seconds = seconds % 60;

		long hours = minutes / 60;
		minutes = minutes % 60;

		return String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, millis);
	}
}
