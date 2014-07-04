package com.vanillaci.internal.util;

import com.vanillaci.internal.exceptions.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

/**
 * Helper class to retrieve properties from the properties file.
 * @author Joel Johnson
 */
public class VanillaCiConfig {
	private static final Logger log = LogManager.getLogger();

	@NotNull
	private final Properties properties;

	@NotNull
	private final File homeDirectory; // Since this is so commonly used, just hang on to it.

	@NotNull
	public static VanillaCiConfig createDefault() {
		File propertiesFile;

		String propertiesFileLocation = System.getenv("VANILLACI_HOME");
		if (propertiesFileLocation == null) {
			log.info("VANILLACI_HOME environment variable not set.");

			propertiesFileLocation = System.getenv("HOME") + "/.vanillaci";
		}
		propertiesFile = new File(propertiesFileLocation);

		log.info("Using properties file: " + propertiesFile.getAbsolutePath());

		try {
			return new VanillaCiConfig(propertiesFile);
		} catch (FileNotFoundException e) {
			throw new VanillaCiFileException("Properties file not found: " + propertiesFileLocation + " (" + propertiesFile.getAbsolutePath() + ")", e);
		} catch (IOException e) {
			throw new VanillaCiFileException("Unable to load properties file: " + propertiesFileLocation + " (" + propertiesFile.getAbsolutePath() + ")", e);
		}
	}

	public VanillaCiConfig(File homeDirectory) throws IOException {
		if(!homeDirectory.exists() && !homeDirectory.mkdirs()) {
			throw new VanillaCiFileException("could not create home directory: " + homeDirectory);
		}
		this.homeDirectory = homeDirectory;

		// Load the config file, if it exists
		File configFile = new File(this.homeDirectory, "config.properties");

		Properties properties = new Properties();
		if(configFile.exists()) {
			try(FileInputStream configFileInputStream = new FileInputStream(configFile)) {
				properties.load(configFileInputStream);
			}
		}
		this.properties = properties;
	}

	@NotNull
	public File getHomeDirectory() {
		return homeDirectory;
	}

	@NotNull
	public File getWorkspacesDirectory() {
		File workspacesDirectory = new File(getHomeDirectory(), "workspaces");
		if(!workspacesDirectory.exists() && !workspacesDirectory.mkdirs()) {
			throw new VanillaCiFileException("Unable to create workspaces directory: " + workspacesDirectory.getAbsolutePath());
		}

		return workspacesDirectory;
	}

	public File getTempDir() {
		return new File(getHomeDirectory(), "temp");
	}

	public int getWeight() {
		String weightString = properties.getProperty("weight", "5");
		return Integer.parseInt(weightString);
	}

	@NotNull
	public String getExpression() {
		return properties.getProperty("expression", "");
	}
}
