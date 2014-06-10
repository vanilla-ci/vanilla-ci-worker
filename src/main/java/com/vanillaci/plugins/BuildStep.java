package com.vanillaci.plugins;

import org.jetbrains.annotations.*;

/**
 * @author Joel Johnson
 */
public interface BuildStep {
	public Result execute(BuildStepContext context) throws Exception;

	public static enum Result {
		/**
		 * The result when the build finished without any problems.
		 */
		SUCCESS,

		/**
		 * Typically means no exceptions were thrown,
		 * but minor problems were found that are not severe enough to prevent other steps to continue.
		 * e.g. test failures.
		 */
		FAILURE_CONTINUE,

		/**
		 * There were problems detected that should prevent other build steps from running.
		 * e.g. an external machine isn't running or a required website is down.
		 */
		FAILURE_HALT,

		/**
		 * An unexpected error was found. This is typically only used if there was an unhandled exception thrown by the BuildStep.
		 */
		ERROR,

		/**
		 * The BuildStep was canceled while it was running. Typically because a user or plugin canceled the run.
		 */
		ABORTED;

		public boolean isBetterThan(@NotNull Result that) {
			return this.compareTo(that) < 0;
		}

		public boolean isBetterThanOrEqualTo(@NotNull Result that) {
			return this.compareTo(that) <= 0;
		}

		public boolean isWorseThan(@NotNull Result that) {
			return this.compareTo(that) > 0;
		}

		public boolean isWorseThanOrEqualTo(@NotNull Result that) {
			return this.compareTo(that) >= 0;
		}
	}
}
