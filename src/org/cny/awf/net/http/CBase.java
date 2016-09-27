package org.cny.awf.net.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.cny.awf.util.MultiOutputStream;
import org.cny.awf.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.util.Pair;

public abstract class CBase implements Runnable, PIS.PisH {
	public static enum Policy {
		C, // cache only.
		CN, // cache first.
		N, // normal HTTP cache.
		I, // the image cache.
		NO, // not cache
	}

	public static boolean ShowLog = false;
	public static final int BUF_SIZE = 1024;
	private static Logger L = LoggerFactory.getLogger(CBase.class);
	protected List<NameValuePair> headers = new ArrayList<NameValuePair>();
	protected List<NameValuePair> args = new ArrayList<NameValuePair>();
	protected List<PIS> files = new ArrayList<PIS>();
	protected String cencoding = "UTF-8";
	protected String sencoding = "UTF-8";
	protected int bsize = BUF_SIZE;
	protected boolean running = false;
	protected HttpEntity entity;
	//
	protected String url;
	protected HDb db;
	protected HCallback cback;
	protected long lastProcess;

	public CBase(String url, HDb db, HCallback cback) {
		this.setUrl(url);
		this.db = db;
		this.cback = cback;
	}

	public void addArg(String key, String val) {
		if (Util.isNullOrEmpty(key) || Util.isNullOrEmpty(val)) {
			return;
		}
		this.args.add(new BasicNameValuePair(key, val));
	}

	public void addHead(String key, String val) {
		if (Util.isNullOrEmpty(key) || Util.isNullOrEmpty(val)) {
			return;
		}
		this.headers.add(new BasicNameValuePair(key, val));
	}

	public void addBinary(PIS pis) {
		this.files.add(pis);
		pis.setH(this);
	}

	public String findArg(String key) {
		for (NameValuePair arg : this.args) {
			if (arg.getName().equals(key)) {
				return arg.getValue();
			}
		}
		return "";
	}

	public String findHead(String key) {
		for (NameValuePair arg : this.headers) {
			if (arg.getName().equals(key)) {
				return arg.getValue();
			}
		}
		return "";
	}

	public void setUrl(String url) {
		this.args.clear();
		this.headers.clear();
		String[] urls = url.split("\\?", 0);
		this.url = urls[0];
		if (urls.length < 2) {
			return;
		}
		for (String arg : urls[1].split("&")) {
			String[] kv = arg.split("=", 0);
			if (kv.length < 2) {
				continue;
			}
			this.addArg(kv[0], kv[1]);
		}
	}

	public String getUrl() {
		return this.url;
	}

	public String getQuery() {
		return parseQuery(this.args, this.cencoding);
	}

	public String getFullUrl() {
		String url = this.getUrl();
		String args = this.getQuery();
		if (Util.isNoEmpty(args)) {
			url += "?" + args;
		}
		return url;
	}

	public static String checkCache(Context ctx, String url, String m) {
		HDb db = HDb.loadDb_(ctx);
		HResp res = findCache(db, url, m);
		if (res == null || res.path == null) {
			return null;
		}
		File fc = db.checkCacheF(res.path);
		if (fc == null) {
			return null;
		}
		return fc.getAbsolutePath();
	}

	public static HResp checkCache2(Context ctx, String url, String m) {
		HDb db = HDb.loadDb_(ctx);
		HResp res = findCache(db, url, m);
		if (res == null || res.path == null) {
			return null;
		}
		File fc = db.checkCacheF(res.path);
		if (fc == null) {
			return null;
		}
		return res;
	}

	public static HResp findCache(HDb db, String url, String m) {
		Pair<String, List<NameValuePair>> uargs = parseUrlArgs(url);
		String args = parseQuery(uargs.second, "UTF-8");
		return db.find(uargs.first, m, args);
	}

	public static String parseUrl(String url) {
		Pair<String, List<NameValuePair>> uargs = parseUrlArgs(url);
		if (uargs.second.isEmpty()) {
			return uargs.first;
		} else {
			return uargs.first + "?" + parseQuery(uargs.second, "UTF-8");
		}
	}

	public static Pair<String, List<NameValuePair>> parseUrlArgs(String url) {
		String[] urls = url.split("\\?", 0);
		List<NameValuePair> args_ = new ArrayList<NameValuePair>();
		if (urls.length > 1) {
			for (String arg : urls[1].split("&")) {
				String[] kv = arg.split("=", 0);
				if (kv.length < 2) {
					continue;
				}
				args_.add(new BasicNameValuePair(kv[0], kv[1]));
			}

		}
		return new Pair<String, List<NameValuePair>>(urls[0], args_);
	}

