package sd2223.trab1.api.discovery;

import java.net.*;
import java.sql.*;
import java.util.Objects;

public class URIEntry {

	private URI uri;
	private Timestamp timestamp;

	public URIEntry(URI uri, Timestamp timestamp) {
		this.uri = uri;
		this.timestamp = timestamp;
	}

	public URIEntry(String uri, Timestamp timestamp) {
		this(URI.create(uri), timestamp);
	}

	public URI getUri() {
		return uri;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uri);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof URIEntry))
			return false;
		URIEntry other = (URIEntry) obj;
		return Objects.equals(uri, other.uri);
	}

	@Override
	public String toString() {
		return "URIEntry [uri=" + uri + ", timestamp=" + timestamp + "]";
	}

}
