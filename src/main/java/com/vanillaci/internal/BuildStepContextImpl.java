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
	private final Sdk sdk;

	private BuildStep.Result result;
	private BuildStep.Status status;

	/**
	 * @param pluginAddedParameters Any time a plugin adds parameters, those parameters will be available to all other jobs.
	 */
	public BuildStepContextImpl(Map<String, String> parameters, Map<String, String> pluginAddedParameters, BuildStep.Result result, BuildStep.Status status, File workspace, Sdk sdk) {
		this.parameters = ImmutableMap.copyOf(parameters);
		this.addedParameters = new HashMap<>(pluginAddedParameters);
		this.workspace = workspace;
		this.sdk = sdk;

		this.result = result;
		this.status = status;
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

	public File getWorkspace() {
		return workspace;
	}

	public Sdk getSdk() {
		return sdk;
	}
}
