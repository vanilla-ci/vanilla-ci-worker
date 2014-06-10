package com.vanillaci.internal.services;

import com.google.common.collect.*;
import com.vanillaci.internal.*;
import com.vanillaci.internal.exceptions.*;
import com.vanillaci.internal.model.*;
import com.vanillaci.plugins.*;
import org.apache.logging.log4j.*;

import java.io.*;
import java.util.*;

/**
 * @author Joel Johnson
 */
public class WorkService {
	private static final Logger log = LogManager.getLogger();

	private final BuildStepService buildStepService;

	public WorkService(BuildStepService buildStepService) {
		this.buildStepService = buildStepService;
	}

	public BuildStep.Result executeWork(Work work) throws IOException {
		File workspace = new File("workspace");
		if(!workspace.exists() && workspace.mkdirs()) {
			throw new IOException("couldn't create workspace " + workspace.getAbsolutePath());
		}

		// This will be passed into the BuildStepContext allowing plugins to add variables to it.
		Map<String, String> pluginAddedParameters = new HashMap<String, String>();

		BuildStep.Result finalResult = executeBuildSteps(work, workspace, pluginAddedParameters);
		BuildStep.Result postBuildFinalResult = executePostBuildSteps(work, workspace, finalResult, pluginAddedParameters);

		if(postBuildFinalResult.isWorseThan(finalResult)) {
			finalResult = postBuildFinalResult;
		}

		return finalResult;
	}

	private BuildStep.Result executeBuildSteps(Work work, File workspace, Map<String, String> pluginAddedParameters) {
		BuildStep.Result finalResult = BuildStep.Result.SUCCESS;
		try {
			for (BuildStepMessage buildStepMessage : work.getBuildSteps()) {
				BuildStep buildStep = buildStepService.get(buildStepMessage.getName(), buildStepMessage.getVersion());
				if(buildStep == null) {
					throw new UnresolvedBuildStepException(buildStepMessage);
				}

				Map<String, String> buildStepParameters = buildStepMessage.getParameters();
				Map<String, String> workParametersParameters = work.getParameters();
				Map<String, String> allParameters = ImmutableMap.<String, String>builder()
					.putAll(buildStepParameters)
					.putAll(workParametersParameters)
					.putAll(pluginAddedParameters)
					.build();

				BuildStepContext buildStepContext = new BuildStepContextImpl(allParameters, pluginAddedParameters, workspace, new SdkImpl());

				BuildStep.Result result = buildStep.execute(buildStepContext);

				if(result.isWorseThan(finalResult)) {
					finalResult = result;

					if(finalResult.isWorseThanOrEqualTo(BuildStep.Result.FAILURE_HALT)) {
						break;
					}
				}
			}
		} catch (Exception e) {
			if(finalResult.isBetterThan(BuildStep.Result.ERROR)) {
				finalResult = BuildStep.Result.ERROR;
			}

			log.info("Unexpected exception while running " + work.getId(), e);
		}
		return finalResult;
	}

	private BuildStep.Result executePostBuildSteps(Work work, File workspace, BuildStep.Result finalResult, Map<String, String> pluginAddedParameters) {
		for (BuildStepMessage buildStepMessage : work.getPostBuildSteps()) {
			BuildStep buildStep = buildStepService.get(buildStepMessage.getName(), buildStepMessage.getVersion());
			if(buildStep == null) {
				throw new UnresolvedBuildStepException(buildStepMessage);
			}

			Map<String, String> buildStepParameters = buildStepMessage.getParameters();
			Map<String, String> workParametersParameters = work.getParameters();
			Map<String, String> allParameters = ImmutableMap.<String, String>builder()
				.putAll(buildStepParameters)
				.putAll(workParametersParameters)
				.putAll(pluginAddedParameters)
				.build();

			BuildStepContext buildStepContext = new BuildStepContextImpl(allParameters, pluginAddedParameters, workspace, new SdkImpl());

			try {
				BuildStep.Result result = buildStep.execute(buildStepContext);

				if(result.isWorseThan(finalResult)) {
					finalResult = result;
				}
			} catch (Exception e) {
				if(finalResult.isBetterThan(BuildStep.Result.ERROR)) {
					finalResult = BuildStep.Result.ERROR;
				}
			}
		}

		return finalResult;
	}
}
