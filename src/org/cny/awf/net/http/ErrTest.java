package org.cny.awf.net.http;

import java.security.KeyStore;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.message.BasicHttpResponse;
import org.cny.awf.net.http.C.FullSSLSocketFactory;
import org.cny.awf.net.http.C.FullX509TrustManager;
import org.cny.awf.test.MainActivity;
import org.cny.jwf.util.Utils;

import android.os.Message;
import android.test.ActivityInstrumentationTestCase2;

public class ErrTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public ErrTest() {
		super(MainActivity.class);
	}

	public void testHAsyncTask() throws InterruptedException {
		new HAsyncTask(this.getActivity(), "ssd", null) {

			@Override
			public void run() {
				throw new RuntimeException();
			}

		}.asyncExec();
		Thread.sleep(1000);
	}

	public void testHResp() {
		Utils.oinfo(new HResp());
		new HResp().toString();
		new HResp().encoding(null);
		new HResp().close();
		HResp re = new HResp();
		re.toObjects(true);
		re.tid = 1;
		re.toObjects(true);
		try {
			new HResp().init(null, (String) null);
		} catch (Exception e) {

		}
		try {
			new HResp().init(null, "sfsfs");
		} catch (Exception e) {

		}
		try {
			HResp.formatLmt(null);
		} catch (Exception e) {

		}
		try {
			HResp.parseLmt(null);
		} catch (Exception e) {

		}
		try {
			HResp.parseLmt("");
		} catch (Exception e) {

		}

		try {

			new HResp().init(new BasicHttpResponse(new StatusLine() {

				@Override
				public int getStatusCode() {
					return 200;
				}

				@Override
				public String getReasonPhrase() {
					return "abc";
				}

				@Override
				public ProtocolVersion getProtocolVersion() {
					return new ProtocolVersion("http", 1, 1);
				}
			}) {

				@Override
				public Header[] getAllHeaders() {
					return new Header[] { new Header() {

						@Override
						public String getValue() {
							return null;
						}

						@Override
						public String getName() {
							return null;
						}

						@Override
						public HeaderElement[] getElements()
								throws ParseException {
							return null;
						}
					} };
				}

			}, "UTF-8");
		} catch (Exception e) {

		}
	}

	public void testC() throws Exception {
		C c = new C(this.getActivity(), "", null);
		c.setMethod("sds");
		c.createR();
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null, null);
		SSLSocketFactory sf = new FullSSLSocketFactory(trustStore);
		sf.createSocket();
		FullX509TrustManager fxtm = new FullX509TrustManager();
		fxtm.checkClientTrusted(null, null);
		fxtm.checkServerTrusted(null, null);
		fxtm.getAcceptedIssuers();
		try {
			new C(this.getActivity(), "sfs", null).initc(null);
		} catch (Exception e) {

		}
	}

	public void testHandler() throws Throwable {
		this.runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				Message m = new Message();
				m.what = -1;
				m.obj = new Object[] { null, null };
				HCallback.HandlerCallback.H.dispatchMessage(m);
				Message msg = new Message();
				msg.what = -100;
				HCallback.HandlerCallback.H.dispatchMessage(msg);
			}
		});
	}
}
