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
	private final BuildStepInterceptorService buildStepInterceptorService;

	public WorkService(BuildStepService buildStepService, BuildStepInterceptorService buildStepInterceptorService) {
		this.buildStepService = buildStepService;
		this.buildStepInterceptorService = buildStepInterceptorService;
	}

	public BuildStep.Result executeWork(Work work) throws IOException {
		File workspace = new File("workspace");
		if (!workspace.exists() && !workspace.mkdirs()) {
			throw new IOException("couldn't create workspace " + workspace.getAbsolutePath());
		}

		Collection<BuildStepInterceptor> buildStepInterceptors = buildStepInterceptorService.getAll();

		Map<String, String> pluginAddedParameters = new HashMap<>();
		BuildStep.Result currentResult = BuildStep.Result.SUCCESS;
		BuildStep.Status currentStatus = BuildStep.Status.CONTINUE;
		BuildStepContextImpl buildStepContext;

		for(List<BuildStepMessage> buildSteps : Arrays.asList(work.getBuildSteps(), work.getPostBuildSteps())) {
			int buildStepIndex = -1; // -1 so we can increment at the start. That way we don't have to worry about breaks/continues/catches in the loop
			final int totalBuildSteps = buildSteps.size();

			for (BuildStepMessage buildStepMessage : buildSteps) {
				buildStepIndex++;
				if(log.isInfoEnabled()) {
					log.info("Executing step " + buildStepMessage.getName() + " " + buildStepMessage.getVersion());
				}

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

				boolean runStep = runBuildStepInterceptorBefore(buildStepInterceptors, buildStepContext, buildStepIndex, totalBuildSteps, buildStep);
				if(!runStep) {
					continue;
				}

				long buildStepStartTime = System.currentTimeMillis();
				try {
					buildStep.execute(buildStepContext);

					long buildStepRunTime = System.currentTimeMillis() - buildStepStartTime;

					if(log.isInfoEnabled()) {
						log.info("----------------------------");
						log.info("Run time ("+buildStep.getClass().getName()+"): " + buildStepRunTime + "ms");
						log.info("----------------------------");
					}

					pluginAddedParameters = new HashMap<>(buildStepContext.getAddedParameters());
					currentResult = buildStepContext.getResult();
					currentStatus = buildStepContext.getStatus();

					if (currentStatus == BuildStep.Status.HALT) {
						runBuildStepInterceptorAfter(buildStepInterceptors, buildStepContext, buildStepIndex, totalBuildSteps, buildStep);
						break;
					}
				} catch (Exception e) {
					long buildStepRunTime = System.currentTimeMillis() - buildStepStartTime;

					log.info("Unexpected exception while running " + work.getId(), e);
					if(log.isInfoEnabled()) {
						log.info("----------------------------");
						log.info("Run time ("+buildStep.getClass().getName()+"):" + buildStepRunTime + "ms");
						log.info("----------------------------");
					}

					pluginAddedParameters = new HashMap<>(buildStepContext.getAddedParameters());
					currentResult = BuildStep.Result.ERROR;
				}
				runBuildStepInterceptorAfter(buildStepInterceptors, buildStepContext, buildStepIndex, totalBuildSteps, buildStep);
			}

			currentStatus = BuildStep.Status.POST_BUILD;
		}

		return currentResult;
	}

	private boolean runBuildStepInterceptorBefore(Collection<BuildStepInterceptor> buildStepInterceptors, BuildStepContextImpl buildStepContext, int buildStepIndex, int totalBuildSteps, BuildStep buildStep) {
		boolean result = true;
		for (BuildStepInterceptor buildStepInterceptor : buildStepInterceptors) {
			if(log.isInfoEnabled()) {
				log.info("Running BuildStepInterceptor before: " + buildStepInterceptor.getClass().getName());
			}

			long interceptorStartTime = System.currentTimeMillis();
			BuildStepInterceptor.InterceptorStatus status = buildStepInterceptor.before(buildStepContext, buildStep, buildStepIndex, totalBuildSteps);
			long interceptorRunTime = System.currentTimeMillis() - interceptorStartTime;

			if(log.isInfoEnabled()) {
				log.info("----------------------------");
				log.info("Run time ("+buildStepInterceptor.getClass()+".before): " + interceptorRunTime + "ms");
				log.info("----------------------------");
			}

			result = result && status != BuildStepInterceptor.InterceptorStatus.SKIP; // Null or RUN are treated the same
		}
		return result;
	}

	private void runBuildStepInterceptorAfter(Collection<BuildStepInterceptor> buildStepInterceptors, BuildStepContextImpl buildStepContext, int buildStepIndex, int totalBuildSteps, BuildStep buildStep) {
		for (BuildStepInterceptor buildStepInterceptor : buildStepInterceptors) {
			if(log.isInfoEnabled()) {
				log.info("Running BuildStepInterceptor after: " + buildStepInterceptor.getClass().getName());
			}

			long interceptorStartTime = System.currentTimeMillis();
			buildStepInterceptor.after(buildStepContext, buildStep, buildStepIndex, totalBuildSteps);
			long interceptorRunTime = System.currentTimeMillis() - interceptorStartTime;

			if(log.isInfoEnabled()) {
				log.info("----------------------------");
				log.info("Run time ("+buildStepInterceptor.getClass()+".after): " + interceptorRunTime + "ms");
				log.info("----------------------------");
			}

		}
	}
}
