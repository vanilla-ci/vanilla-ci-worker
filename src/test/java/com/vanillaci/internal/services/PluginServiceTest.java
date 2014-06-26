package com.vanillaci.internal.services;

import com.vanillaci.internal.exceptions.*;
import com.vanillaci.internal.model.*;
import com.vanillaci.internal.util.*;
import org.junit.*;
import org.osgi.framework.*;

import java.io.*;
import java.util.concurrent.atomic.*;

import static org.mockito.Mockito.*;

/**
 * @author Joel Johnson
 */
public class PluginServiceTest {
	AtomicBoolean installCalled = new AtomicBoolean(false);
	PluginService pluginService;

	@Before
	public void setUp() throws Exception {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));

		VanillaCiConfig config = mock(VanillaCiConfig.class);
		when(config.getTempDir()).thenReturn(tempDir);

		BundleContext bundleContext = mock(BundleContext.class);
		when(bundleContext.installBundle(anyString(), any(InputStream.class))).thenAnswer(invocation -> {
			installCalled.set(true);
			return null;
		});

		pluginService = new PluginService(config, bundleContext);
	}

	@Test
	public void testBasicInstall() throws Exception {
		// Of course when we run this, the file will need to be an OSGi Jar. But we're using a mocked context anyway.
		File resourceFile = TestUtil.getResourceFile("testFile.txt");

		NewPluginMessage pluginMessage = new NewPluginMessage("27a0d9074218b8048e9fe1d89c8f70149adc20e6", resourceFile.toURI().toString());
		pluginService.installPlugin(pluginMessage);
		assert installCalled.get() : "plugin should have been installed";
	}

	@Test(expected = ChecksumException.class)
	public void testShaRejection() throws Exception {
		// Of course when we run this, the file will need to be an OSGi Jar. But we're using a mocked context anyway.
		File resourceFile = TestUtil.getResourceFile("testFile.txt");

		NewPluginMessage pluginMessage = new NewPluginMessage("baconbaconbaconbaconbaconbaconbaconbacon", resourceFile.toURI().toString());
		pluginService.installPlugin(pluginMessage);
		assert !installCalled.get() : "plugin should not have been installed";
	}
}
