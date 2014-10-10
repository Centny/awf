package org.cny.amf.net.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.cny.amf.util.MultiOutputStream;
import org.cny.amf.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;

public abstract class HCache extends HClientM {
	public static enum Policy {
		C, // cache only.
		N, // normal HTTP cache.
		I, // the image cache.
	}

	HDb db;
	Policy policy;
	private static Logger log = LoggerFactory.getLogger(HCache.class);

	public HCache(HDb db, String url, HCallback cback) {
		super(url, cback);
		this.db = db;
	}

	public HCache(Activity aty, String url, HCallback cback) throws IOException {
		super(url, cback);
		this.db = HDb.loadDb(aty);
	}

	public Policy parsePolicy() {
		String hc = this.findParameter("_hc_");
		if (hc == null) {
			return Policy.N;
		}
		try {
			return Policy.valueOf(hc);
		} catch (Exception e) {
			return Policy.N;
		}
	}

	public String query() {
		return this.query("_hc_");
	}

	public HCResp findR() {
		return this.db.find(this.uri(), this.method(), this.query());
	}

	@Override
	public HResp doRequest(HttpClient hc, HttpUriRequest uri) throws Exception {
		this.policy = this.parsePolicy();
		HCResp creq = this.findR();
		if (creq == null || !this.db.CacheExist(creq)) {

			if (this.policy == Policy.C) {// cache only policy.
				throw new HCacheException(this.uri(), this.method(),
						this.query());
			} else {// image policy.
				log.info(
						"onRequest by uri({}),method({}),query({}),policy({}),cache({})",
						this.uri(), this.method(), this.query(), this.policy,
						"not found");
				// return null(network).
				return new HCResp(this, super.doRequest(hc, uri));
			}
		} else {
			switch (this.policy) {
			case C:// cache only policy.
					// open file.
				log.info(
						"onRequest by uri({}),method({}),query({}),policy({}),cache({})",
						this.uri(), this.method(), this.query(), this.policy,
						true);
				creq.setIn(new FileInputStream(this.db.openCacheF(creq
						.getPath())));
				return creq;
			case I:// image policy.
					// return image file path.
				log.info(
						"onRequest by uri({}),method({}),query({}),policy({}),cache({})",
						this.uri(), this.method(), this.query(), this.policy,
						true);
				creq.setIn(new ByteArrayInputStream(this.db
						.openCacheF(creq.getPath()).getAbsolutePath()
						.getBytes()));
				return creq;
			default:
				if (creq.getLmt() > 0) {
					uri.addHeader("If-Modified-Since",
							HResp.formatLmt(new Date(creq.getLmt())));
				}
				if (Util.isNullOrEmpty(creq.getEtag())) {
					uri.addHeader("If-None-Match", creq.getEtag());
				}
				log.info(
						"onRequest by uri({}),method({}),query({}),policy({}),cache({})",
						this.uri(), this.method(), this.query(), this.policy,
						"Network");
				HResp res = super.doRequest(hc, uri);
				if (res.getStatusCode() == 304) {
					creq.setIn(new FileInputStream(this.db.openCacheF(creq
							.getPath())));
					creq.setStatusCode(304);
					return creq;
				}else{
					creq.init(reponse, encoding)
				}
			}
		}
	}

	// @Override
	// public InputStream onResponse(HClient c, HResp r) throws Exception {
	// if (r.getStatusCode() == 304) {
	// // open file.
	// log.info(
	// "onResponse({}) by uri({}),method({}),query({}),policy({}),cache({})",
	// r.getStatusCode(), c.uri(), c.method(), this.query(c),
	// this.policy, "304");
	// return new FileInputStream(this.db.openCacheF(this.req.path));
	// }
	// if (this.req == null) {
	// this.req = new R(c, r);
	// log.info(
	// "onResponse({}) by uri({}),method({}),query({}),policy({}),cache({})",
	// r.getStatusCode(), c.uri(), c.method(), this.query(c),
	// this.policy, "N");
	// } else {
	// log.info(
	// "onResponse({}) by uri({}),method({}),query({}),policy({}),cache({})",
	// r.getStatusCode(), c.uri(), c.method(), this.query(c),
	// this.policy, "U");
	// this.req.update(c, r);
	// }
	// return null;
	// }

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
	public void onEnd(HClient c, OutputStream out) {
		try {
			if (out != null) {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (this.req == null) {
			return;
		}
		this.req.setTime(new Date().getTime());
		if (this.req.getTid() > 0) {
			this.db.update(this.req);
			log.info("update cache {}", this.req);
		} else {
			this.db.add(this.req);
			log.info("add cache {}", this.req);
		}
	}

	@Override
	public void onError(HClient c, Throwable err) {
		if (this.req == null || !this.db.CacheExist(this.req)) {
			log.info(
					"onError({}) by uri({}),method({}),query({}),policy({}),cache({})",
					err.getMessage(), c.uri(), c.method(), this.query(),
					this.policy, "not found");
			this.onError(c, null, err);
			return;
		}
		File cf = this.db.openCacheF(this.req.path);
		log.info(
				"onError({}) by uri({}),method({}),query({}),policy({}),cache({})",
				err.getMessage(), c.uri(), c.method(), this.query(),
				this.policy, true);
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
			log.error("read cache file error({})", e.getMessage());
			return "";
		}
	}

	public abstract void onError(HClient c, String cache, Throwable err);
}
