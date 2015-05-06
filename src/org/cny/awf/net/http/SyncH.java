package org.cny.awf.net.http;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.cny.awf.util.Util;

import android.content.Context;
import android.graphics.Bitmap;

public class SyncH {
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
	public static C doPost(Context ctx, String url,
			List<BasicNameValuePair> args, PIS pis, HCallback cb) {
		return doPost(ctx, url, args, null, pis, cb);
	}

	public static C doPost(String url, List<BasicNameValuePair> args,
			List<BasicNameValuePair> heads, PIS pis, HCallback cb) {
		return doPost(H.CTX, url, args, heads, pis, cb);
	}

	public static C doPost(Context ctx, String url,
			List<BasicNameValuePair> args, List<BasicNameValuePair> heads,
			PIS pis, HCallback cb) {
		return doPostNH(ctx, url, args, heads, pis, cb);
	}

	public static C doPostData(String url, String data, HCallback cb)
			throws UnsupportedEncodingException {
		return doPostData(H.CTX, url, data, cb);
	}

	public static C doPostData(Context ctx, String url, String data,
			HCallback cb) throws UnsupportedEncodingException {
		return doPostDataNH(ctx, url, data, cb);
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
	public static C doPostNH(Context ctx, String url,
			List<BasicNameValuePair> args, PIS pis, HCallback cb) {
		return doPostNH(ctx, url, args, null, pis, cb);
	}

	public static C doPostNH(String url, List<BasicNameValuePair> args,
			PIS pis, HCallback cb) {
		return doPostNH(H.CTX, url, args, null, pis, cb);
	}

	public static C doPostNH(Context ctx, String url,
			List<BasicNameValuePair> args, List<BasicNameValuePair> heads,
			PIS pis, HCallback cb) {
		C hc = new C(ctx, url, cb);
		if (args != null) {
			hc.getArgs().addAll(args);
		}
		if (pis != null) {
			hc.addBinary(pis);
		}
		if (heads != null) {
			hc.getHeaders().addAll(heads);
		}
		hc.setMethod("POST");
		hc.run();
		return hc;
	}

	public static C doPostDataNH(String url, String data, HCallback cb)
			throws UnsupportedEncodingException {
		return doPostDataNH(H.CTX, url, data, cb);
	}

	public static C doPostDataNH(Context ctx, String url, String data,
			HCallback cb) throws UnsupportedEncodingException {
		C hc = new C(ctx, url, cb);
		if (Util.isNoEmpty(data)) {
			hc.setEntity(new StringEntity(data));
		}
		hc.setMethod("POST");
		hc.run();
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
	public static C doPost(Context ctx, String url,
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
	public static C doPost(Context ctx, String url, HCallback cb) {
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
	public static C doPost(Context ctx, String url, PIS pis, HCallback cb) {
		return doPost(ctx, url, null, pis, cb);

	}

	/**
	 * Do a POST request,using common H.CTX.
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
	public static C doPost(String url, List<BasicNameValuePair> args, PIS pis,
			HCallback cb) {
		return doPost(H.CTX, url, args, pis, cb);
	}

	/**
	 * Do a POST request, using common H.CTX.
	 * 
	 * @param url
	 *            the target URL.
	 * @param args
	 *            the POST arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static C doPost(String url, List<BasicNameValuePair> args,
			HCallback cb) {
		return doPost(H.CTX, url, args, cb);
	}

	/**
	 * Do a POST request,using common H.CTX.
	 * 
	 * @param url
	 *            the target URL.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static C doPost(String url, HCallback cb) {
		return doPost(url, (PIS) null, cb);
	}

	/**
	 * Do a POST request,using common H.CTX.
	 * 
	 * @param url
	 *            the target URL.
	 * @param pis
	 *            the process input stream for upload.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static C doPost(String url, PIS pis, HCallback cb) {
		return doPost(url, null, pis, cb);
	}

	public static C doPost(String url, String name, Bitmap bm, HCallback cb) {
		return doPost(url, null, PIS.create(name, bm), cb);
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
	public static C doGet(Context ctx, String url,
			List<BasicNameValuePair> args, HCallback cb) {
		return doGet(ctx, url, args, null, cb);
	}

	public static C doGet(String url, List<BasicNameValuePair> args,
			List<BasicNameValuePair> heads, HCallback cb) {
		return doGet(H.CTX, url, args, heads, cb);
	}

	public static C doGet(Context ctx, String url,
			List<BasicNameValuePair> args, List<BasicNameValuePair> heads,
			HCallback cb) {
		return doGetNH(ctx, url, args, heads, cb);
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
	public static C doGetNH(String url, List<BasicNameValuePair> args,
			HCallback cb) {
		return doGetNH(H.CTX, url, args, null, cb);
	}

	public static C doGetNH(String url, List<BasicNameValuePair> args,
			List<BasicNameValuePair> heads, HCallback cb) {
		return doGetNH(H.CTX, url, args, heads, cb);
	}

	public static C doGetNH(Context ctx, String url,
			List<BasicNameValuePair> args, List<BasicNameValuePair> heads,
			HCallback cb) {
		C hc = new C(ctx, url, cb);
		if (args != null) {
			hc.getArgs().addAll(args);
		}
		if (heads != null) {
			hc.getHeaders().addAll(heads);
		}
		hc.run();
		return hc;
	}

	/**
	 * Do a GET request and call back by Handler, using common H.CTX.
	 * 
	 * @param url
	 *            the target URL
	 * @param args
	 *            the GET arguments.
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static C doGet(String url, List<BasicNameValuePair> args,
			HCallback cb) {
		return doGet(H.CTX, url, args, cb);
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
	public static C doGet(Context ctx, String url, HCallback cb) {
		return doGet(ctx, url, null, cb);
	}

	/**
	 * Do a GET request and call back by Handler, using common H.CTX.
	 * 
	 * @param url
	 *            the target URL
	 * @param cb
	 *            HTTP call back instance.
	 * @return the HTTPAsyncTask.
	 */
	public static C doGet(String url, HCallback cb) {
		return doGet(url, null, cb);
	}
}
