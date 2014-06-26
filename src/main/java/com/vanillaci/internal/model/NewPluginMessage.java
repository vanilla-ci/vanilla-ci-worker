package com.vanillaci.internal.model;

import org.codehaus.jackson.annotate.*;
import org.jetbrains.annotations.*;

import java.net.*;

/**
 * The message received when a new plugin has been uploaded and this node needs to download it.
 *
 * @author Joel Johnson
 */
public class NewPluginMessage {
	@NotNull private final String shaHash;
	@NotNull private final String location;
	@JsonIgnore @NotNull private final URI uri; //Json ignored since it can be derived from the location field.

	/**
	 * @param shaHash The sha-1 hash of the file.
	 * @param location The location of the plugin. Must be in a format the URI constructor can parse.
	 * @throws URISyntaxException if the location string is an invalid URI.
	 */
	public NewPluginMessage(@NotNull String shaHash, @JsonProperty("location") @NotNull String location) throws URISyntaxException {
		this.shaHash = shaHash;
		this.location = location;
		this.uri = new URI(location);
	}

	@NotNull
	public String getLocation() {
		return location;
	}


	@NotNull
	public String getShaHash() {
		return shaHash;
	}

	@NotNull
	@JsonIgnore
	public URI getUri() {
		return uri;
	}
}
