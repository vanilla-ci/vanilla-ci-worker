package com.vanillaci.internal.osgi;

import com.vanillaci.internal.annotations.*;
import com.vanillaci.internal.services.*;
import com.vanillaci.internal.util.*;
import org.apache.logging.log4j.*;
import org.osgi.framework.*;

/**
 * Entry point of the application.
 * Called when the OSGi container starts the module.
 *
 * @author Joel Johnson
 */
@ReflectivelyUsed
public class Activator implements BundleActivator {
	private static final Logger log = LogManager.getLogger();

	@Override
	public void start(BundleContext context) throws Exception {
		VanillaCiConfig config = VanillaCiConfig.createDefault();

		BuildStepService buildStepService = new BuildStepService();
		BuildStepInterceptorService buildStepInterceptorService = new BuildStepInterceptorService();

		WorkService workService = new WorkService(config, buildStepService, buildStepInterceptorService);
		log.info("initialized workService " + workService);

		//TODO: initialize Messaging and start listening to queues/topics

		PluginServiceListener listener = new PluginServiceListener(buildStepService, buildStepInterceptorService);
		context.addServiceListener(listener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}
}
