package com.vanillaci.plugins;

import java.io.*;
import java.util.*;

/**
 * Provides context for BuildSteps.
 * Allows plugin developers to easily access things like the working directory of the job, the SDK, and parameters.
 *
 * @author Joel Johnson
 */
public interface BuildStepContext {
	/**
	 * All the parameters provided globally, specifically to this build step, or provided by previous buildSteps
	 * To add parameters for later buildSteps, use the {@link #addParameter(String, String)} method.
	 * @return read-only map of the parameters.
	 */
	Map<String, String> getParameters();

	/**
	 * @return The working directory of all the Work. Creating or touching files outside of this directory should be avoided.
	 */
	File getWorkspace();

	Sdk getSdk();

	/**
	 * Adds the given parameter to the parameter map for future build steps.
	 */
	void addParameter(String parameterName, String parameterValue);

	/**
	 * Returns the last known result of all the buildSteps. Typically, it's the worst result of all build steps that have run so far,
	 * but it's possible, albeit rare, that another buildStep has overwritten the value.
	 */
	BuildStep.Result getResult();

	/**
	 * Returns the last known status of all the buildSteps.
	 * @return Typically CONTINUE will be returned if it's in the Build phase,
	 * POST_BUILD will be returned if it's in the PostBuild phase,
	 * and HALT will be returned if it's in the Build phase and the last build step errored out, or was told to halt by another plugin.
	 */
	BuildStep.Status getStatus();

	/**
	 * Manually overwrites the Result and Status.
	 * Typically you will want to use {@link #setResult(com.vanillaci.plugins.BuildStep.Result, com.vanillaci.plugins.BuildStep.Status)} instead.
	 * This would be used if the plugin needs to set the result or status to a better state than it is currently.
	 */
	void forceSetResult(BuildStep.Result result, BuildStep.Status status);

	/**
	 * Only sets the given result if it's worse than the current result.
	 * @param result if the result.isWorseThan(currentResult) == true, then the current result will be overwritten.
	 * @param status if the status is HALT and the current status is not POST_BUILD, then the current status will be set to HALT.
	 */
	void setResult(BuildStep.Result result, BuildStep.Status status);
}