	public static String parseQuery(List<NameValuePair> args, String cencoding) {
		List<NameValuePair> args_ = new ArrayList<NameValuePair>();
		for (NameValuePair arg : args) {
			if (arg.getName().equals("_hc_")) {
				continue;
			} else {
				args_.add(arg);
			}
		}
		Collections.sort(args_, new Comparator<NameValuePair>() {

			@Override
			public int compare(NameValuePair arg0, NameValuePair arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}

		});
		return URLEncodedUtils.format(args_, cencoding);
	}

	public Policy parsePolicy() {
		// String hc = this.findArg("_hc_");
		// if (hc == null) {
		// return Policy.N;
		// }
		try {
			return Policy.valueOf(this.findArg("_hc_"));
		} catch (Exception e) {
			return Policy.N;
		}
	}

	@Override
	public void run() {
		this.running = true;
		try {
			this.exec();
		} catch (Exception e) {
			this.cback.onExecErr(this, e);
		}
		this.running = false;
	}

	protected HResp find(Policy pc) {
		if (pc == Policy.NO) {
			return null;
		} else {
			return this.db.find(this.getUrl(), this.getMethod(), this.getQuery());
		}
	}

	public String readCache() {
		HResp res = this.find(this.parsePolicy());
		return this.readCahce(res);
	}

	public String readCahce(HResp res) {
		try {
			if (this.db.CacheExist(res)) {
				return res.readCache(this.db);
			} else {
				return null;
			}
		} catch (Exception e) {
			L.warn("read cache error:", e.getMessage());
			return null;
		}
	}

	private void slog(String msg, Policy pc) {
		if (ShowLog) {
			L.info("{} for {}:{}?{}({})", msg, this.getMethod(), this.getUrl(), this.getQuery(), pc);
		}
	}

	@SuppressWarnings("resource")
	protected void exec() throws Exception {
		HResp res = null;
		InputStream in = null;
		MultiOutputStream out = null;
		try {
			Policy pc = this.parsePolicy();
			res = this.find(pc);
			if (res == null) {
				res = new HResp().init(this);
			}
			res = this.createRes(res, pc);
			in = res.getIn();
			if (in == null) {
				this.onSuccess(res);
				return;
			}
			out = this.createO(res, pc);
			long clen = res.len;
			long rsize = 0;
			byte[] buf = new byte[this.bsize];
			int length = -1;
			while ((length = in.read(buf)) != -1) {
				out.write(buf, 0, length);
				rsize += length;
				this.onProcess(res, clen, rsize, length);
				if (!this.running) {
					throw new InterruptedException("Transfter file stopped");
				}
			}
			this.onProcEnd(res, in, out);
			this.onSuccess(res);
			if (pc != Policy.NO) {
				res.time = new Date().getTime();
				if (res.tid > 0) {
					this.slog("update cache for", pc);
					this.db.update(res);
				} else {
					this.slog("adding cache for", pc);
					this.db.add(res);
				}
			}
		} catch (Exception e) {
			this.onError(new Exception(this.url + "," + this.getMethod() + "->" + e.getMessage(), e));
		} finally {
			this.closea(res, in, out);
		}
	}

	protected void closea(HResp res, InputStream in, OutputStream out) {
		res.close();
		try {
			in.close();
		} catch (Exception e) {

		}
		try {
			out.close();
		} catch (Exception e) {

		}
		for (PIS pis : this.files) {
			try {
				pis.close();
			} catch (Exception e) {

			}
		}
	}

	protected HResp createRes(HResp res, Policy pc) throws Exception {
		HttpUriRequest uri;
		boolean exist = this.db.CacheExist(res);
		if (exist && pc != Policy.NO) {
			if (pc == Policy.C || pc == Policy.CN) {
				res.code = 304;
				this.slog("using cache", pc);
				return res.initFileStream(this.db);
			} else if (pc == Policy.I) {
				res.code = 304;
				this.slog("using cache", pc);
				return res.initPathStream(this.db);
			}
			this.onCache(res);
			this.onCreateR(res, pc);
			uri = this.createR();
			if (pc == Policy.N) {
				if (res.lmt > 0) {
					uri.addHeader("If-Modified-Since", HResp.formatLmt(new Date(res.lmt)));
				}
				if (!Util.isNullOrEmpty(res.etag)) {
					uri.addHeader("If-None-Match", res.etag);
				}
				this.slog("check modified to server", pc);
			}
		} else {
			if (pc == Policy.C) { // if cache not found and only cache policy
				throw new Exception("cache not found");
			}
			this.onCreateR(res, pc);
			uri = this.createR();
			this.slog("not cache", pc);
		}
		HttpClient c = this.createC();
		HttpResponse resp = c.execute(uri);
		if (resp.getStatusLine().getStatusCode() == 304 && exist) {
			this.slog("using cache(304)", pc);
			res.code = 304;
			return res;
		} else {
			res.code = resp.getStatusLine().getStatusCode();
			res.init(resp, this.sencoding);
			this.slog("request server data", pc);
			return res.initHttpStream(resp);
		}
	}

