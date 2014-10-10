package org.cny.amf.net.http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

/**
 * the external class for HTTP response.
 * 
 * @author cny
 * 
 */
public abstract class HResp {
	HttpResponse reponse;
	long contentLength;
	String contentType;
	String encoding = "UTF-8";
	int statusCode;
	String filename;
	long lmt;
	String etag;
	Map<String, String> headers = new HashMap<String, String>();

	public HResp() {

	}

	protected String encoding(String data) {
		try {
			return new String(data.getBytes("ISO-8859-1"), this.encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getValue(String key) {
		return this.headers.get(key);
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

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public long getLmt() {
		return lmt;
	}

	public void setLmt(long lmt) {
		this.lmt = lmt;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getFilename() {
		return filename;
	}

	public HttpResponse getReponse() {
		return reponse;
	}

	public void init(HttpResponse reponse, String encoding) {
		if (reponse == null) {
			throw new RuntimeException("response is null");
		}
		if (encoding == null) {
			throw new RuntimeException("encoding is null");
		}
		this.reponse = reponse;
		this.encoding = encoding;
		this.statusCode = reponse.getStatusLine().getStatusCode();
		Header h;
		h = reponse.getFirstHeader("Content-Length");
		if (h == null) {
			this.contentLength = 0;
		} else {
			this.contentLength = Long.parseLong(h.getValue());
		}
		h = reponse.getFirstHeader("Content-Type");
		if (h == null) {
			this.contentType = null;
		} else {
			HeaderElement he = h.getElements()[0];
			this.contentType = he.getName();
			NameValuePair cnv = he.getParameterByName("charset");
			if (cnv != null) {
				this.encoding = cnv.getValue();
			}

		}
		h = reponse.getFirstHeader("ETag");
		if (h == null) {
			this.etag = null;
		} else {
			this.etag = h.getValue();
		}
		h = reponse.getFirstHeader("Last-Modified");
		try {
			this.lmt = parseLmt(h.getValue());
		} catch (Exception e) {
			this.lmt = 0;
		}
		h = reponse.getFirstHeader("Content-Disposition");
		if (h == null) {
			this.filename = null;
		} else {
			HeaderElement he = h.getElements()[0];
			NameValuePair cnv = he.getParameterByName("filename");
			if (cnv != null) {
				this.filename = encoding(cnv.getValue());
			}
		}

		for (Header hd : reponse.getAllHeaders()) {
			String cval = encoding(hd.getValue());
			if (cval == null) {
				continue;
			}
			this.headers.put(hd.getName(), cval);
		}

	}

	public abstract InputStream getInput() throws Exception;

	// //
}
