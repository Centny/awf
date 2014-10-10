package org.cny.amf.net.http;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * the normal HTTP client extends HTTPClient for GET/POST.
 * 
 * @author cny
 * 
 */
public abstract class HClientM extends HClient {

	private String method = "GET";

	/**
	 * default constructor by URL and call back.
	 * 
	 * @param url
	 *            the URL.
	 * @param cback
	 *            the HTTPCallback.
	 */
	public HClientM(String url, HCallback cback) {
		super(url, cback);
	}

	/**
	 * Set the request method,default GET.
	 * 
	 * @param method
	 *            the target method.
	 * @return the HTTPMClient instance.
	 */
	public HClientM setMethod(String method) {
		this.method = method;
		return this;
	}

	/**
	 * Get the request method.
	 * 
	 * @return the method
	 */
	public String getMethod() {
		return this.method;
	}

	private String gurl() {
		String params = URLEncodedUtils.format(this.args, this.rencoding);
		if (params.length() > 0) {
			if (this.url.indexOf("?") > 0) {
				return this.url + "&" + params;
			} else {
				return this.url + "?" + params;
			}
		} else {
			return this.url;
		}
	}

	@Override
	public HttpUriRequest createRequest() throws Exception {
		if ("GET".equals(this.method)) {
			HttpGet get = new HttpGet(this.gurl());
			for (NameValuePair nv : this.headers) {
				get.addHeader(nv.getName(), new String(
						nv.getValue().getBytes(), "ISO-8859-1"));
			}
			return get;
		} else if ("POST".equals(this.method)) {
			HttpPost post = new HttpPost(this.url);
			post.setEntity(new UrlEncodedFormEntity(this.args, this
					.getRencoding()));
			for (NameValuePair nv : this.headers) {
				post.addHeader(nv.getName(), new String(nv.getValue()
						.getBytes(), "ISO-8859-1"));
			}
			return post;
		} else {
			return null;
		}
	}

	public String query(String inc) {
		try {
			List<NameValuePair> args_ = new ArrayList<NameValuePair>();
			if ("GET".equals(method)) {
				for (NameValuePair nvp : URLEncodedUtils.parse(
						new URI(this.gurl()), this.rencoding)) {
					if (inc != null && nvp.getName().matches(inc)) {
						continue;
					} else {
						args_.add(nvp);
					}
				}
			} else if ("POST".equals(this.method)) {
				for (NameValuePair nvp : this.args) {
					if (inc != null && nvp.getName().matches(inc)) {
						continue;
					} else {
						args_.add(nvp);
					}
				}
			}
			return URLEncodedUtils.format(args_, this.rencoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String findParameter(String key) {
		try {
			if ("GET".equals(method)) {
				for (NameValuePair nvp : URLEncodedUtils.parse(
						new URI(this.gurl()), this.rencoding)) {
					if (key.equals(nvp.getName())) {
						return nvp.getValue();
					}
				}
			} else if ("POST".equals(this.method)) {
				for (NameValuePair nvp : this.args) {
					if (key.equals(nvp.getName())) {
						return nvp.getValue();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public HResp doRequest(HttpClient hc, HttpUriRequest uri) throws Exception {
		return new HMResp(hc.execute(uri));
	}

	@Override
	public void doneRequest(HClient c, HResp r) throws Exception {

	}
}
