package org.cny.awf.net.http;

import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

public class Args {
	public static class V {
		public List<BasicNameValuePair> Args;

		public V A(String name, Object value) {
			this.Args.add(new BasicNameValuePair(name, value.toString()));
			return this;
		}

		public V J(String name, Object value) {
			return this.A(name, new Gson().toJson(value));
		}
	}

	public static V A(String name, Object value) {
		return new V().A(name, value);
	}

	public static V J(String name, Object value) {
		return new V().J(name, value);
	}
}
