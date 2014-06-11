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
		BuildStepService buildStepService = new BuildStepService();
		BuildStepInterceptorService buildStepInterceptorService = new BuildStepInterceptorService();

		//WorkService workService = new WorkService(buildStepService, buildStepInterceptorService);
		//TODO: initialize Messaging and start listening to queues

		PluginServiceListener listener = new PluginServiceListener(buildStepService, buildStepInterceptorService);
		context.addServiceListener(listener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}
}
