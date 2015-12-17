package org.cny.awf.net.http;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.cny.awf.net.http.dlm.DLM;
import org.cny.awf.net.http.dlm.DlmCallback;
import org.cny.awf.util.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

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
	public static Handler H = HCallback.HandlerCallback.H;

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
	public static HAsyncTask doPost(Context ctx, String url, List<BasicNameValuePair> args, PIS pis, HCallback cb) {
		return doPost(ctx, url, args, null, pis, cb);
	}

	public static HAsyncTask doPost(String url, List<BasicNameValuePair> args, List<BasicNameValuePair> heads, PIS pis,
			HCallback cb) {
		return doPost(CTX, url, args, heads, pis, cb);
	}

	public static HAsyncTask doPost(Context ctx, String url, List<BasicNameValuePair> args,
			List<BasicNameValuePair> heads, PIS pis, HCallback cb) {
		return doPostNH(ctx, url, args, heads, pis, new HCallback.HandlerCallback(cb));
	}

	/**
	 * post JSON string to server.
	 * 
	 * @param url
	 *            target URL.
	 * @param args
	 *            arguments to post.
	 * @param cb
	 *            callback.
	 * @return task.
	 * @throws UnsupportedEncodingException
	 */
	public static HAsyncTask doPostData(String url, Args.V json, HCallback cb) throws UnsupportedEncodingException {
		return doPostDataNH(CTX, url, null, json.toString(), new HCallback.HandlerCallback(cb));
	}

	public static HAsyncTask doPostData(String url, Args.V args, Args.V json, HCallback cb)
			throws UnsupportedEncodingException {
		return doPostDataNH(CTX, url, args, json.toString(), new HCallback.HandlerCallback(cb));
	}

	public static HAsyncTask doPostData(String url, Object data, HCallback cb) throws UnsupportedEncodingException {
		return doPostDataNH(CTX, url, null, data.toString(), new HCallback.HandlerCallback(cb));
	}

	public static HAsyncTask doPostData(String url, Args.V args, Object data, HCallback cb)
			throws UnsupportedEncodingException {
		return doPostDataNH(CTX, url, args, data.toString(), new HCallback.HandlerCallback(cb));
	}

	public static HAsyncTask doPostData(Context ctx, String url, Object data, HCallback cb)
			throws UnsupportedEncodingException {
		return doPostDataNH(ctx, url, null, data.toString(), new HCallback.HandlerCallback(cb));
	}

	public static HAsyncTask doPostData(Context ctx, String url, Args.V args, Object data, HCallback cb)
			throws UnsupportedEncodingException {
		return doPostDataNH(ctx, url, args, data.toString(), new HCallback.HandlerCallback(cb));
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
	public static HAsyncTask doPostNH(Context ctx, String url, List<BasicNameValuePair> args, PIS pis, HCallback cb) {
		return doPostNH(ctx, url, args, null, pis, cb);
	}

	public static HAsyncTask doPostNH(String url, List<BasicNameValuePair> args, PIS pis, HCallback cb) {
		return doPostNH(CTX, url, args, null, pis, cb);
	}

	public static HAsyncTask doPostNH(Context ctx, String url, List<BasicNameValuePair> args,
			List<BasicNameValuePair> heads, PIS pis, HCallback cb) {
		HAsyncTask hc = new HAsyncTask(ctx, url, cb);
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
		hc.asyncExec();
		return hc;
	}

	public static HAsyncTask doPostDataNH(String url, String data, HCallback cb) throws UnsupportedEncodingException {
		return doPostDataNH(CTX, url, null, data, cb);
	}

	public static HAsyncTask doPostDataNH(Context ctx, String url, Args.V args, String data, HCallback cb)
			throws UnsupportedEncodingException {
		HAsyncTask hc = new HAsyncTask(ctx, url, cb);
		if (Util.isNoEmpty(data)) {
			hc.setEntity(new StringEntity(data));
		}
		if (args != null) {
			hc.getArgs().addAll(args);
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
	public static HAsyncTask doPost(Context ctx, String url, List<BasicNameValuePair> args, HCallback cb) {
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
	public static HAsyncTask doPost(Context ctx, String url, PIS pis, HCallback cb) {
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
	public static HAsyncTask doPost(String url, List<BasicNameValuePair> args, PIS pis, HCallback cb) {
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
	public static HAsyncTask doPost(String url, List<BasicNameValuePair> args, HCallback cb) {
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

	public static HAsyncTask doPost(String url, String name, Bitmap bm, HCallback cb) {
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
	public static HAsyncTask doGet(Context ctx, String url, List<BasicNameValuePair> args, HCallback cb) {
		return doGet(ctx, url, args, null, cb);
	}

	public static HAsyncTask doGet(String url, List<BasicNameValuePair> args, List<BasicNameValuePair> heads,
			HCallback cb) {
		return doGet(CTX, url, args, heads, cb);
	}

	public static HAsyncTask doGet(Context ctx, String url, List<BasicNameValuePair> args,
			List<BasicNameValuePair> heads, HCallback cb) {
		return doGetNH(ctx, url, args, heads, new HCallback.HandlerCallback(cb));
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
	public static HAsyncTask doGetNH(String url, List<BasicNameValuePair> args, HCallback cb) {
		return doGetNH(CTX, url, args, null, cb);
	}

	public static HAsyncTask doGetNH(String url, List<BasicNameValuePair> args, List<BasicNameValuePair> heads,
			HCallback cb) {
		return doGetNH(CTX, url, args, heads, cb);
	}

	public static HAsyncTask doGetNH(Context ctx, String url, List<BasicNameValuePair> args,
			List<BasicNameValuePair> heads, HCallback cb) {
		HAsyncTask hc = new HAsyncTask(ctx, url, cb);
		if (args != null) {
			hc.getArgs().addAll(args);
		}
		if (heads != null) {
			hc.getHeaders().addAll(heads);
		}
		hc.asyncExec();
		return hc;
	}

	public static HAsyncTask doGetNH(Context ctx, Executor ec, String url, List<BasicNameValuePair> args,
			List<BasicNameValuePair> heads, HCallback cb) {
		HAsyncTask hc = new HAsyncTask(ctx, ec, url, cb);
		if (args != null) {
			hc.getArgs().addAll(args);
		}
		if (heads != null) {
			hc.getHeaders().addAll(heads);
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
	public static HAsyncTask doGet(String url, List<BasicNameValuePair> args, HCallback cb) {
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

	public static String findCache(String url, String m) {
		return CBase.checkCache(CTX, url, m);
	}

	public static HResp findCache2(String url, String m) {
		return CBase.checkCache2(CTX, url, m);
	}

	public static String findCache(String url) {
		return findCache(url, "GET");
	}

	//
	protected static DLM _DLM_;

	public static DLM dlm() {
		if (_DLM_ == null) {
			_DLM_ = new DLM(0, 3, 100);
		}
		return _DLM_;
	}

	public static void initDlm(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
		if (_DLM_ != null) {
			_DLM_.shutdown();
		}
		_DLM_ = new DLM(corePoolSize, maximumPoolSize, keepAliveTime);
	}

	public static String doGet(String url, String spath, List<BasicNameValuePair> args, List<BasicNameValuePair> heads,
			DlmCallback cback) {
		return dlm().put(CTX, url, "GET", spath, args, heads, cback);
	}

	public static String doGet(String url, String spath, DlmCallback cback) {
		return dlm().put(CTX, url, "GET", spath, null, null, cback);
	}

	public static String doPost(String url, String spath, List<BasicNameValuePair> args, List<BasicNameValuePair> heads,
			DlmCallback cback) {
		return dlm().put(CTX, url, "POST", spath, args, heads, cback);
	}

	public static String doPost(String url, String spath, DlmCallback cback) {
		return dlm().put(CTX, url, "POST", spath, null, null, cback);
	}

	public static String doPost(String url, String spath, List<BasicNameValuePair> args, DlmCallback cback) {
		return dlm().put(CTX, url, "POST", spath, args, null, cback);
	}
}
