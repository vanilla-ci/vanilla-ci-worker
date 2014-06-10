package com.vanillaci.plugins;

import java.io.*;
import java.util.*;

/**
 * Provides context for BuildSteps.
 * Allows plugin developers to easily access things like the working directory of the job, the SDK, and parameters.
 *
 * @author Joel Johnson
 */
public interface BuildStepContext {
	Map<String, ? extends Collection<String>> getParameters();
	File getWorkspace();
	Sdk getSdk();
}
