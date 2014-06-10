package com.vanillaci.internal.services;

import com.vanillaci.internal.model.*;
import com.vanillaci.plugins.*;
import org.apache.logging.log4j.*;

/**
 * @author Joel Johnson
 */
public class WorkService {
	private static final Logger log = LogManager.getLogger();

	private final BuildStepService buildStepService;

	public WorkService(BuildStepService buildStepService) {
		this.buildStepService = buildStepService;
	}

	public BuildStep.Result executeWork(Work work) {
		BuildStepContext buildStepContext = null;

		BuildStep.Result finalResult = executeBuildSteps(work, buildStepContext);
		BuildStep.Result postBuildFinalResult = executePostBuildSteps(work, buildStepContext, finalResult);

		if(postBuildFinalResult.isWorseThan(finalResult)) {
			finalResult = postBuildFinalResult;
		}

		return finalResult;
	}

	private BuildStep.Result executeBuildSteps(Work work, BuildStepContext buildStepContext) {
		BuildStep.Result finalResult = BuildStep.Result.SUCCESS;
		try {
			for (BuildStep buildStep : work.getScripts()) {
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

	private BuildStep.Result executePostBuildSteps(Work work, BuildStepContext buildStepContext, BuildStep.Result finalResult) {
		for (BuildStep buildStep : work.getPostScripts()) {
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
