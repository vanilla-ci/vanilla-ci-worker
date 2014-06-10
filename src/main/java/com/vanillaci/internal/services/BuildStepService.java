package com.vanillaci.internal.services;

import com.vanillaci.plugins.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author Joel Johnson
 */
public class BuildStepService {
	private static final Logger log = LogManager.getLogger();

	@NotNull private final Map<String, BuildStep> buildStepMap;

	public BuildStepService() {
		buildStepMap = new ConcurrentHashMap<String, BuildStep>();
	}

	@Nullable
	public BuildStep get(@NotNull String name, @NotNull String version) {
		String fullyQualifiedName = name + ":" + version;
		return buildStepMap.get(fullyQualifiedName);
	}

	public void register(@NotNull String name, @NotNull String version, @NotNull BuildStep buildStep) {
		String fullyQualifiedName = name + ":" + version;
		BuildStep oldPlugin = buildStepMap.put(fullyQualifiedName, buildStep);
		if(oldPlugin != null) {
			throw new IllegalStateException("Replacing " + oldPlugin.getClass() + " with " + fullyQualifiedName + " by using the 'register' method. Use the 'update' method instead.");
		}

		if(log.isInfoEnabled()) {
			log.info("Registered new plugin: " + fullyQualifiedName);
		}
	}

	public void unregister(@NotNull String name, @NotNull String version) {
		String fullyQualifiedName = name + ":" + version;
		if(buildStepMap.remove(fullyQualifiedName) != null) {
			if(log.isInfoEnabled()) {
				log.info("Unregistered plugin: " + fullyQualifiedName);
			}
		}
	}

	public void update(@NotNull String name, @NotNull String version, @NotNull BuildStep buildStep) {
		throw new UnsupportedOperationException("Need to implement the update method");
	}
}
