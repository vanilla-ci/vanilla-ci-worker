package com.vanillaci.internal.services;

import com.google.common.collect.*;
import com.vanillaci.internal.model.*;
import com.vanillaci.plugins.*;
import org.junit.*;

import java.util.*;

import static com.vanillaci.plugins.BuildStep.Result;
import static org.mockito.Mockito.*;

/**
 * @author Joel Johnson
 */
public class WorkServiceTest {
	private static final String V1_0 = "1.0.0";

	private WorkService workService;
	private BuildStepService buildStepService;

	@Before
	public void setUp() throws Exception {
		buildStepService = mock(BuildStepService.class);
		when(buildStepService.get(Result.SUCCESS.name(), V1_0)).thenReturn(new SimpleBuildStep(Result.SUCCESS));
		when(buildStepService.get(Result.FAILURE_CONTINUE.name(), V1_0)).thenReturn(new SimpleBuildStep(Result.FAILURE_CONTINUE));
		when(buildStepService.get(Result.FAILURE_HALT.name(), V1_0)).thenReturn(new SimpleBuildStep(Result.FAILURE_HALT));
		when(buildStepService.get(Result.ERROR.name(), V1_0)).thenReturn(new SimpleBuildStep(Result.ERROR));
		when(buildStepService.get(Result.ABORTED.name(), V1_0)).thenReturn(new SimpleBuildStep(Result.ABORTED));

		workService = new WorkService(buildStepService);
	}

	@Test
	public void testExecuteWork_buildStep_haltsOnFailureHalt() throws Exception {
		when(buildStepService.get("Throw", V1_0)).thenReturn(new ErrorBuildStep("Should not continue after HALT")); // throw an error so it will stop the test if it's thrown (which it shouldn't in this test)

		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.FAILURE_HALT.name(), V1_0, Collections.emptyMap()),
			new BuildStepMessage("Throw", V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of();

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		workService.executeWork(work);
	}

	@Test
	public void testExecuteWork_postBuildStep_continuesOnFailureHalt() throws Exception {
		when(buildStepService.get("Throw", V1_0)).thenReturn(new ErrorBuildStep("Should continue HALT")); // throw an error so the exception bubbles all the way up and we can assert it was called

		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of();

		List<BuildStepMessage> postBuildSteps = ImmutableList.of(
			new BuildStepMessage(Result.FAILURE_HALT.name(), V1_0, Collections.emptyMap()),
			new BuildStepMessage("Throw", V1_0, Collections.emptyMap())
		);

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);

		try {
			workService.executeWork(work);
			assert false : "Should have thrown the error because all post build steps should always run";
		} catch (TestError ignore) {}
	}

	@Test
	public void testExecuteWork_worstWins_badThenGood() throws Exception {
		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.FAILURE_HALT.name(), V1_0, Collections.emptyMap()),
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of();

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		Result result = workService.executeWork(work);
		assert result == Result.FAILURE_HALT : "the worst Result should be what is returned.";
	}

	@Test
	public void testExecuteWork_worstWins_goodThenBad() throws Exception {
		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap()),
			new BuildStepMessage(Result.FAILURE_HALT.name(), V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of();

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		Result result = workService.executeWork(work);
		assert result == Result.FAILURE_HALT : "the worst Result should be what is returned.";
	}

	@Test
	public void testExecuteWork_worstWins_goodThenBad_includingPostBuild() throws Exception {
		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of(
			new BuildStepMessage(Result.FAILURE_HALT.name(), V1_0, Collections.emptyMap())
		);

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		Result result = workService.executeWork(work);
		assert result == Result.FAILURE_HALT : "the worst Result should be what is returned.";
	}

	@Test
	public void testExecuteWork_worstWins_badThenGood_includingPostBuild() throws Exception {
		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.FAILURE_HALT.name(), V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of(
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap())
		);

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		Result result = workService.executeWork(work);
		assert result == Result.FAILURE_HALT : "the worst Result should be what is returned.";
	}

}

class SimpleBuildStep implements BuildStep {
	private final Result result;

	public SimpleBuildStep(Result result) {
		this.result = result;
	}

	@Override
	public Result execute(BuildStepContext context) throws Exception {
		return result;
	}
}

class ErrorBuildStep implements BuildStep {
	private final String message;

	public ErrorBuildStep(String message) {
		this.message = message;
	}

	@Override
	public Result execute(BuildStepContext context) throws Exception {
		throw new TestError(message);
	}
}

/**
 * use Error since Exceptions are handled and only logged in WorkService
 */
class TestError extends Error {
	TestError(String message) {
		super(message);
	}
}