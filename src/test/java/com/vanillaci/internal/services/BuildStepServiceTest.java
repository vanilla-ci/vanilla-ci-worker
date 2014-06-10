package com.vanillaci.internal.services;

import com.vanillaci.plugins.*;
import org.junit.*;

/**
 * @author Joel Johnson
 */
public class BuildStepServiceTest {
	private BuildStepService buildStepService;

	@Before
	public void setUp() {
		buildStepService = new BuildStepService();
	}

	@Test
	public void testRegister() throws Exception {
		BuildStepStub buildStep = new BuildStepStub();
		buildStepService.register("bacon", "1.0.0", buildStep);
		assert buildStepService.get("bacon", "1.0.0") == buildStep;
		assert buildStepService.get("bacon", "1.0.1") == null;
	}

	@Test
	public void testUnregister() {
		BuildStepStub buildStep = new BuildStepStub();
		buildStepService.register("bacon", "1.0.0", buildStep);
		buildStepService.unregister("bacon", "1.0.0");

		assert buildStepService.get("bacon", "1.0.0") == null;
	}
}

class BuildStepStub implements BuildStep {
	@Override
	public void execute(BuildStepContext context) throws Exception {
		context.setResult(Result.SUCCESS, Status.CONTINUE);
	}
}
