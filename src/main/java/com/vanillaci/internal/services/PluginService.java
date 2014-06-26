package com.vanillaci.internal.services;

import com.vanillaci.internal.exceptions.*;
import com.vanillaci.internal.model.*;
import com.vanillaci.internal.util.*;
import org.apache.logging.log4j.*;
import org.osgi.framework.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;

/**
 * Used for installing a Plugin from a {@link com.vanillaci.internal.model.NewPluginMessage}.
 *
 * @author Joel Johnson
 */
public class PluginService {
	private static final Logger log = LogManager.getLogger();
	private static final StopWatch stopwatch = new StopWatch(log);

	private final VanillaCiConfig config;
	private final BundleContext bundleContext;

	public PluginService(VanillaCiConfig config, BundleContext bundleContext) {
		this.config = config;
		this.bundleContext = bundleContext;
	}

	/**
	 * Downloads, checksums, and installs the plugin from the location contains in the given pluginMessage
	 * @throws BundleException
	 * @throws IOException
	 */
	public void installPlugin(NewPluginMessage pluginMessage) throws BundleException, IOException {
		log.info("Installing plugin from " + pluginMessage.getLocation() + " with hash " + pluginMessage.getShaHash());

		URI uri = pluginMessage.getUri();

		log.info("Downloading " + pluginMessage.getLocation());
		InputStream inputStream = uri.toURL().openStream();

		File tempDir = config.getTempDir();
		File pluginFile = new File(tempDir, pluginMessage.getShaHash() + ".plugin");
		try {
			stopwatch.time("Downloading: " + pluginMessage.getLocation(),
				() -> Files.copy(inputStream, pluginFile.toPath())
			);

			log.info("Verifying " + pluginMessage.getLocation() + " hash.");
			String actualHash = ShaUtil.getSha(pluginFile);
			if(!actualHash.equals(pluginMessage.getShaHash())) {
				throw new ChecksumException(pluginMessage.getLocation(), pluginMessage.getShaHash(), actualHash);
			}

			try(InputStream pluginInputStream = new FileInputStream(pluginFile)) {
				log.info("Installing: " + pluginFile.toString());
				bundleContext.installBundle(null, pluginInputStream);
			}
		} finally {
			log.info("Deleting temp plugin file: " + pluginFile.toString());
			if(!pluginFile.delete() && pluginFile.exists()) {
				log.error("Unable to delete temp plugin file: " + pluginFile.getAbsolutePath());
			}
		}

		log.info("Successfully installed " + pluginMessage.getLocation());
	}
}
