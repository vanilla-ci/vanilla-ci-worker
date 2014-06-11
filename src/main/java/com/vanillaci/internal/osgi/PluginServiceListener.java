package com.vanillaci.internal.osgi;

import com.vanillaci.internal.services.*;
import com.vanillaci.plugins.*;
import org.jetbrains.annotations.*;
import org.osgi.framework.*;

/**
 * Listens for installations of new Plugins and registers them with the BuildStepService.
 * @author Joel Johnson
 */
public class PluginServiceListener implements ServiceListener {
	@NotNull private final BuildStepService buildStepService;
	@NotNull private final BuildStepInterceptorService buildStepInterceptorService;

	public PluginServiceListener(@NotNull BuildStepService buildStepService, @NotNull BuildStepInterceptorService buildStepInterceptorService) {
		this.buildStepService = buildStepService;
		this.buildStepInterceptorService = buildStepInterceptorService;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		ServiceReference<?> serviceReference = event.getServiceReference();

		Bundle bundle = serviceReference.getBundle(); // TODO: handle the Unregister and Modified states! They will actually throw a NPE right now
		BundleContext context = bundle.getBundleContext();
		Object service = context.getService(serviceReference);

		if(service instanceof BuildStep) {
			BuildStep buildStep = (BuildStep) service;
			String name = buildStep.getClass().getName();
			String version = getVersion(bundle);

			switch (event.getType()) {
				case ServiceEvent.REGISTERED:
					buildStepService.register(name, version, buildStep);
					break;
				case ServiceEvent.UNREGISTERING:
					buildStepService.unregister(name, version);
					break;
				case ServiceEvent.MODIFIED:
					buildStepService.update(name, version, buildStep);
					break;
			}
		}

		//no else-if in case someone implements more than one interface in one class
		if(service instanceof BuildStepInterceptor) {
			BuildStepInterceptor buildStep = (BuildStepInterceptor) service;
			String name = buildStep.getClass().getName();
			String version = getVersion(bundle);

			switch (event.getType()) {
				case ServiceEvent.REGISTERED:
					buildStepInterceptorService.register(name, version, buildStep);
					break;
				case ServiceEvent.UNREGISTERING:
					buildStepInterceptorService.unregister(name, version);
					break;
				case ServiceEvent.MODIFIED:
					buildStepInterceptorService.update(name, version, buildStep);
					break;
			}
		}
	}

	private String getVersion(Bundle bundle) {
		Version version = bundle.getVersion();
		int major = version.getMajor();
		int minor = version.getMinor();
		int micro = version.getMicro();

		return major + "." + minor + "." + micro;
	}
}
