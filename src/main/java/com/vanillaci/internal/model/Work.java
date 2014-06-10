package com.vanillaci.internal.model;

import com.google.common.collect.*;
import org.codehaus.jackson.annotate.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * @author Joel Johnson
 */
public class Work {
	@NotNull private final String id;
	@NotNull private final Map<String, String> parameters;
	@NotNull private final List<BuildStepMessage> buildSteps;
	@NotNull private final List<BuildStepMessage> postBuildSteps;

	public Work(
		@JsonProperty("id") @NotNull String id,
		@JsonProperty("parameters") @Nullable Map<String, String> parameters,
		@JsonProperty("scripts") @Nullable List<BuildStepMessage> buildSteps,
		@JsonProperty("postScripts") @Nullable List<BuildStepMessage> postBuildSteps) {
		this.id = id;

		if(parameters == null) parameters = ImmutableMap.of();
		this.parameters = ImmutableMap.copyOf(parameters);

		if(buildSteps == null) buildSteps = ImmutableList.of();
		this.buildSteps = ImmutableList.copyOf(buildSteps);

		if(postBuildSteps == null) postBuildSteps = ImmutableList.of();
		this.postBuildSteps = ImmutableList.copyOf(postBuildSteps);
	}

	@NotNull
	public String getId() {
		return id;
	}

	@NotNull
	public Map<String, String> getParameters() {
		return parameters;
	}

	@NotNull
	public List<BuildStepMessage> getBuildSteps() {
		return buildSteps;
	}

	@NotNull
	public List<BuildStepMessage> getPostBuildSteps() {
		return postBuildSteps;
	}
}
