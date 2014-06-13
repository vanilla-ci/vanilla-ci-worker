package com.vanillaci.plugins;

/**
 * A plugin that allows code to be run before and/or after a build step is run.
 * For example, sending status updates to a remote server for collecting stats.
 *
 * @author Joel Johnson
 */
public interface BuildStepInterceptor {
	/**
	 * Called immediately <em>before</em> every build step.
	 * @param context The context that will be passed into the {@link com.vanillaci.plugins.BuildStep}.
	 */
	void before(BuildStepContext context);

	/**
	 * Called immediately <em>after</em> every build step.
	 * This is always called if the {@link #before} method is called. Even if the build step throws an exception (but it will not be called if the {@link #before} method throws an exception or the {@link #before} method is skipped).
	 * @param context The context that was passed into the {@link com.vanillaci.plugins.BuildStep}
	 */
	void after(BuildStepContext context);
}
