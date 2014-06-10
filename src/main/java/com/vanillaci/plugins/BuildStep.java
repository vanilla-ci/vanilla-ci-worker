package com.vanillaci.plugins;

import org.jetbrains.annotations.*;

/**
 * Extension point for creating custom build-steps.
 * Use the provided BuildStepContext parameter to set status, halt the build process, access build parameters, add build parameters, or access other useful objects.
 *
 * @author Joel Johnson
 */
public interface BuildStep {
	public void execute(BuildStepContext context) throws Exception;

	/**
	 * The result of the build. This is to help give the user an at-a-glance idea of what happened with the build.
	 */
	public static enum Result {
		/**
		 * The result when the build finished without any problems.
		 */
		SUCCESS,

		/**
		 * Sanity checks have failed or tests have failed.
		 */
		FAILURE,

		/**
		 * An unexpected error occurred. This is typically only used if there was an unhandled exception thrown by the BuildStep.
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

	/**
	 * Lets to worker know whether or not it should continue running build steps.
	 * It doesn't matter what the Result is, the worker determines whether or not to continue solely on this status.
	 */
	public static enum Status {
		/**
		 * Tells the worker to continue running build steps.
		 */
		CONTINUE,

		/**
		 * Tells the worker to stop running build steps.
		 */
		HALT,

		/**
		 * Tells the worker that we're in the post-build phase and that we should attempt to continue running all post build steps no matter what.
		 * POST_BUILD and CONTINUE effectively behave the same way,
		 * however, {@link com.vanillaci.plugins.BuildStepContext#setResult(com.vanillaci.plugins.BuildStep.Result, com.vanillaci.plugins.BuildStep.Status)}
		 * will not override POST_BUILD like it will CONTINUE.
		 */
		POST_BUILD
	}
}
