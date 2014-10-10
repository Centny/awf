package org.cny.amf.net.http;

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
public class HResp {
	private HttpResponse reponse;
	private long contentLength;
	private String contentType;
	private String encoding = "UTF-8";
	private int statusCode;
	private String filename;
	private Map<String, String> headers = new HashMap<String, String>();

	/**
	 * the constructor by HttpResponse.
	 * 
	 * @param reponse
	 *            the HttpResponse.
	 */
	public HResp(HttpResponse reponse) {
		this.init(reponse, "UTF-8");
	}

	/**
	 * the constructor by HttpResponse and encoding.
	 * 
	 * @param reponse
	 *            the HttpResponse.
	 * @param encoding
	 *            the encoding.
	 */
	public HResp(HttpResponse reponse, String encoding) {
		this.init(reponse, encoding);
	}

	private void init(HttpResponse reponse, String encoding) {
		if (reponse == null) {
			throw new RuntimeException("response is null");
		}
		if (encoding == null) {
			throw new RuntimeException("encoding is null");
		}
		this.reponse = reponse;
		this.encoding = encoding;
		this.statusCode = this.reponse.getStatusLine().getStatusCode();
		Header h;
		h = this.reponse.getFirstHeader("Content-Length");
		if (h == null) {
			this.contentLength = 0;
		} else {
			this.contentLength = Long.parseLong(h.getValue());
		}
		h = this.reponse.getFirstHeader("Content-Type");
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
		h = this.reponse.getFirstHeader("Content-Disposition");
		if (h == null) {
			this.filename = null;
		} else {
			HeaderElement he = h.getElements()[0];
			NameValuePair cnv = he.getParameterByName("filename");
			if (cnv != null) {
				this.filename = encoding(cnv.getValue());
			}
		}
		for (Header hd : this.reponse.getAllHeaders()) {
			String cval = encoding(hd.getValue());
			if (cval == null) {
				continue;
			}
			this.headers.put(hd.getName(), cval);
		}

	}

	private String encoding(String data) {
		try {
			return new String(data.getBytes("ISO-8859-1"), this.encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * Get HttpResponse.
	 * 
	 * @return the HttpResponse.
	 */
	public HttpResponse getReponse() {
		return reponse;
	}

	/**
	 * Get the content length.
	 * 
	 * @return the content length.
	 */
	public long getContentLength() {
		return contentLength;
	}

	/**
	 * Get the content type.
	 * 
	 * @return the content type.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Get the encoding.
	 * 
	 * @return the encoding.
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Get the status code.
	 * 
	 * @return the status code.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Get the file name.
	 * 
	 * @return the file name.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Get the header value by key.
	 * 
	 * @param key
	 *            the key.
	 * @return the header value.
	 */
	public String getValue(String key) {
		return this.headers.get(key);
	}

	/**
	 * Get the last modified time.
	 * 
	 * @return LMT
	 * @throws ParseException
	 */
	public long getLmt() {
		try {
			return parseLmt(this.getValue("Last-Modified"));
		} catch (Exception e) {
			return 0;
		}
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
