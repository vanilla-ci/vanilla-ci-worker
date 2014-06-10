package com.vanillaci.internal.model;

import com.google.common.collect.*;
import org.codehaus.jackson.annotate.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * @author Joel Johnson
 */
public class BuildStepMessage {
	@NotNull private final String name;
	@NotNull private final String version;
	@NotNull private final Map<String, String> parameters;

	public BuildStepMessage(
		@JsonProperty("name") @NotNull String name,
		@JsonProperty("version") @NotNull String version,
		@JsonProperty("parameters") @NotNull Map<String, String> parameters
	) {
		this.name = name;
		this.version = version;
		this.parameters = ImmutableMap.copyOf(parameters);
	}

	@NotNull
	public String getName() {
		return name;
	}

	@NotNull
	public String getVersion() {
		return version;
	}

	@NotNull
	public Map<String, String> getParameters() {
		return parameters;
	}
}