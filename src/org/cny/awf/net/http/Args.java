package org.cny.awf.net.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Args {
	private static Logger L = LoggerFactory.getLogger(Args.class);

	public static class V {
		public final List<BasicNameValuePair> Args = new ArrayList<BasicNameValuePair>();

		public V A(String name, Object value) {
			this.Args.add(new BasicNameValuePair(name, value.toString()));
			return this;
		}

		public V J(String name, Object value) {
			if (value == null) {
				L.debug("the value is null for name " + name);
				return this;
			}
			return this.A(name, new Gson().toJson(value));
		}
	}

	public static V A(String name, Object value) {
		return new V().A(name, value);
	}

	public static V J(String name, Object value) {
		return new V().J(name, value);
	}

	protected Args() {

	}
}
