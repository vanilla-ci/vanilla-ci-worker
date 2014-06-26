package com.vanillaci.internal.services;

import com.vanillaci.internal.model.*;
import com.vanillaci.internal.util.*;
import org.osgi.framework.*;

import java.io.*;
import java.net.*;

/**
 * @author Joel Johnson
 */
public class PluginService {
	private final VanillaCiConfig config;
	private final BundleContext bundleContext;

	public PluginService(VanillaCiConfig config, BundleContext bundleContext) {
		this.config = config;
		this.bundleContext = bundleContext;
	}

	public void installPlugin(NewPluginMessage pluginMessage) throws BundleException {
		URI uri = pluginMessage.getUri();

		InputStream inputStream;
		try {
			inputStream = uri.toURL().openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		File tempDir = config.getTempDir();
		File pluginFile = new File(tempDir, pluginMessage.getShaHash() + ".plugindownload");

		// TODO: Write inputStream to pluginFile

		// TODO: verify pluginFile's hash

		// TODO: pass the file's input stream into bundleContext.installBundle(null, inputStream);
	}
}
