package org.cny.amf.net.http.hdb;

import java.io.File;
import java.util.Date;

import org.cny.amf.net.http.HClient;
import org.cny.amf.net.http.HResp;

import android.database.Cursor;

public class R {
	public long tid = 0;
	public String u = "";
	public String m = "";
	public String arg = "";
	public long lmt = 0;
	public String etag = "";
	public String type = "";
	public long len = 0;
	public String path = "";
	public long time = 0;

	public static final String COLS = "TID,U,M,ARG,LMT,ETAG,TYPE,LEN,PATH,TIME";

	public R() {

	}

	public R(Cursor c) {
		String[] ns = c.getColumnNames();
		for (int i = 0; i < ns.length; i++) {
			if ("TID".equals(ns[i])) {
				this.tid = c.getLong(i);
			} else if ("U".equals(ns[i])) {
				this.u = c.getString(i);
			} else if ("M".equals(ns[i])) {
				this.m = c.getString(i);
			} else if ("ARG".equals(ns[i])) {
				this.arg = c.getString(i);
			} else if ("LMT".equals(ns[i])) {
				this.lmt = c.getLong(i);
			} else if ("ETAG".equals(ns[i])) {
				this.etag = c.getString(i);
			} else if ("TYPE".equals(ns[i])) {
				this.type = c.getString(i);
			} else if ("LEN".equals(ns[i])) {
				this.len = c.getLong(i);
			} else if ("PATH".equals(ns[i])) {
				this.path = c.getString(i);
			} else if ("TIME".equals(ns[i])) {
				this.time = c.getLong(i);
			}
		}
	}

	public R(HClient c, HResp r) {
		this.u = c.uri();
		this.m = c.getRequest().getMethod();
		this.arg = c.query();
		this.update(c, r);
	}

	public void update(HClient c, HResp r) {
		this.lmt = r.getLmt();
		this.etag = r.getValue("ETag");
		this.type = r.getContentType();
		this.len = r.getContentLength();
		this.time = new Date().getTime();
	}

	public boolean CacheExist() {
		return new File(this.path).exists();
	}
}
