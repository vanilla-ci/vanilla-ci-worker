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
	 * @param context		  The context that will be passed into the {@link com.vanillaci.plugins.BuildStep}.
	 * @param nextBuildStep   The {@link com.vanillaci.plugins.BuildStep} that is about to be called.
	 * @param buildStepsIndex The index the BuildStep is in its respective list (zero-based). Or in other words, how many build steps have been run in the current phase before the given build step.
	 * @param totalBuildSteps The number of BuildSteps that will be run total, in this phase (assuming everything goes well).
	 * @return {@link com.vanillaci.plugins.BuildStepInterceptor.InterceptorStatus#RUN} if the build step and {@link #after(BuildStepContext, BuildStep, int, int)} method should run. {@link com.vanillaci.plugins.BuildStepInterceptor.InterceptorStatus#SKIP} if they should not run.
	 * 			Typically, {@link com.vanillaci.plugins.BuildStepInterceptor.InterceptorStatus#RUN} will be used, but this functionality is provided because sometimes it is nice to be able to
	 * 			skip all or some build steps with out erroring out. A null value will be treated as {@link com.vanillaci.plugins.BuildStepInterceptor.InterceptorStatus#RUN}.
	 */
	InterceptorStatus before(BuildStepContext context, BuildStep nextBuildStep, int buildStepsIndex, int totalBuildSteps);

	/**
	 * Called immediately <em>after</em> every build step.
	 * This is always called if the {@link #before(BuildStepContext, BuildStep, int, int)} method is called. Even if the build step throws an exception (but it will not be called if the {@link #before(BuildStepContext, BuildStep, int, int)} method throws an exception or the {@link #before(BuildStepContext, BuildStep, int, int)} method is skipped).
	 * @param context the context that was passed into the {@link com.vanillaci.plugins.BuildStep}
	 * @param previousBuildStep the {@link com.vanillaci.plugins.BuildStep} that just finished running.
	 * @param buildStepsIndex The index the BuildStep is in its respective list (zero-based). Or in other words, how many build steps have been run in the current phase before the given build step.
	 * @param totalBuildSteps The number of BuildSteps that will be run total, in this phase (assuming everything goes well).
	 */
	void after(BuildStepContext context, BuildStep previousBuildStep, int buildStepsIndex, int totalBuildSteps);

	public static enum InterceptorStatus {
		RUN,
		SKIP
	}
}
