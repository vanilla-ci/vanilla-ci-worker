package com.vanillaci.internal.services;

import com.google.common.collect.*;
import com.vanillaci.internal.model.*;
import com.vanillaci.internal.util.*;
import com.vanillaci.plugins.*;
import org.junit.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static com.vanillaci.plugins.BuildStep.Result;
import static com.vanillaci.plugins.BuildStep.Status;
import static org.mockito.Mockito.*;

/**
 * @author Joel Johnson
 */
public class WorkServiceTest {
	private static final String V1_0 = "1.0.0";

	private WorkService workService;
	private BuildStepService buildStepService;
	private BuildStepInterceptorService buildStepInterceptorService;

	@Before
	public void setUp() throws Exception {
		VanillaCiConfig config = mock(VanillaCiConfig.class);

		buildStepService = mock(BuildStepService.class);
		when(buildStepService.get(Result.SUCCESS.name(), V1_0)).thenReturn(new SimpleBuildStep(Result.SUCCESS, Status.CONTINUE));
		when(buildStepService.get(Result.FAILURE.name(), V1_0)).thenReturn(new SimpleBuildStep(Result.FAILURE, Status.HALT));
		when(buildStepService.get(Result.ERROR.name(), V1_0)).thenReturn(new SimpleBuildStep(Result.ERROR, Status.HALT));
		when(buildStepService.get(Result.ABORTED.name(), V1_0)).thenReturn(new SimpleBuildStep(Result.ABORTED, Status.HALT));

		buildStepInterceptorService = mock(BuildStepInterceptorService.class);
		when(buildStepInterceptorService.getAll()).thenReturn(Collections.emptyList());

		workService = new WorkService(config, buildStepService, buildStepInterceptorService);
	}

	@Test
	public void testExecuteWork_buildStep_haltsOnFailureHalt() throws Exception {
		when(buildStepService.get("Throw", V1_0)).thenReturn(new ErrorBuildStep("Should not continue after HALT")); // throw an error so it will stop the test if it's thrown (which it shouldn't in this test)

		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.FAILURE.name(), V1_0, Collections.emptyMap()),
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
			new BuildStepMessage(Result.FAILURE.name(), V1_0, Collections.emptyMap()),
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
			new BuildStepMessage(Result.FAILURE.name(), V1_0, Collections.emptyMap()),
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of();

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		Result result = workService.executeWork(work);
		assert result == Result.FAILURE : "the worst Result should be what is returned.";
	}

	@Test
	public void testExecuteWork_worstWins_goodThenBad() throws Exception {
		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap()),
			new BuildStepMessage(Result.FAILURE.name(), V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of();

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		Result result = workService.executeWork(work);
		assert result == Result.FAILURE : "the worst Result should be what is returned.";
	}

	@Test
	public void testExecuteWork_worstWins_goodThenBad_includingPostBuild() throws Exception {
		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of(
			new BuildStepMessage(Result.FAILURE.name(), V1_0, Collections.emptyMap())
		);

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		Result result = workService.executeWork(work);
		assert result == Result.FAILURE : "the worst Result should be what is returned.";
	}

	@Test
	public void testExecuteWork_worstWins_badThenGood_includingPostBuild() throws Exception {
		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.FAILURE.name(), V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of(
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap())
		);

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		Result result = workService.executeWork(work);
		assert result == Result.FAILURE : "the worst Result should be what is returned.";
	}

	@Test
	public void testExecuteInterceptor_skip() throws Exception {
		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of();

		when(buildStepInterceptorService.getAll()).thenReturn(Arrays.asList(
			new BuildStepInterceptor() {
				@Override
				public void before(BuildStepContext context) {
					context.setBuildStep(context1 -> { throw new TestError("Failure from inside the before"); });
				}

				@Override
				public void after(BuildStepContext context) {

				}
			}
		));

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		try {
			workService.executeWork(work);
			assert false : "exception should have been thrown by the overwritten BuildStep";
		} catch (TestError e) {
			if(!"Failure from inside the before".equals(e.getMessage())) {
				throw e;
			}
		}
	}

	@Test
	public void testExecuteInterceptor_afterRunsForEach() throws Exception {
		//using atomic integers so I can pass by reference
		AtomicInteger beforeCount = new AtomicInteger();
		AtomicInteger afterCount = new AtomicInteger();

		when(buildStepInterceptorService.getAll()).thenReturn(Arrays.asList(
			new BuildStepInterceptor() {
				@Override
				public void before(BuildStepContext context) {
					beforeCount.incrementAndGet();
				}

				@Override
				public void after(BuildStepContext context) {
					afterCount.incrementAndGet();
				}
			}
		));

		Map<String, String> parameters = Collections.emptyMap();
		List<BuildStepMessage> buildSteps = ImmutableList.of(
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap()),
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap()),
			new BuildStepMessage(Result.SUCCESS.name(), V1_0, Collections.emptyMap())
		);

		List<BuildStepMessage> postBuildSteps = ImmutableList.of();

		Work work = new Work("work 1", parameters, buildSteps, postBuildSteps);
		workService.executeWork(work);

		assert beforeCount.get() == 3 : "before should be called 3 times. Once for each build step.";
		assert afterCount.get() == 3 : "after should be called 3 times. Once for each build step.";
	}
}

class SimpleBuildStep implements BuildStep {
	private final Result result;
	private final Status status;

	public SimpleBuildStep(Result result, Status status) {
		this.result = result;
		this.status = status;
	}

	@Override
	public void execute(BuildStepContext context) throws Exception {
		context.setResult(result, status);
	}
}

class ErrorBuildStep implements BuildStep {
	private final String message;

	public ErrorBuildStep(String message) {
		this.message = message;
	}

	@Override
	public void execute(BuildStepContext context) throws Exception {
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
