package com.vanillaci.internal.util;

import org.jetbrains.annotations.*;

import java.util.regex.*;

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


	private static final Pattern nonFileCharacters = Pattern.compile("[\0/<>:'\"\\\\|?*~\\s]");
	/**
	 * Replaces all non-alpha-numeric characters with the given character
	 */
	@NotNull
	public static String sanitizeFilename(@NotNull String stringToSanitize) {
		Matcher matcher = nonFileCharacters.matcher(stringToSanitize);
		return matcher.replaceAll("_");
	}
}
