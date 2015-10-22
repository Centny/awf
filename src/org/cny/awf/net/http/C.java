package org.cny.awf.net.http;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;

public class C extends CBase {
	protected String method = "GET";
	protected HttpClient client;

	@Override
	protected String getMethod() {
		return this.method;
	}

	@Override
	protected HttpClient createC() throws Exception {
		return this.client;
	}

	@Override
	protected HttpUriRequest createR() throws Exception {
		if ("GET".equals(this.method)) {
			HttpGet get = new HttpGet(this.url + "?" + this.getQuery());
			for (NameValuePair nv : this.headers) {
				get.addHeader(nv.getName(), new String(
						nv.getValue().getBytes(), "ISO-8859-1"));
			}
			return get;
		} else if ("POST".equals(this.method)) {
			HttpPost post;
			if (this.entity != null) {
				post = new HttpPost(this.url + "?" + this.getQuery());
				post.setEntity(this.entity);
			} else if (this.files.isEmpty()) {
				post = new HttpPost(this.url);
				post.setEntity(new UrlEncodedFormEntity(this.args,
						this.cencoding));
			} else {
				post = new HttpPost(this.url + "?" + this.getQuery());
				MultipartEntityBuilder meb = MultipartEntityBuilder.create();
				for (PIS pis : this.files) {
					meb.addBinaryBody(pis.name, pis, pis.ct, pis.filename);
				}
				post.setEntity(meb.build());
			}
			for (NameValuePair nv : this.headers) {
				post.addHeader(nv.getName(), new String(nv.getValue()
						.getBytes(), "ISO-8859-1"));
			}
			return post;
		} else {
			return null;
		}
	}

	public C(Context ctx, String url, HCallback cback) {
		super(url, HDb.loadDb_(ctx), cback);
		this.initc(url);
	}

	public C(HDb db, String url, HCallback cback) {
		super(url, db, cback);
		this.initc(url);
	}

	protected void initc(String url) {
		try {
			if (url.trim().matches("^https\\:.*")) {
				this.client = newHttpsClient(80, httpsPort(url));
			} else {
				this.client = new DefaultHttpClient();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * create HTTPS HttpClient.
	 * 
	 * @param https_p
	 *            the HTTPS port.
	 * @return the HttpClient.
	 * @throws Exception
	 *             the error.
	 */
	public static HttpClient newHttpsClient(int http_p, int https_p)
			throws Exception {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null, null);
		SSLSocketFactory sf = new FullSSLSocketFactory(trustStore);
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", new PlainSocketFactory(), http_p));
		registry.register(new Scheme("https", sf, https_p));
		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
				registry);
		return new DefaultHttpClient(ccm, params);
	}

	/**
	 * The full access SSL socket factory.
	 * 
	 * @author cny
	 * 
	 */
	public static class FullSSLSocketFactory extends SSLSocketFactory {
		SSLContext ctx = SSLContext.getInstance("TLS");

		public FullSSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);
			ctx.init(null, new TrustManager[] { new FullX509TrustManager() },
					null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return ctx.getSocketFactory().createSocket(socket, host, port,
					autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return ctx.getSocketFactory().createSocket();
		}
	}

	/**
	 * The full X509TrustManager.
	 * 
	 * @author cny
	 * 
	 */
	public static class FullX509TrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	/**
	 * Get the HTTPS port.
	 * 
	 * @param url
	 *            target URL.
	 * @return the port,default is 443.
	 */
	public static int httpsPort(String url) {
		Pattern ptn = Pattern.compile("^http[s]?\\:\\/\\/[^\\:]*\\:[0-9]+");
		Matcher m = ptn.matcher(url);
		if (!m.find()) {
			return 443;
		}
		String pu = m.group();
		String sport = pu.substring(pu.lastIndexOf(":") + 1);
		return Integer.parseInt(sport);
	}
}
