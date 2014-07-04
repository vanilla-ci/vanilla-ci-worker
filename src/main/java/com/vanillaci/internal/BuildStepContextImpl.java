package com.vanillaci.internal;

import com.google.common.collect.*;
import com.vanillaci.plugins.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

/**
 * @author Joel Johnson
 */
public class BuildStepContextImpl implements BuildStepContext {
	private final Map<String, String> parameters;
	private final Map<String, String> addedParameters;

	private final File workspace;

	private BuildStep buildStep;
	private BuildStep.Result result;
	private BuildStep.Status status;
	private final int buildStepsIndex;
	private final int totalBuildSteps;


	/**
	 * @param pluginAddedParameters Any time a plugin adds parameters, those parameters will be available to all other jobs.
	 */
	public BuildStepContextImpl(Map<String, String> parameters, Map<String, String> pluginAddedParameters, BuildStep buildStep, BuildStep.Result result, BuildStep.Status status, File workspace, int buildStepsIndex, int totalBuildSteps) {
		this.parameters = ImmutableMap.copyOf(parameters);
		this.addedParameters = new HashMap<>(pluginAddedParameters);
		this.workspace = workspace;

		this.buildStep = buildStep;
		this.result = result;
		this.status = status;
		this.buildStepsIndex = buildStepsIndex;
		this.totalBuildSteps = totalBuildSteps;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
	public void addParameter(@NotNull String parameterName, @NotNull String parameterValue) {
		addedParameters.put(parameterName, parameterValue);
	}
	public Map<String, String> getAddedParameters() {
		return ImmutableMap.copyOf(addedParameters);
	}


	@Override
	public BuildStep getBuildStep() {
		return buildStep;
	}

	@Override
	public void setBuildStep(BuildStep buildStep) {
		this.buildStep = buildStep;
	}

	@Override
	public BuildStep.Result getResult() {
		return result;
	}

	@Override
	public BuildStep.Status getStatus() {
		return status;
	}

	@Override
	public void forceSetResult(BuildStep.Result result, BuildStep.Status status) {
		this.result = result;
		this.status = status;
	}

	@Override
	public void setResult(BuildStep.Result result, BuildStep.Status status) {
		if(result.isWorseThan(this.result)) {
			this.result = result;
		}

		if(status == BuildStep.Status.HALT && this.status != BuildStep.Status.POST_BUILD) {
			this.status = status;
		}
	}

	@Override
	public int getBuildStepIndex() {
		return buildStepsIndex;
	}

	@Override
	public int getTotalBuildSteps() {
		return totalBuildSteps;
	}

	public File getWorkspace() {
		return workspace;
	}
}
