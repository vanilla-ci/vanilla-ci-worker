package com.vanillaci.internal.osgi;

import com.vanillaci.internal.annotations.*;
import com.vanillaci.internal.services.*;
import org.osgi.framework.*;

/**
 * Entry point of the application.
 * Called when the OSGi container starts the module.
 *
 * @author Joel Johnson
 */
@ReflectivelyUsed
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext context) throws Exception {
		//TODO: initialize Messaging and start listening to queues

		BuildStepService buildStepService = new BuildStepService();
		WorkService workService = new WorkService(buildStepService);

		PluginServiceListener listener = new PluginServiceListener(buildStepService);
		context.addServiceListener(listener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}
}
