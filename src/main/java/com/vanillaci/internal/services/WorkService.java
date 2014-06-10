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
		if (!workspace.exists() && !workspace.mkdirs()) {
			throw new IOException("couldn't create workspace " + workspace.getAbsolutePath());
		}

		Map<String, String> pluginAddedParameters = new HashMap<>();
		BuildStep.Result currentResult = BuildStep.Result.SUCCESS;
		BuildStep.Status currentStatus = BuildStep.Status.CONTINUE;
		BuildStepContextImpl buildStepContext;

		for(List<BuildStepMessage> buildSteps : Arrays.asList(work.getBuildSteps(), work.getPostBuildSteps())) {
			for (BuildStepMessage buildStepMessage : buildSteps) {
				BuildStep buildStep = buildStepService.get(buildStepMessage.getName(), buildStepMessage.getVersion());
				if (buildStep == null) {
					throw new UnresolvedBuildStepException(buildStepMessage);
				}

				Map<String, String> buildStepParameters = buildStepMessage.getParameters();
				Map<String, String> workParametersParameters = work.getParameters();
				Map<String, String> allParameters = ImmutableMap.<String, String>builder()
					.putAll(buildStepParameters)
					.putAll(workParametersParameters)
					.putAll(pluginAddedParameters)
					.build();

				buildStepContext = new BuildStepContextImpl(allParameters, pluginAddedParameters, currentResult, currentStatus, workspace, new SdkImpl());

				try {
					buildStep.execute(buildStepContext);

					pluginAddedParameters = new HashMap<>(buildStepContext.getAddedParameters());
					currentResult = buildStepContext.getResult();
					currentStatus = buildStepContext.getStatus();

					if (currentStatus == BuildStep.Status.HALT) {
						break;
					}
				} catch (Exception e) {
					log.info("Unexpected exception while running " + work.getId(), e);

					pluginAddedParameters = new HashMap<>(buildStepContext.getAddedParameters());
					currentResult = BuildStep.Result.ERROR;
					break;
				}
			}

			currentStatus = BuildStep.Status.POST_BUILD;
		}

		return currentResult;
	}
}
