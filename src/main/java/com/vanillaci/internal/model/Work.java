package com.vanillaci.internal.model;

import com.google.common.collect.*;
import com.vanillaci.plugins.*;
import org.codehaus.jackson.annotate.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * @author Joel Johnson
 */
public class Work {
	@NotNull private final String id;
	@NotNull private final Map<String, String> parameters;
	@NotNull private final List<BuildStepMessage> scripts;
	@NotNull private final List<BuildStepMessage> postScripts;

	public Work(
		@JsonProperty("id") @NotNull String id,
		@JsonProperty("parameters") @Nullable Map<String, String> parameters,
		@JsonProperty("scripts") @Nullable List<BuildStepMessage> scripts,
		@JsonProperty("postScripts") @Nullable List<BuildStepMessage> postScripts) {
		this.id = id;

		if(parameters == null) parameters = ImmutableMap.of();
		this.parameters = ImmutableMap.copyOf(parameters);

		if(scripts == null) scripts = ImmutableList.of();
		this.scripts = ImmutableList.copyOf(scripts);

		if(postScripts == null) postScripts = ImmutableList.of();
		this.postScripts = ImmutableList.copyOf(postScripts);
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
	public List<BuildStepMessage> getScripts() {
		return scripts;
	}

	@NotNull
	public List<BuildStepMessage> getPostScripts() {
		return postScripts;
	}
}
