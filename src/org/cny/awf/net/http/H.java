package org.cny.awf.net.http;

import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

/**
 * Static class method to HTTP GET/POST.
 * 
 * @author cny
 * 
 */
public class H {

	/**
	 * default CTX for HTTP call.it should be set when APP start.
	 */
	public static Context CTX;

	/**
	 * Do a POST request for upload and call back by Handler.
	 * 
	 * @param ctx
	 *            the Context
	 * @param url
	 *            the target URL.
	 * @param args
	 *            the POST arguments.
	 * @param pis
	 *            the process input stream for upload.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPost(Context ctx, String url,
			List<BasicNameValuePair> args, PIS pis, HCallback cb) {
		return doPostNH(ctx, url, args, pis, new HCallback.HandlerCallback(cb));
	}

	/**
	 * Do a POST request for upload and not call back by Handler.
	 * 
	 * @param ctx
	 *            the Context
	 * @param url
	 *            the target URL.
	 * @param args
	 *            the POST arguments.
	 * @param pis
	 *            the process input stream for upload.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPostNH(Context ctx, String url,
			List<BasicNameValuePair> args, PIS pis, HCallback cb) {
		HAsyncTask hc = new HAsyncTask(ctx, url, cb);
		if (args != null) {
			hc.getArgs().addAll(args);
		}
		if (pis != null) {
			hc.addBinary(pis);
		}
		hc.setMethod("POST");
		hc.asyncExec();
		return hc;
	}

	/**
	 * Do a POST request.
	 * 
	 * @param ctx
	 *            the Context
	 * @param url
	 *            the target URL.
	 * @param args
	 *            the POST arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPost(Context ctx, String url,
			List<BasicNameValuePair> args, HCallback cb) {
		return doPost(ctx, url, args, null, cb);
	}

	/**
	 * Do a POST request.
	 * 
	 * @param ctx
	 *            the Context
	 * @param url
	 *            the target URL.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPost(Context ctx, String url, HCallback cb) {
		return doPost(ctx, url, (PIS) null, cb);
	}

	/**
	 * Do a POST request.
	 * 
	 * @param ctx
	 *            the Context
	 * @param url
	 *            the target URL.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPost(Context ctx, String url, PIS pis,
			HCallback cb) {
		return doPost(ctx, url, null, pis, cb);

	}

	/**
	 * Do a POST request,using common CTX.
	 * 
	 * @param url
	 *            the target URL.
	 * @param args
	 *            the POST arguments.
	 * @param pis
	 *            the process input stream for upload.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPost(String url, List<BasicNameValuePair> args,
			PIS pis, HCallback cb) {
		return doPost(CTX, url, args, pis, cb);
	}

	/**
	 * Do a POST request, using common CTX.
	 * 
	 * @param url
	 *            the target URL.
	 * @param args
	 *            the POST arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPost(String url, List<BasicNameValuePair> args,
			HCallback cb) {
		return doPost(CTX, url, args, cb);
	}

	/**
	 * Do a POST request,using common CTX.
	 * 
	 * @param url
	 *            the target URL.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPost(String url, HCallback cb) {
		return doPost(url, (PIS) null, cb);
	}

	/**
	 * Do a POST request,using common CTX.
	 * 
	 * @param url
	 *            the target URL.
	 * @param pis
	 *            the process input stream for upload.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doPost(String url, PIS pis, HCallback cb) {
		return doPost(url, null, pis, cb);
	}

	/**
	 * Do a GET request and call back by Handler.
	 * 
	 * @param ctx
	 *            the Context
	 * @param url
	 *            the target URL
	 * @param args
	 *            the GET arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doGet(Context ctx, String url,
			List<BasicNameValuePair> args, HCallback cb) {
		return doGetNH(ctx, url, args, new HCallback.HandlerCallback(cb));
	}

	/**
	 * Do a GET request and not call back by Handler.
	 * 
	 * @param ctx
	 *            the Context
	 * @param url
	 *            the target URL
	 * @param args
	 *            the GET arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doGetNH(Context ctx, String url,
			List<BasicNameValuePair> args, HCallback cb) {
		HAsyncTask hc = new HAsyncTask(ctx, url, cb);
		if (args != null) {
			hc.getArgs().addAll(args);
		}
		hc.asyncExec();
		return hc;
	}

	/**
	 * Do a GET request and call back by Handler, using common CTX.
	 * 
	 * @param url
	 *            the target URL
	 * @param args
	 *            the GET arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doGet(String url, List<BasicNameValuePair> args,
			HCallback cb) {
		return doGet(CTX, url, args, cb);
	}

	/**
	 * Do a GET request and call back by Handler.
	 * 
	 * @param ctx
	 *            the Context
	 * @param url
	 *            the target URL
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static HAsyncTask doGet(Context ctx, String url, HCallback cb) {
		return doGet(ctx, url, null, cb);
	}

	/**
	 * Do a GET request and call back by Handler, using common CTX.
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
	 * default constructor.
	 */
	protected H() {
	}
}
