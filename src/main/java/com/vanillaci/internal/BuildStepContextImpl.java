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

	/**
	 * @param pluginAddedParameters Any time a plugin adds parameters, those parameters will be available to all other jobs.
	 */
	public BuildStepContextImpl(Map<String, String> parameters, Map<String, String> pluginAddedParameters, File workspace, Sdk sdk) {
		this.parameters = ImmutableMap.copyOf(parameters);
		this.addedParameters = pluginAddedParameters;
		this.workspace = workspace;
		this.sdk = sdk;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
	public void addParameter(@NotNull String parameterName, @NotNull String parameterValue) {
		addedParameters.put(parameterName, parameterValue);
	}

	public File getWorkspace() {
		return workspace;
	}

	public Sdk getSdk() {
		return sdk;
	}
}
