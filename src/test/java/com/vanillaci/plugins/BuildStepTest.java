package com.vanillaci.plugins;

import org.junit.*;

import static com.vanillaci.plugins.BuildStep.Result;

/**
 * @author Joel Johnson
 */
public class BuildStepTest {
	@Test
	public void testResultBetterThan() throws Exception {
		assert Result.SUCCESS.isBetterThan(Result.FAILURE_CONTINUE);
		assert Result.FAILURE_CONTINUE.isBetterThan(Result.FAILURE_HALT);
		assert Result.FAILURE_HALT.isBetterThan(Result.ERROR);
		assert Result.ERROR.isBetterThan(Result.ABORTED);
		assert Result.SUCCESS.isBetterThan(Result.ABORTED);

		assert !Result.FAILURE_CONTINUE.isBetterThan(Result.SUCCESS);
		assert !Result.FAILURE_HALT.isBetterThan(Result.FAILURE_CONTINUE);
		assert !Result.ERROR.isBetterThan(Result.FAILURE_HALT);
		assert !Result.ABORTED.isBetterThan(Result.ERROR);
		assert !Result.ABORTED.isBetterThan(Result.SUCCESS);

		assert !Result.SUCCESS.isBetterThan(Result.SUCCESS);
		assert !Result.FAILURE_CONTINUE.isBetterThan(Result.FAILURE_CONTINUE);
		assert !Result.FAILURE_HALT.isBetterThan(Result.FAILURE_HALT);
		assert !Result.ERROR.isBetterThan(Result.ERROR);
	}

	@Test
	public void testResultBetterThanOrEqualTo() throws Exception {
		assert Result.SUCCESS.isBetterThanOrEqualTo(Result.FAILURE_CONTINUE);
		assert Result.FAILURE_CONTINUE.isBetterThanOrEqualTo(Result.FAILURE_HALT);
		assert Result.FAILURE_HALT.isBetterThanOrEqualTo(Result.ERROR);
		assert Result.ERROR.isBetterThanOrEqualTo(Result.ABORTED);
		assert Result.SUCCESS.isBetterThanOrEqualTo(Result.ABORTED);

		assert !Result.FAILURE_CONTINUE.isBetterThanOrEqualTo(Result.SUCCESS);
		assert !Result.FAILURE_HALT.isBetterThanOrEqualTo(Result.FAILURE_CONTINUE);
		assert !Result.ERROR.isBetterThanOrEqualTo(Result.FAILURE_HALT);
		assert !Result.ABORTED.isBetterThanOrEqualTo(Result.ERROR);
		assert !Result.ABORTED.isBetterThanOrEqualTo(Result.SUCCESS);

		assert Result.SUCCESS.isBetterThanOrEqualTo(Result.SUCCESS);
		assert Result.FAILURE_CONTINUE.isBetterThanOrEqualTo(Result.FAILURE_CONTINUE);
		assert Result.FAILURE_HALT.isBetterThanOrEqualTo(Result.FAILURE_HALT);
		assert Result.ERROR.isBetterThanOrEqualTo(Result.ERROR);
	}

	@Test
	public void testResultWorseThan() throws Exception {
		assert !Result.SUCCESS.isWorseThan(Result.FAILURE_CONTINUE);
		assert !Result.FAILURE_CONTINUE.isWorseThan(Result.FAILURE_HALT);
		assert !Result.FAILURE_HALT.isWorseThan(Result.ERROR);
		assert !Result.ERROR.isWorseThan(Result.ABORTED);
		assert !Result.SUCCESS.isWorseThan(Result.ABORTED);

		assert Result.FAILURE_CONTINUE.isWorseThan(Result.SUCCESS);
		assert Result.FAILURE_HALT.isWorseThan(Result.FAILURE_CONTINUE);
		assert Result.ERROR.isWorseThan(Result.FAILURE_HALT);
		assert Result.ABORTED.isWorseThan(Result.ERROR);
		assert Result.ABORTED.isWorseThan(Result.SUCCESS);

		assert !Result.SUCCESS.isWorseThan(Result.SUCCESS);
		assert !Result.FAILURE_CONTINUE.isWorseThan(Result.FAILURE_CONTINUE);
		assert !Result.FAILURE_HALT.isWorseThan(Result.FAILURE_HALT);
		assert !Result.ERROR.isWorseThan(Result.ERROR);
	}

	@Test
	public void testResultWorseThanOrEqualTo() throws Exception {
		assert !Result.SUCCESS.isWorseThanOrEqualTo(Result.FAILURE_CONTINUE);
		assert !Result.FAILURE_CONTINUE.isWorseThanOrEqualTo(Result.FAILURE_HALT);
		assert !Result.FAILURE_HALT.isWorseThanOrEqualTo(Result.ERROR);
		assert !Result.ERROR.isWorseThanOrEqualTo(Result.ABORTED);
		assert !Result.SUCCESS.isWorseThanOrEqualTo(Result.ABORTED);

		assert Result.FAILURE_CONTINUE.isWorseThanOrEqualTo(Result.SUCCESS);
		assert Result.FAILURE_HALT.isWorseThanOrEqualTo(Result.FAILURE_CONTINUE);
		assert Result.ERROR.isWorseThanOrEqualTo(Result.FAILURE_HALT);
		assert Result.ABORTED.isWorseThanOrEqualTo(Result.ERROR);
		assert Result.ABORTED.isWorseThanOrEqualTo(Result.SUCCESS);

		assert Result.SUCCESS.isWorseThanOrEqualTo(Result.SUCCESS);
		assert Result.FAILURE_CONTINUE.isWorseThanOrEqualTo(Result.FAILURE_CONTINUE);
		assert Result.FAILURE_HALT.isWorseThanOrEqualTo(Result.FAILURE_HALT);
		assert Result.ERROR.isWorseThanOrEqualTo(Result.ERROR);
	}
}
