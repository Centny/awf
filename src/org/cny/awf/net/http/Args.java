package org.cny.awf.net.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Args {
	private static Logger L = LoggerFactory.getLogger(Args.class);

	public static class V extends ArrayList<BasicNameValuePair> {
		// public final List<BasicNameValuePair> Args = new
		// ArrayList<BasicNameValuePair>();

		private static final long serialVersionUID = 5364764081478877788L;

		public V A(String name, Object value) {
			this.add(new BasicNameValuePair(name, value.toString()));
			return this;
		}

		public V J(String name, Object value) {
			if (value == null) {
				L.debug("the value is null for name " + name);
				return this;
			}
			return this.A(name, new Gson().toJson(value));
		}

		@Override
		public String toString() {
			return new Gson().toJson(this.asMap());
		}

		public Map<String, String> asMap() {
			Map<String, String> args = new HashMap<String, String>();
			for (BasicNameValuePair kv : this) {
				args.put(kv.getName(), kv.getValue());
			}
			return args;
		}

		public VAry asAry() {
			return new VAry().A(this);
		}
	}

	public static class VAry extends ArrayList<V> {
		private static final long serialVersionUID = 4104938699957042745L;

		public VAry A(V args) {
			this.add(args);
			return this;
		}

		@Override
		public String toString() {
			List<Map<String, String>> args = new ArrayList<Map<String, String>>();
			for (V arg : this) {
				args.add(arg.asMap());
			}
			return new Gson().toJson(args);
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
