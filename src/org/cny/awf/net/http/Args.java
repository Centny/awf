package org.cny.awf.net.http;

import java.util.List;

import org.apache.http.message.BasicNameValuePair;

public class Args {
	public static class V {
		public List<BasicNameValuePair> Args;

		public V A(String name, Object value) {
			this.Args.add(new BasicNameValuePair(name, value.toString()));
			return this;
		}
	}

	public static V A(String name, Object value) {
		return new V().A(name, value);
	}
}
