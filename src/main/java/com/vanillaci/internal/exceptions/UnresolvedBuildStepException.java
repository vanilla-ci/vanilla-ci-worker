package com.vanillaci.internal.exceptions;

import com.vanillaci.internal.model.*;

/**
 * Thrown when the Worker attempts to execute a plugin it doesn't have in the repository.
 *
 * @author Joel Johnson
 */
public class UnresolvedBuildStepException extends RuntimeException {
	public UnresolvedBuildStepException(BuildStepMessage buildStepMessage) {
		super("BuildStep was supposed to run but could not be found: " + buildStepMessage.getName() + " " + buildStepMessage.getVersion());
	}
}
