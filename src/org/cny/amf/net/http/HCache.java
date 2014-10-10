package org.cny.amf.net.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.http.client.methods.HttpUriRequest;
import org.cny.amf.net.http.HCallback.HMCallback;
import org.cny.amf.net.http.hdb.HDb;
import org.cny.amf.net.http.hdb.R;
import org.cny.amf.util.MultiOutputStream;
import org.cny.amf.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HCache extends HMCallback {
	public static enum Policy {
		C, // cache only.
		N, // normal HTTP cache.
		I, // the image cache.
	}

	HDb db;
	Policy policy;
	R req;
	private static Logger log = LoggerFactory.getLogger(HCache.class);

	public HCache(HDb db) {
		this.db = db;
	}

	public static Policy parsePolicy(HttpUriRequest r) {
		Object hc = r.getParams().getParameter("_hc_");
		if (hc == null) {
			return Policy.N;
		}
		try {
			return Policy.valueOf(hc.toString());
		} catch (Exception e) {
			return Policy.N;
		}
	}

	@Override
	public InputStream onRequest(HClient c, HttpUriRequest r) throws Exception {
		this.policy = parsePolicy(r);
		this.req = this.db.find(c.uri(), c.method(), c.query());
		log.debug(
				"onRequest by uri(%s),method(%s),query(%s),policy(%s),cache(%s)",
				c.uri(), c.method(), c.query(), this.policy,
				(this.req != null && this.req.CacheExist()));
		if (this.req == null || !this.req.CacheExist()) {// cache not found.
			if (this.policy == Policy.C) {// cache only policy.
				throw new HCacheException(c.uri(), c.method(), c.query());
			} else {// image policy.
				// return null(network).
				return null;
			}
		} else {
			switch (this.policy) {
			case C:// cache only policy.
					// open file.
				return new FileInputStream(this.db.openCacheF(this.req.path));
			case I:// image policy.
					// return image file path.
				return new ByteArrayInputStream(this.db
						.openCacheF(this.req.path).getAbsolutePath().getBytes());
			default:
				if (req.lmt > 0) {
					r.addHeader("If-Modified-Since",
							HResp.formatLmt(new Date(req.lmt)));
				}
				if (req.etag.length() > 0) {
					r.addHeader("If-None-Match", req.etag);
				}
				return null;
			}
		}

	}

	@Override
	public InputStream onResponse(HClient c, HResp r) throws Exception {
		log.debug(
				"onResponse(%s) by uri(%s),method(%s),query(%s),policy(%s),cache(%s)",
				r.getStatusCode(), c.uri(), c.method(), c.query(), this.policy,
				(this.req != null && this.req.CacheExist()));
		if (r.getStatusCode() == 304) {
			// open file.
			return new FileInputStream(this.db.openCacheF(this.req.path));
		}
		if (this.req == null) {
			this.req = new R(c, r);
		} else {
			this.req.update(c, r);
		}
		return null;
	}

	@Override
	public OutputStream onBebin(HClient c, HResp r) throws Exception {
		OutputStream bos = super.onBebin(c, r);
		if (r.getStatusCode() == 304) {
			return bos;
		} else {
			File cf = this.db.newCacheF();
			this.req.path = cf.getName();
			return new MultiOutputStream(bos, new FileOutputStream(cf));
		}
	}

	@Override
	public void onEnd(HClient c, InputStream in, OutputStream out) {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.req.time = new Date().getTime();
		if (this.req.tid > 0) {
			this.db.update(this.req);
		} else {
			this.db.add(this.req);
		}
	}

	@Override
	public void onError(HClient c, Throwable err) {
		log.debug(
				"onError(%s) by uri(%s),method(%s),query(%s),policy(%s),cache(%s)",
				err.getMessage(), c.uri(), c.method(), c.query(), this.policy,
				(this.req != null && this.req.CacheExist()));
		if (this.req == null || !this.req.CacheExist()) {
			this.onError(c, null, err);
			return;
		}
		File cf = this.db.openCacheF(this.req.path);
		if (this.policy == Policy.I) {
			this.onError(c, cf.getAbsolutePath(), err);
		} else {
			this.onError(c, this.readAll(cf), err);
		}
	}

	private String readAll(File cf) {
		try {
			return Util.readAll(cf);
		} catch (Exception e) {
			log.error("read cache file error(%s)", e.getMessage());
			return "";
		}
	}

	public abstract void onError(HClient c, String cache, Throwable err);
}
