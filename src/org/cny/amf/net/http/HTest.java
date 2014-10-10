package org.cny.amf.net.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.message.BasicNameValuePair;
import org.cny.amf.net.http.HCallback.HCacheCallback;
import org.cny.amf.test.MainActivity;

import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;

public class HTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public HTest() {
		super(MainActivity.class);
	}

	File dl;
	private Throwable rerr = null;
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
		File ext = Environment.getExternalStorageDirectory();
		this.dl = new File(ext, "dl");
		if (!this.dl.exists()) {
			this.dl.mkdirs();
		}
		H.ATY = this.getActivity();
		HDb.loadDb(this.getActivity()).clearR();
	}

	@Override
	protected void tearDown() throws Exception {
		HDb.free();
	}

	public class Abc extends HCacheCallback {
		CountDownLatch cdl;
		String name;

		public Abc(CountDownLatch cdl, String name) {
			super();
			this.cdl = cdl;
			this.name = name;
		}

		@Override
		public void onError(CBase c, String cache, Throwable err)
				throws Exception {
			cdl.countDown();
			rerr = err;
			err.printStackTrace();
		}

		@Override
		public void onSuccess(CBase c, HResp res, String data)
				throws Exception {

			cdl.countDown();
			System.out.println(res.getEnc());
			if (!"OK".equals(data)) {
				String msg = "response:" + data + "," + this.name;
				if (rerr != null) {
					msg = rerr.getMessage() + ",," + msg;
				}
				rerr = new Exception(msg);
				return;
			}
		}
	};

	public class Abc2 extends Abc {

		public Abc2(CountDownLatch cdl, String name) {
			super(cdl, name);
		}

		@Override
		public void onSuccess(CBase c, HResp res, String data)
				throws Exception {
			cdl.countDown();
			System.out.println(data + "--------->\n\n\n");
		}
	}

	public void testC() throws InterruptedException {
		this.rerr = null;
		final CountDownLatch cdl = new CountDownLatch(17);

		// 1
		H.doGet("http://" + ts_ip + ":8000/g_args?a=1&b=abc&c=这是中文", new Abc(
				cdl, "1"));
		// 2
		List<BasicNameValuePair> args = new ArrayList<BasicNameValuePair>();
		args.add(new BasicNameValuePair("a", "1"));
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文", args, new Abc(
				cdl, "2"));
		// 2-1
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=N", args,
				new Abc(cdl, "2"));
		// 2-2
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=C", args,
				new Abc(cdl, "2"));
		// 2-3
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=CN", args,
				new Abc(cdl, "2"));
		// 2-4
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=CN", args,
				new Abc(cdl, "2"));
		// 2-5
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=NO", args,
				new Abc(cdl, "2"));
		// 2-6
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=NO", args,
				new Abc(cdl, "2"));
		// 2-7
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=I", args,
				new Abc2(cdl, "2"));
		// 2-8
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=I", args,
				new Abc2(cdl, "2"));
		// 3
		args.add(new BasicNameValuePair("b", "abc"));
		H.doGet("http://" + ts_ip + ":8000/g_args?c=这是中文", args, new Abc(cdl,
				"3"));
		// 4
		args.add(new BasicNameValuePair("c", "这是中文"));
		H.doGet("http://" + ts_ip + ":8000/g_args", args, new Abc(cdl, "4"));
		// 5
		H.doPost("http://" + ts_ip + ":8000/g_args?c=这是中文", args, new Abc(cdl,
				"5"));
		//
		HAsyncTask dc;
		// 6
		dc = new HAsyncTask(this.getActivity(), "http://" + ts_ip
				+ ":8000/g_args", new Abc(cdl, "6"));
		dc.addArg("a", "1");
		dc.addArg("b", "abc");
		dc.addArg("c", "这是中文");
		dc.setMethod("GET");
		dc.asyncExec();
		// 7
		dc = new HAsyncTask(this.getActivity(), "http://" + ts_ip
				+ ":8000/h_args", new Abc(cdl, "7"));
		dc.getHeaders().addAll(args);
		dc.setMethod("GET");
		dc.asyncExec();
		// 8
		dc = new HAsyncTask(this.getActivity(), "http://" + ts_ip
				+ ":8000/h_args", new Abc(cdl, "8"));
		dc.getHeaders().addAll(args);
		dc.setMethod("POST");
		assertEquals("POST", dc.getMethod());
		dc.setCencoding("UTF-8");
		dc.asyncExec();
		// 9
		dc = new HAsyncTask(this.getActivity(), "http://" + ts_ip
				+ ":8000/h_args", new Abc(cdl, "9"));
		dc.addHead("a", "1");
		dc.addHead("b", "abc");
		dc.addHead("c", "这是中文");
		dc.setMethod("POST");
		dc.setCencoding("UTF-8");
		dc.asyncExec();
		cdl.await();
		if (this.rerr != null) {
			assertNull(this.rerr.getMessage(), this.rerr);
		}
	}
}
