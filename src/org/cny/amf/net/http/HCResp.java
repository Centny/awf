package org.cny.amf.net.http;

import java.io.InputStream;

import android.database.Cursor;

public class HCResp extends HResp {
	private long tid = 0;
	private String u = "";
	private String m = "";
	private String arg = "";
	private String path = "";
	private long time = 0;
	private InputStream in = null;
	public static final String COLS = "TID,U,M,ARG,LMT,ETAG,TYPE,LEN,ENC,PATH,TIME";

	public HCResp(HClient c, HResp r) throws Exception {
		this.u = c.uri();
		this.m = c.getRequest().getMethod();
		this.arg = c.query();
		this.init(r.reponse, "UTF-8");
		this.in = r.getInput();
	}

	public HCResp(Cursor c) {
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
				this.setContentType(c.getString(i));
			} else if ("LEN".equals(ns[i])) {
				this.setContentLength(c.getLong(i));
			} else if ("ENC".equals(ns[i])) {
				this.setEncoding(c.getString(i));
			} else if ("PATH".equals(ns[i])) {
				this.path = c.getString(i);
			} else if ("TIME".equals(ns[i])) {
				this.time = c.getLong(i);
			}
		}
	}

	@Override
	public String toString() {
		return "u:" + this.u + ",m:" + this.m + ",arg:" + this.arg + ",lmt:"
				+ this.lmt + ",etag:" + this.etag + ",path:" + this.path;
	}

	@Override
	public InputStream getInput() throws Exception {
		return this.in;
	}

	public long getTid() {
		return tid;
	}

	public void setTid(long tid) {
		this.tid = tid;
	}

	public String getU() {
		return u;
	}

	public void setU(String u) {
		this.u = u;
	}

	public String getM() {
		return m;
	}

	public void setM(String m) {
		this.m = m;
	}

	public String getArg() {
		return arg;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public Object[] toObjects(boolean id_) {
		if (id_) {
			return new Object[] { this.tid > 0 ? this.tid : null, this.u,
					this.m, this.arg, this.lmt, this.etag, this.contentType,
					this.contentLength, this.encoding, this.path, this.time };
		} else {
			return new Object[] { this.u, this.m, this.arg, this.lmt,
					this.etag, this.contentType, this.contentLength,
					this.encoding, this.path, this.time };
		}
	}
}
