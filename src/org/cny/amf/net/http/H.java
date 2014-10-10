package org.cny.amf.net.http;

import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.cny.amf.net.http.HCallback.HDownCallback;

/**
 * Static class method to HTTP GET/POST.
 * 
 * @author cny
 * 
 */
public class H {
	/**
	 * Do a POST request.
	 * 
	 * @param url
	 *            the target URL.
	 * @param args
	 *            the POST arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPost(String url,
			List<BasicNameValuePair> args, HCallback cb) {
		HAsyncTask hc = new HAsyncTask(url, cb);
		if (args != null) {
			hc.getArgs().addAll(args);
		}
		hc.setMethod("POST");
		hc.asyncExec();
		return hc;
	}

	/**
	 * Do a GET request.
	 * 
	 * @param url
	 *            the target URL
	 * @param args
	 *            the GET arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doGet(String url,
			List<BasicNameValuePair> args, HCallback cb) {
		HAsyncTask hc = new HAsyncTask(url, cb);
		if (args != null) {
			hc.getArgs().addAll(args);
		}
		hc.setMethod("GET");
		hc.asyncExec();
		return hc;
	}

	/**
	 * Do a GET request.
	 * 
	 * @param url
	 *            the target URL
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doGet(String url, HCallback cb) {
		return doGet(url, null, cb);
	}

	/**
	 * Do a GET download.
	 * 
	 * @param url
	 *            the target URL.
	 * @param args
	 *            the GET arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doGetDown(String url,
			List<BasicNameValuePair> args, HDownCallback cb) {
		HAsyncTask dc = new HAsyncTask(url, cb);
		if (args != null) {
			dc.getArgs().addAll(args);
		}
		dc.setMethod("GET");
		dc.asyncExec();
		return dc;
	}

	/**
	 * Do a GET download.
	 * 
	 * @param url
	 *            the target URL.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doGetDown(String url, HDownCallback cb) {
		return doGetDown(url, null, cb);
	}

	/**
	 * Do a POST download.
	 * 
	 * @param url
	 *            the target URL.
	 * @param args
	 *            the POST arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPostDown(String url,
			List<BasicNameValuePair> args, HDownCallback cb) {
		HAsyncTask dc = new HAsyncTask(url, cb);
		if (args != null) {
			dc.getArgs().addAll(args);
		}
		dc.setMethod("POST");
		dc.asyncExec();
		return dc;
	}
}
