package com.vanillaci.internal.services;

import com.google.common.collect.*;
import com.vanillaci.plugins.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author Joel Johnson
 */
public class BuildStepInterceptorService {
	private static final Logger log = LogManager.getLogger();

	@NotNull
	private final Map<String, BuildStepInterceptor> buildStepInterceptorMap;

	public BuildStepInterceptorService() {
		buildStepInterceptorMap = new ConcurrentHashMap<>();
	}

	@NotNull
	public Collection<BuildStepInterceptor> getAll() {
		return ImmutableList.copyOf(buildStepInterceptorMap.values());
	}

	public void register(@NotNull String name, @NotNull String version, @NotNull BuildStepInterceptor buildStep) {
		String fullyQualifiedName = name + ":" + version;
		BuildStepInterceptor oldPlugin = buildStepInterceptorMap.put(fullyQualifiedName, buildStep);
		if(oldPlugin != null) {
			throw new IllegalStateException("Replacing " + oldPlugin.getClass() + " with " + fullyQualifiedName + " by using the 'register' method. Use the 'update' method instead.");
		}

		if(log.isInfoEnabled()) {
			log.info("Registered new plugin: " + fullyQualifiedName);
		}
	}

	public void unregister(@NotNull String name, @NotNull String version) {
		String fullyQualifiedName = name + ":" + version;
		if(buildStepInterceptorMap.remove(fullyQualifiedName) != null) {
			if(log.isInfoEnabled()) {
				log.info("Unregistered plugin: " + fullyQualifiedName);
			}
		}
	}

	public void update(@NotNull String name, @NotNull String version, @NotNull BuildStepInterceptor buildStep) {
		throw new UnsupportedOperationException("Need to implement the update method");
	}
}
