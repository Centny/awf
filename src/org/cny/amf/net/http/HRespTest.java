package org.cny.amf.net.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cny.amf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class HRespTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	public HRespTest() {
		super(MainActivity.class);
	}

	private String ts_ip;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		InputStream in = this.getActivity().getAssets().open("ts_ip.dat");
		assertNotNull("the TServer ip config file is not found", in);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		this.ts_ip = reader.readLine();
		in.close();
		assertNotNull("the TServer ip is not found", this.ts_ip);
		assertFalse("the TServer ip is not found", this.ts_ip.isEmpty());
	}

	public void testNormal() throws Exception {
		HttpClient c = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://" + ts_ip
				+ ":8000/g_args?a=1&b=abc&c=这是中文");
		HResp resp = new HResp(c.execute(get));
		System.out.println(resp.getContentLength() + "");
		System.out.println(resp.getContentType() + "");
		System.out.println(resp.getEncoding() + "");
		System.out.println(resp.getFilename() + "");
		System.out.println(resp.getStatusCode() + "");
		System.out.println(resp.getValue("Content-Type") + "");
		System.out.println(resp.getReponse() + "");
	}

	public void testNormal2() throws Exception {
		HttpClient c = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://" + ts_ip
				+ ":8000/g_args?a=1&b=abc&c=这是中文");
		HttpResponse rp = c.execute(get);
		rp.removeHeaders("Content-Type");
		HResp resp = new HResp(rp);
		System.out.println(resp.getContentLength() + "");
		System.out.println(resp.getContentType() + "");
		System.out.println(resp.getEncoding() + "");
		System.out.println(resp.getFilename() + "");
		System.out.println(resp.getStatusCode() + "");
		System.out.println(resp.getValue("Content-Type") + "");
		System.out.println(resp.getReponse() + "");
	}

	public void testNormal3() throws Exception {
		HttpClient c = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://" + ts_ip
				+ ":8000/t_args?a=1&b=abc&c=这是中文");
		HttpResponse rp = c.execute(get);
		HResp resp = new HResp(rp, "kkk");
		System.out.println(resp.getContentLength() + "");
		System.out.println(resp.getContentType() + "");
		System.out.println(resp.getEncoding() + "");
		System.out.println(resp.getFilename() + "");
		System.out.println(resp.getStatusCode() + "");
		System.out.println(resp.getValue("Content-Type") + "");
		System.out.println(resp.getValue("ABB") + "");
		System.out.println(resp.getReponse() + "");
	}

	public void testNewErr() throws Exception {
		HttpClient c = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://" + ts_ip
				+ ":8000/g_args?a=1&b=abc&c=这是中文");
		try {
			new HResp(c.execute(get), null);
		} catch (RuntimeException e) {

		}
		try {
			new HResp(null, null);
		} catch (RuntimeException e) {

		}
	}
}
