package org.cny.amf.net.http;

public class HCacheException extends RuntimeException {

	private String uri;
	private String method;
	private String query;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5671333053140109443L;

	public HCacheException(String uri, String method, String query) {
		super("cache by " + uri + "," + method + "," + query + " not found");
		this.uri = uri;
		this.method = method;
		this.query = query;
	}

	public String getUri() {
		return uri;
	}

	public String getMethod() {
		return method;
	}

	public String getQuery() {
		return query;
	}

}