	@SuppressWarnings("resource")
	protected MultiOutputStream createO(HResp res, Policy pc) throws Exception {
		if (res.code == 304) {
			// not need download to file.
			return new MultiOutputStream(null, this.cback.createO(this, res)).mark(0, false);
		}
		File cf = null;
		if (this.db.CacheExist(res)) {
			cf = this.db.openCacheF(res.path);
		} else {
			cf = this.db.newCacheF();
			res.path = cf.getName();
		}
		OutputStream tout = this.cback.createO(this, res);
		if (pc == Policy.I) {
			// in image policy,only write the path to call back
			// output stream.
			tout.write(cf.getAbsolutePath().getBytes());
			return new MultiOutputStream(new FileOutputStream(cf), tout).mark(1, false);
		} else {
			return new MultiOutputStream(new FileOutputStream(cf), tout).mark(1, true);
		}
	}

	protected void onProcEnd(HResp res, InputStream in, MultiOutputStream o) throws Exception {
		if (res.code == 304) {
			this.cback.onProcEnd(this, res, o.at(1));
			return;
		}
		Exception err = null;
		try {
			in.close();
		} catch (Exception e) {
			err = e;
		}
		try {
			o.close(0);
			this.cback.onProcEnd(this, res, o.at(1));
		} catch (Exception e) {
			err = e;
		}
		if (err != null) {
			throw err;
		}
	}

	protected void onProcess(HResp res, long clen, long rsize, long period) {
		this.cback.onProcess(this, clen, rsize, period);
	}

	// protected void onProcess(long rsize, long clen) {
	// if (clen > 0) {
	// this.onProcess((float) (((double) rsize) / ((double) clen)));
	// } else {
	// this.onProcess((float) 0);
	// }
	// }

	protected void onCache(HResp res) throws Exception {
		this.cback.onCache(this, res);
	}
	//
	// protected void onProcess(float rate) {
	// this.cback.onProcess(this, rate);
	// }

	protected void onSuccess(HResp res) throws Exception {
		this.cback.onSuccess(this, res);
	}

	protected void onError(Throwable err) throws Exception {
		this.cback.onError(this, err);
	}

	@Override
	public void onProcess(PIS pis, float rate) {
		this.cback.onProcess(this, pis, rate);
	}

	public void onCreateR(HResp res, Policy pc) throws Exception {
		this.cback.onCreateR(this, res, pc);
	}

	protected abstract String getMethod();

	protected abstract HttpClient createC() throws Exception;

	protected abstract HttpUriRequest createR() throws Exception;

	/**
	 * @return the args
	 */
	public List<NameValuePair> getArgs() {
		return args;
	}

	public List<PIS> getFiles() {
		return files;
	}

	/**
	 * @param args
	 *            the args to set
	 */
	public void setArgs(List<NameValuePair> args) {
		this.args = args;
	}

	/**
	 * @return the cencoding
	 */
	public String getCencoding() {
		return cencoding;
	}

	/**
	 * @param cencoding
	 *            the cencoding to set
	 */
	public void setCencoding(String cencoding) {
		this.cencoding = cencoding;
	}

	/**
	 * @return the sencoding
	 */
	public String getSencoding() {
		return sencoding;
	}

	/**
	 * @param sencoding
	 *            the sencoding to set
	 */
	public void setSencoding(String sencoding) {
		this.sencoding = sencoding;
	}

	/**
	 * @return the bsize
	 */
	public int getBsize() {
		return bsize;
	}

	/**
	 * @param bsize
	 *            the bsize to set
	 */
	public void setBsize(int bsize) {
		this.bsize = bsize;
	}

	/**
	 * @return the db
	 */
	public HDb getDb() {
		return db;
	}

	/**
	 * @param db
	 *            the db to set
	 */
	public void setDb(HDb db) {
		this.db = db;
	}

	/**
	 * @return the cback
	 */
	public HCallback getCback() {
		return cback;
	}

	/**
	 * @param cback
	 *            the cback to set
	 */
	public void setCback(HCallback cback) {
		this.cback = cback;
	}

	/**
	 * @return the headers
	 */
	public List<NameValuePair> getHeaders() {
		return headers;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @return the entity
	 */
	public HttpEntity getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	@Override
	public String toString() {
		return this.getMethod() + "->" + this.url + "?" + this.getQuery();
	}

}
