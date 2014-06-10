package com.vanillaci.internal;

import com.vanillaci.plugins.*;

import java.io.*;
import java.util.*;

/**
 * @author Joel Johnson
 */
public class BuildStepContextImpl implements BuildStepContext {
	private final Map<String, String> parameters;
	private final File workspace;
	private final Sdk sdk;

	public BuildStepContextImpl(Map<String, String> parameters, File workspace, Sdk sdk) {
		this.parameters = parameters;
		this.workspace = workspace;
		this.sdk = sdk;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public File getWorkspace() {
		return workspace;
	}

	public Sdk getSdk() {
		return sdk;
	}
}
