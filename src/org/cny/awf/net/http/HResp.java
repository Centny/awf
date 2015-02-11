package org.cny.awf.net.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.cny.awf.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HResp {
	private static Logger L = LoggerFactory.getLogger(HResp.class);
	long tid;
	String path;
	long time;
	//
	String u;
	String m;
	String arg;
	//
	int code;
	long len;
	String type;
	String enc = "UTF-8";
	long lmt;
	String etag;
	//
	InputStream in = null;
	String filename;
	Map<String, String> headers = new HashMap<String, String>();

	//
	// public HResp init(Cursor c) {
	// String[] ns = c.getColumnNames();
	// for (int i = 0; i < ns.length; i++) {
	// if ("TID".equals(ns[i])) {
	// this.tid = c.getLong(i);
	// } else if ("U".equals(ns[i])) {
	// this.u = c.getString(i);
	// } else if ("M".equals(ns[i])) {
	// this.m = c.getString(i);
	// } else if ("ARG".equals(ns[i])) {
	// this.arg = c.getString(i);
	// } else if ("LMT".equals(ns[i])) {
	// this.lmt = c.getLong(i);
	// } else if ("ETAG".equals(ns[i])) {
	// this.etag = c.getString(i);
	// } else if ("TYPE".equals(ns[i])) {
	// this.type = c.getString(i);
	// } else if ("LEN".equals(ns[i])) {
	// this.len = c.getLong(i);
	// } else if ("ENC".equals(ns[i])) {
	// this.enc = c.getString(i);
	// } else if ("PATH".equals(ns[i])) {
	// this.path = c.getString(i);
	// } else if ("TIME".equals(ns[i])) {
	// this.time = c.getLong(i);
	// }
	// }
	// return this;
	// }

	public HResp init(CBase c) {
		this.u = c.getUrl();
		this.m = c.getMethod();
		this.arg = c.getQuery();
		return this;
	}

	public HResp init(HttpResponse res, String encoding) {
		if (encoding == null) {
			throw new RuntimeException("encoding is null");
		}
		if (res == null) {
			throw new RuntimeException("response is null");
		}
		this.enc = encoding;
		this.code = res.getStatusLine().getStatusCode();
		Header h;
		h = res.getFirstHeader("Content-Length");
		if (h == null) {
			this.len = 0;
		} else {
			this.len = Long.parseLong(h.getValue());
		}
		h = res.getFirstHeader("Content-Type");
		if (h == null) {
			this.type = null;
		} else {
			HeaderElement he = h.getElements()[0];
			this.type = he.getName();
			NameValuePair cnv = he.getParameterByName("charset");
			if (cnv != null) {
				this.enc = cnv.getValue();
			}

		}
		h = res.getFirstHeader("ETag");
		if (h == null) {
			this.etag = null;
		} else {
			this.etag = h.getValue();
		}
		h = res.getFirstHeader("Last-Modified");
		try {
			this.lmt = parseLmt(h.getValue());
		} catch (Exception e) {
			this.lmt = 0;
		}
		h = res.getFirstHeader("Content-Disposition");
		if (h == null) {
			this.filename = null;
		} else {
			HeaderElement he = h.getElements()[0];
			NameValuePair cnv = he.getParameterByName("filename");
			if (cnv != null) {
				this.filename = encoding(cnv.getValue());
			}
		}

		for (Header hd : res.getAllHeaders()) {
			String cval = encoding(hd.getValue());
			if (cval == null) {
				continue;
			}
			this.headers.put(hd.getName(), cval);
		}
		return this;
	}

	public HResp initHttpStream(HttpResponse res) throws IllegalStateException,
			IOException {
		this.in = res.getEntity().getContent();
		return this;
	}

	public HResp initFileStream(HDb db) throws FileNotFoundException {
		File cf = db.openCacheF(this.path);
		this.in = new FileInputStream(cf);
		return this;
	}

	public HResp initPathStream(HDb db) throws FileNotFoundException {
		File cf = db.openCacheF(this.path);
		this.in = new ByteArrayInputStream(cf.getAbsolutePath().getBytes());
		return this;
	}

	public String readCache(HDb db) throws Exception {
		return Util.readAll(db.openCacheF(this.path));
	}

	public void close() {
		try {
			// if (this.in != null) {
			this.in.close();
			// }
		} catch (Exception e) {
			L.warn("closing stream error:", e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "u:" + this.u + ",m:" + this.m + ",arg:" + this.arg + ",lmt:"
				+ this.lmt + ",etag:" + this.etag + ",path:" + this.path;
	}

	public Object[] toObjects(boolean id_) {
		if (id_) {
			return new Object[] { this.tid > 0 ? this.tid : null, this.u,
					this.m, this.arg, this.lmt, this.etag, this.type, this.len,
					this.enc, this.path, this.time };
		} else {
			return new Object[] { this.u, this.m, this.arg, this.lmt,
					this.etag, this.type, this.len, this.enc, this.path,
					this.time };
		}
	}

	protected String encoding(String data) {
		try {
			return new String(data.getBytes("ISO-8859-1"), this.enc);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @return the in.
	 */
	public InputStream getIn() {
		return in;
	}

	/**
	 * @return the tid
	 */
	public long getTid() {
		return tid;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return the u
	 */
	public String getU() {
		return u;
	}

	/**
	 * @return the m
	 */
	public String getM() {
		return m;
	}

	/**
	 * @return the arg
	 */
	public String getArg() {
		return arg;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the len
	 */
	public long getLen() {
		return len;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the enc
	 */
	public String getEnc() {
		return enc;
	}

	/**
	 * @return the lmt
	 */
	public long getLmt() {
		return lmt;
	}

	/**
	 * @return the etag
	 */
	public String getEtag() {
		return etag;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	public void setTid(long tid) {
		this.tid = tid;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setU(String u) {
		this.u = u;
	}

	public void setM(String m) {
		this.m = m;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}

	public void setLen(long len) {
		this.len = len;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setEnc(String enc) {
		this.enc = enc;
	}

	public void setLmt(long lmt) {
		this.lmt = lmt;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	public static long parseLmt(String gmt) throws ParseException {
		if (gmt == null) {
			return 0;
		}
		gmt = gmt.trim();
		if (gmt.isEmpty()) {
			return 0;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.parse(gmt).getTime();
	}

	public static String formatLmt(Date gmt) throws ParseException {
		if (gmt == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(gmt);
	}
}
