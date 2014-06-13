package com.vanillaci.internal.util;

import org.apache.logging.log4j.*;

/**
 * @author Joel Johnson
 */
public class StopWatch {
	private final Logger log;

	public StopWatch(Logger log) {
		this.log = log;
	}

	/**
	 * Logs the amount of time it takes to run the given function.
	 * The given name is included in the logs.
	 */
	public <E extends Exception> long time(String name, TimedFunction<E> timedFunction) throws E {
		long start = System.currentTimeMillis();
		long runTime;
		try {
			timedFunction.execute();
		} finally {
			runTime = System.currentTimeMillis() - start;

			if(log.isInfoEnabled()) {
				log.info("---------------");
				log.info(String.format("Run time for '%s': %s", name, StringUtil.millisToClockFormat(runTime)));
				log.info("---------------");
			}
		}

		return runTime;
	}

	public static interface TimedFunction<E extends Exception> {
		void execute() throws E;
	}
}
