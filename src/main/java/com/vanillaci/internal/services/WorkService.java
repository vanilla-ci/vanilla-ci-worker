package com.vanillaci.internal.services;

import com.google.common.collect.*;
import com.vanillaci.internal.*;
import com.vanillaci.internal.exceptions.*;
import com.vanillaci.internal.model.*;
import com.vanillaci.internal.util.*;
import com.vanillaci.plugins.*;
import org.apache.logging.log4j.*;

import java.io.*;
import java.util.*;

/**
 * @author Joel Johnson
 */
public class WorkService {
	private static final Logger log = LogManager.getLogger();

	private final StopWatch stopWatch = new StopWatch(log);

	private final VanillaCiConfig config;
	private final BuildStepService buildStepService;
	private final BuildStepInterceptorService buildStepInterceptorService;

	public WorkService(VanillaCiConfig config, BuildStepService buildStepService, BuildStepInterceptorService buildStepInterceptorService) {
		this.config = config;
		this.buildStepService = buildStepService;
		this.buildStepInterceptorService = buildStepInterceptorService;
	}

	public BuildStep.Result executeWork(Work work) throws IOException {
		File workspacesDirectory = config.getWorkspacesDirectory();
		String workspaceName = StringUtil.sanitizeFilename(work.getId());

		// TODO: allow work to define the name of the workspace

		File workspace = new File(workspacesDirectory, workspaceName);
		if (!workspace.exists() && !workspace.mkdirs()) {
			throw new IOException("couldn't create workspace " + workspace.getAbsolutePath());
		}

		Collection<BuildStepInterceptor> buildStepInterceptors = buildStepInterceptorService.getAll();

		Map<String, String> pluginAddedParameters = new HashMap<>();
		BuildStep.Result currentResult = BuildStep.Result.SUCCESS;
		BuildStep.Status currentStatus = BuildStep.Status.CONTINUE;

		for(List<BuildStepMessage> buildSteps : Arrays.asList(work.getBuildSteps(), work.getPostBuildSteps())) {
			int buildStepIndex = -1; // -1 so we can increment at the start. That way we don't have to worry about breaks/continues/catches in the loop
			final int totalBuildSteps = buildSteps.size();

			for (BuildStepMessage buildStepMessage : buildSteps) {
				buildStepIndex++;
				if(log.isInfoEnabled()) {
					log.info("Executing step " + buildStepMessage.getName() + " " + buildStepMessage.getVersion());
				}

				BuildStep definedBuildStep = buildStepService.get(buildStepMessage.getName(), buildStepMessage.getVersion());
				if (definedBuildStep == null) {
					throw new UnresolvedBuildStepException(buildStepMessage);
				}

				Map<String, String> buildStepParameters = buildStepMessage.getParameters();
				Map<String, String> workParametersParameters = work.getParameters();
				Map<String, String> allParameters = ImmutableMap.<String, String>builder()
					.putAll(buildStepParameters)
					.putAll(workParametersParameters)
					.putAll(pluginAddedParameters)
					.build();

				BuildStepContextImpl buildStepContext = new BuildStepContextImpl(allParameters, pluginAddedParameters, definedBuildStep, currentResult, currentStatus, workspace, buildStepIndex, totalBuildSteps);

				runBuildStepInterceptorBefore(buildStepInterceptors, buildStepContext);

				BuildStep buildStepToExecute = buildStepContext.getBuildStep();
				if(buildStepToExecute != definedBuildStep && log.isInfoEnabled()) {
					log.info("Overwritten build step: " + definedBuildStep.getClass().getName() + " was overwritten by " + buildStepToExecute.getClass().getName());
				}
				try {
					stopWatch.time(buildStepToExecute.getClass().getName(),
						() -> buildStepToExecute.execute(buildStepContext)
					);

					pluginAddedParameters = new HashMap<>(buildStepContext.getAddedParameters());
					currentResult = buildStepContext.getResult();
					currentStatus = buildStepContext.getStatus();

					if (currentStatus == BuildStep.Status.HALT) {
						runBuildStepInterceptorAfter(buildStepInterceptors, buildStepContext);
						break;
					}
				} catch (Exception e) {
					log.info("Unexpected exception while running " + work.getId(), e);

					pluginAddedParameters = new HashMap<>(buildStepContext.getAddedParameters());
					currentResult = BuildStep.Result.ERROR;
				}
				runBuildStepInterceptorAfter(buildStepInterceptors, buildStepContext);
			}

			currentStatus = BuildStep.Status.POST_BUILD;
		}

		return currentResult;
	}

	private void runBuildStepInterceptorBefore(Collection<BuildStepInterceptor> buildStepInterceptors, BuildStepContextImpl buildStepContext) {
		for (BuildStepInterceptor buildStepInterceptor : buildStepInterceptors) {
			if(log.isInfoEnabled()) {
				log.info("Running BuildStepInterceptor before: " + buildStepInterceptor.getClass().getName());
			}

			stopWatch.time(buildStepInterceptor.getClass().getName(),
				() -> buildStepInterceptor.before(buildStepContext)
			);
		}
	}

	private void runBuildStepInterceptorAfter(Collection<BuildStepInterceptor> buildStepInterceptors, BuildStepContextImpl buildStepContext) {
		for (BuildStepInterceptor buildStepInterceptor : buildStepInterceptors) {
			if(log.isInfoEnabled()) {
				log.info("Running BuildStepInterceptor after: " + buildStepInterceptor.getClass().getName());
			}

			stopWatch.time(buildStepInterceptor.getClass().getName(),
				() -> buildStepInterceptor.after(buildStepContext)
			);
		}
	}
}
