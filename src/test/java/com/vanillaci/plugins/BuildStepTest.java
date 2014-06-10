package com.vanillaci.plugins;

import org.junit.*;

import static com.vanillaci.plugins.BuildStep.Result;

/**
 * @author Joel Johnson
 */
public class BuildStepTest {
	@Test
	public void testResultBetterThan() throws Exception {
		assert Result.SUCCESS.isBetterThan(Result.FAILURE);
		assert Result.FAILURE.isBetterThan(Result.ERROR);
		assert Result.ERROR.isBetterThan(Result.ABORTED);
		assert Result.SUCCESS.isBetterThan(Result.ABORTED);

		assert !Result.FAILURE.isBetterThan(Result.SUCCESS);
		assert !Result.ERROR.isBetterThan(Result.FAILURE);
		assert !Result.ABORTED.isBetterThan(Result.ERROR);
		assert !Result.ABORTED.isBetterThan(Result.SUCCESS);

		assert !Result.SUCCESS.isBetterThan(Result.SUCCESS);
		assert !Result.FAILURE.isBetterThan(Result.FAILURE);
		assert !Result.ERROR.isBetterThan(Result.ERROR);
	}

	@Test
	public void testResultBetterThanOrEqualTo() throws Exception {
		assert Result.SUCCESS.isBetterThanOrEqualTo(Result.FAILURE);
		assert Result.FAILURE.isBetterThanOrEqualTo(Result.ERROR);
		assert Result.ERROR.isBetterThanOrEqualTo(Result.ABORTED);
		assert Result.SUCCESS.isBetterThanOrEqualTo(Result.ABORTED);

		assert !Result.FAILURE.isBetterThanOrEqualTo(Result.SUCCESS);
		assert !Result.ERROR.isBetterThanOrEqualTo(Result.FAILURE);
		assert !Result.ABORTED.isBetterThanOrEqualTo(Result.ERROR);
		assert !Result.ABORTED.isBetterThanOrEqualTo(Result.SUCCESS);

		assert Result.SUCCESS.isBetterThanOrEqualTo(Result.SUCCESS);
		assert Result.FAILURE.isBetterThanOrEqualTo(Result.FAILURE);
		assert Result.ERROR.isBetterThanOrEqualTo(Result.ERROR);
	}

	@Test
	public void testResultWorseThan() throws Exception {
		assert !Result.SUCCESS.isWorseThan(Result.FAILURE);
		assert !Result.FAILURE.isWorseThan(Result.ERROR);
		assert !Result.ERROR.isWorseThan(Result.ABORTED);
		assert !Result.SUCCESS.isWorseThan(Result.ABORTED);

		assert Result.FAILURE.isWorseThan(Result.SUCCESS);
		assert Result.ERROR.isWorseThan(Result.FAILURE);
		assert Result.ABORTED.isWorseThan(Result.ERROR);
		assert Result.ABORTED.isWorseThan(Result.SUCCESS);

		assert !Result.SUCCESS.isWorseThan(Result.SUCCESS);
		assert !Result.FAILURE.isWorseThan(Result.FAILURE);
		assert !Result.ERROR.isWorseThan(Result.ERROR);
	}

	@Test
	public void testResultWorseThanOrEqualTo() throws Exception {
		assert !Result.SUCCESS.isWorseThanOrEqualTo(Result.FAILURE);
		assert !Result.FAILURE.isWorseThanOrEqualTo(Result.ERROR);
		assert !Result.ERROR.isWorseThanOrEqualTo(Result.ABORTED);
		assert !Result.SUCCESS.isWorseThanOrEqualTo(Result.ABORTED);

		assert Result.FAILURE.isWorseThanOrEqualTo(Result.SUCCESS);
		assert Result.ERROR.isWorseThanOrEqualTo(Result.FAILURE);
		assert Result.ABORTED.isWorseThanOrEqualTo(Result.ERROR);
		assert Result.ABORTED.isWorseThanOrEqualTo(Result.SUCCESS);

		assert Result.SUCCESS.isWorseThanOrEqualTo(Result.SUCCESS);
		assert Result.FAILURE.isWorseThanOrEqualTo(Result.FAILURE);
		assert Result.ERROR.isWorseThanOrEqualTo(Result.ERROR);
	}
}
