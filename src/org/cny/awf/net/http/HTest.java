package org.cny.awf.net.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.cny.awf.net.http.HCallback.HCacheCallback;
import org.cny.awf.net.http.dlm.DlmC;
import org.cny.awf.net.http.dlm.DlmCallback;
import org.cny.awf.test.MainActivity;
import org.cny.awf.test.R;
import org.cny.awf.util.CDL;
import org.cny.awf.util.MultiOutputStream;
import org.cny.jwf.util.FUtil;
import org.cny.jwf.util.FUtil.Hash;
import org.cny.jwf.util.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import junit.framework.Assert;

public class HTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public HTest() {
		super(MainActivity.class);
	}

	File dl;
	private Throwable rerr = null;
	private String ts_ip;

	@Override
	protected void setUp() throws Exception {
		try {
			this.runTestOnUiThread(new Runnable() {

				@Override
				public void run() {
					new HCallback.HandlerCallback(null);// for register handler.

				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("-------------->");
		System.out.println("-------------->");
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
		H.CTX = this.getActivity();
		HDb.loadDb_(this.getActivity()).clearR();
		System.out.println("test ip:" + ts_ip);
		System.out.println("test ip:" + ts_ip);
		System.out.println("test ip:" + ts_ip);
		System.out.println("test ip:" + ts_ip);
	}

	@Override
	protected void tearDown() throws Exception {
		HDb.free();
		super.tearDown();
	}

	public class None extends HCacheCallback {
		CountDownLatch cdl;
		String name;

		public None(CountDownLatch cdl, String name) {
			super();
			this.cdl = cdl;
			this.name = name;
		}

		@Override
		public void onError(CBase c, String cache, Throwable err) throws Exception {
			this.cdl.countDown();
		}

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			this.cdl.countDown();
		}

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
		public void onError(CBase c, String cache, Throwable err) throws Exception {
			cdl.countDown();
			rerr = err;
			err.printStackTrace();
		}

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {

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
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			cdl.countDown();
			System.out.println(c.readCache() + "--------->\n\n\n");
		}
	}

	public static class Jab {
		public int i;
		public String s;
	}

	public class TJson extends HCallback.GDataCallback<Jab> {

		CountDownLatch cdl;
		String name;

		public TJson(CountDownLatch cdl, String name) {
			super(Jab.class);
			this.cdl = cdl;
			this.name = name;
		}

		@Override
		public void onError(CBase c, Throwable err) throws Exception {
			cdl.countDown();
		}

		@Override
		public void onSuccess(CBase c, HResp res, Jab data) throws Exception {
			cdl.countDown();
		}

	}

	public class TJson2 extends HCallback.GCacheCallback<Jab> {

		CountDownLatch cdl;
		String name;

		public TJson2(CountDownLatch cdl, String name) {
			super(Jab.class);
			this.cdl = cdl;
			this.name = name;
		}

		@Override
		public void onSuccess(CBase c, HResp res, Jab data) throws Exception {
			cdl.countDown();
		}

		@Override
		public void onError(CBase c, Jab cache, Throwable err) throws Exception {
			cdl.countDown();
		}

	}

	public void testC() throws Throwable {
		this.runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				new HCallback.HandlerCallback(null);// for register handler.

			}
		});
		callC();
	}

	public void callC() throws Exception {
		System.out.println("test ip:" + ts_ip);
		System.out.println("test ip:" + ts_ip);
		System.out.println("test ip:" + ts_ip);
		System.out.println("test ip:" + ts_ip);
		this.rerr = null;
		final CDL cdl = new CDL(35);

		// 1
		H.doGet("http://" + ts_ip + ":8000/g_args?a=1&b=abc&c=这是中文", new Abc(cdl, "1"));
		// 1-1
		H.doGet("http://" + ts_ip + ":8000/g_args?param={\"pa\":{\"pn\":1,\"ps\":5},\"filter\":\"\"}&a=1&b=abc&c=这是中文",
				new Abc(cdl, "1-1"));
		// 1-2
		H.doGet("http://" + ts_ip + ":8000/g_args?", new None(cdl, "1-2"));
		// 1-3
		H.doGet("http://" + ts_ip + ":8000/g_args", new None(cdl, "1-3"));
		// 1-4
		H.doGet("http://" + ts_ip + ":8000/g_args?a=", new None(cdl, "1-4"));
		// 2
		List<BasicNameValuePair> args = new ArrayList<BasicNameValuePair>();
		args.add(new BasicNameValuePair("a", "1"));
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文", args, new Abc(cdl, "2"));
		// 2-1
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=N", args, new Abc(cdl, "2-1"));
		// 2-2
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=C", args, new Abc(cdl, "2-2"));
		// 2-3
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=CN", args, new Abc(cdl, "2"));
		// 2-4
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=CN", args, new Abc(cdl, "2"));
		// 2-5
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=NO", args, new Abc(cdl, "2"));
		// 2-6
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=NO", args, new Abc(cdl, "2"));
		// 2-7
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=I", args, new Abc2(cdl, "2"));
		// 2-8
		H.doGet("http://" + ts_ip + ":8000/g_args?b=abc&c=这是中文&_hc_=I", args, new Abc2(cdl, "2"));
		// 2-9
		H.doGet("http://" + ts_ip + ":8000/g_argsc?_hc_=N", args, new Abc(cdl, "2-9"));
		// 2-10
		H.doGet("http://" + ts_ip + ":8000/g_argsc?_hc_=N", args, new Abc(cdl, "2-10"));
		// 2-11
		H.doGet("http://" + ts_ip + ":8000/g_argsc?_hc_=I", args, new Abc2(cdl, "2-11"));
		// 2-12
		H.doGet("http://" + ts_ip + ":8000/g_argsc?ssdfs=sfs&sfs=1&_hc_=C", args, new None(cdl, "2-12"));
		// 3
		args.add(new BasicNameValuePair("b", "abc"));
		H.doGet("http://" + ts_ip + ":8000/g_args?c=这是中文", args, new Abc(cdl, "3"));
		// 3-1
		H.doGet(this.getActivity(), "http://" + ts_ip + ":8000/g_args?c=这是中文", args, new None(cdl, "3"));
		// 3-2
		H.doGet("http://" + ts_ip + ":8000/g_args?c=这是中文", new None(cdl, "3-2"));
		// 3-3
		H.doGet(this.getActivity(), "http://" + ts_ip + ":8000/g_args?c=这是中文", new None(cdl, "3-3"));
		// 4
		args.add(new BasicNameValuePair("c", "这是中文"));
		H.doGet("http://" + ts_ip + ":8000/g_args", args, new Abc(cdl, "4"));
		// 5
		H.doPost("http://" + ts_ip + ":8000/g_args?c=这是中文", args, new Abc(cdl, "5"));
		// 5-1
		H.doPost(this.getActivity(), "http://" + ts_ip + ":8000/g_args?c=这是中文", new None(cdl, "5-1"));
		// 5-2
		H.doPost("http://" + ts_ip + ":8000/g_args?c=这是中文", new None(cdl, "5-2"));
		//
		HAsyncTask dc;
		// 6
		dc = new HAsyncTask(this.getActivity(), "http://" + ts_ip + ":8000/g_args", new Abc(cdl, "6"));
		dc.addArg("a", "1");
		dc.addArg("b", "abc");
		dc.addArg("c", "这是中文");
		dc.addArg("c", null);
		dc.addArg(null, "这是中文");
		dc.setMethod("GET");
		dc.asyncExec();
		// 7
		dc = new HAsyncTask(this.getActivity(), "http://" + ts_ip + ":8000/h_args", new Abc(cdl, "7"));
		dc.getHeaders().addAll(args);
		Assert.assertEquals("这是中文", dc.findHead("c"));
		Assert.assertEquals("", dc.findHead("csfdsfs"));
		dc.setMethod("GET");
		dc.asyncExec();
		// 8
		dc = new HAsyncTask(this.getActivity(), "http://" + ts_ip + ":8000/h_args", new Abc(cdl, "8"));
		dc.getHeaders().addAll(args);
		dc.setMethod("POST");
		assertEquals("POST", dc.getMethod());
		dc.setCencoding("UTF-8");
		dc.asyncExec();
		// 9
		dc = new HAsyncTask(this.getActivity(), "http://" + ts_ip + ":8000/h_args", new Abc(cdl, "9"));
		dc.addHead("a", "1");
		dc.addHead("b", "abc");
		dc.addHead("c", "这是中文");
		dc.addHead("c", null);
		dc.addHead(null, "这是中文");
		dc.setMethod("POST");
		dc.setCencoding("UTF-8");
		dc.asyncExec();
		// 10
		H.doGet("http://" + ts_ip + ":8000/dl?sw=1", args, new None(cdl, "10"));
		// 11
		H.doGet("https://www.baidu.com", args, new None(cdl, "11"));
		// 11-1
		H.doGet("https://www.baidu.com:443", args, new None(cdl, "11-1"));
		// 12
		H.doGet("http://" + ts_ip + ":8000/res_j", args, new TJson(cdl, "12"));
		// 12-1
		H.doGet("http://" + ts_ip + ":8000/res_j", args, new TJson2(cdl, "12-1"));
		//
		cdl.await();
		if (this.rerr != null) {
			this.rerr.printStackTrace();
			assertNull(this.rerr.getMessage(), this.rerr);
		}
		String res = H.findCache("http://" + ts_ip + ":8000/g_args?a=1&b=abc&c=这是中文");
		assertNotNull(res);
		new H();
	}

	public void testErr() throws URISyntaxException {
		final CountDownLatch cdl = new CountDownLatch(17);
		try {
			new HAsyncTask(HDb.loadDb_(getActivity()), new String(new byte[] { 1, 2, 3 }), new Abc(cdl, "9"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		new HAsyncTask(HDb.loadDb_(getActivity()), "http://www.baidu.com", new Abc(cdl, "9"));
		new C(this.getActivity(), "", null) {

			@Override
			protected void exec() throws Exception {
				throw new Exception();
			}

		}.run();
		new C(this.getActivity(), "", null) {

		}.readCache();
		new C(this.getActivity(), "", null) {

			@Override
			protected HResp find(Policy pc) {
				throw new RuntimeException();
			}

		}.readCache();
		//

		new HAsyncTask(HDb.loadDb_(getActivity()), "http://www.baidu.com", new Abc(cdl, "9")) {

			@Override
			protected void onProcess(HResp res, long rsize, long clen) {
				super.onProcess(res, rsize, clen);
				this.running = false;
			}

		}.run();
		//
		HAsyncTask at = new HAsyncTask(HDb.loadDb_(getActivity()), "http://www.baidu.com", new None(cdl, ""));
		at.setArgs(at.getArgs());
		at.setBsize(at.getBsize());
		at.setCback(at.getCback());
		at.setCencoding(at.getCencoding());
		at.setDb(at.getDb());
		at.setMethod(at.getMethod());
		at.setSencoding(at.getSencoding());
		at.setUrl(at.getUrl());
		at.isRunning();

		//
		CBase cb = new CBase("", HDb.loadDb_(getActivity()), new None(cdl, "")) {

			@Override
			protected String getMethod() {
				return null;
			}

			@Override
			protected HttpClient createC() throws Exception {
				return null;
			}

			@Override
			protected HttpUriRequest createR() throws Exception {
				return null;
			}

		};
		cb.onProcess(null, 100);
		cb.onProcess(100);
		try {
			cb.onProcEnd(new HResp(), new InputStream() {

				@Override
				public int read() throws IOException {
					return 0;
				}

				@Override
				public void close() throws IOException {
					throw new IOException();
				}

			}, new MultiOutputStream() {

				@Override
				public void write(int arg0) throws IOException {

				}
			});
		} catch (Exception e) {

		}
	}

	public class Abc3 extends Abc {

		@Override
		public void onProcess(CBase c, PIS pis, float rate) {
			System.err.println("Pis:" + pis.filename + "->" + rate);
		}

		public Abc3(CountDownLatch cdl, String name) {
			super(cdl, name);
		}

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			cdl.countDown();
			System.out.println(data + "--------->\n\n\n");
		}
	}

	public void testUpload() throws Exception {
		this.rerr = null;
		final CountDownLatch cdl = new CountDownLatch(1);
		ByteArrayInputStream bais = new ByteArrayInputStream("abc\n这是中文\n".getBytes());
		H.doPost("http://" + ts_ip + ":8000/rec_f?sw=testing&abc=这是中文2&_hc_=NO", PIS.create("file", "abc.txt", bais),
				new Abc3(cdl, "tu1"));
		Thread.sleep(200);
		cdl.await();
		assertNull(this.rerr);
	}

	public void testUploadBm() throws Exception {
		this.rerr = null;
		final CountDownLatch cdl = new CountDownLatch(1);
		Bitmap bm = BitmapFactory.decodeResource(this.getActivity().getResources(), R.drawable.ic_launcher);
		H.doPost("http://" + ts_ip + ":8000/rec_f?sw=testing&abc=这是中文2&_hc_=NO", "file", bm, new Abc3(cdl, "tu2"));
		Thread.sleep(200);
		cdl.await();
		assertNull(this.rerr);
	}

	public void testJsonErr() throws Exception {
		final CountDownLatch cdl = new CountDownLatch(1);
		new TJson(cdl, "").onSuccess(null, null, (String) null);
		new TJson(cdl, "").onSuccess(null, null, "");
		new TJson2(cdl, "").onSuccess(null, null, (String) null);
		new TJson2(cdl, "").onSuccess(null, null, "");
		new TJson2(cdl, "").onError(null, "", null);
		new TJson2(cdl, "").onError(null, (String) null, null);
		new TJson2(cdl, "").onError(null, "{}", null);
	}

	// public void testURL() throws Throwable {
	// this.runTestOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// new HCallback.HandlerCallback(null);// for register handler.
	//
	// }
	// });
	// //
	// H.doGet("http://rcp.dev.jxzy.com/get-course-list?param={\"pa\":{\"pn\":1,\"ps\":5},\"filter\":\"\"}",
	// // null);
	// // H.doGet("http://rcp.dev.jxzy.com/get-course-list", null);
	// // H.doGet("http://rcp.dev.jxzy.com/get-course-list?", null);
	// // H.doGet("http://"
	// // + ts_ip
	// // +
	// //
	// ":8000/g_args?param={\"pa\":{\"pn\":1,\"ps\":5},\"filter\":\"\"}&a=1&b=abc&c=这是中文",
	// // null);
	// // H.doGet("http://" + ts_ip + ":8000/g_args?a=1&b=abc&c=这是中文", null);
	// final CountDownLatch cdl = new CountDownLatch(1);
	// H.doGet("http://rcp.dev.jxzy.com/get-section?courseId=310&token=",
	// new None(cdl, "sss"));
	// cdl.await();
	// // Thread.sleep(5000);
	// }

	public void testData() throws Throwable {
		final CountDownLatch cdl = new CountDownLatch(1);
		HCacheCallback hc = new HCacheCallback() {

			@Override
			public void onSuccess(CBase c, HResp res, String data) throws Exception {
				cdl.countDown();
				assertEquals("abc", data);
			}

			@Override
			public void onError(CBase c, String cache, Throwable err) throws Exception {
				cdl.countDown();
				assertNull(err);
			}
		};
		H.doPostData("http://" + ts_ip + ":8000/data_j", "abc", hc).getEntity();
		cdl.await();
	}

	public class TestPostJsonCallback_ extends HCacheCallback {
		public String data;
		public CountDownLatch cdl;

		public TestPostJsonCallback_(CountDownLatch cdl, String data) {
			this.data = data;
			this.cdl = cdl;
		}

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			cdl.countDown();
			assertEquals(this.data, data);
		}

		@Override
		public void onError(CBase c, String cache, Throwable err) throws Exception {
			cdl.countDown();
			assertNull(err);
		}
	}

	public void testPostJson() throws Throwable {
		final CountDownLatch cdl = new CountDownLatch(2);

		// for json object.
		Args.V args = Args.A("a", "1").A("b", "xx").A("c", "val");
		H.doPostData("http://" + ts_ip + ":8000/data_j", args, new TestPostJsonCallback_(cdl, args.toString()));

		// for json array.
		Args.VAry aary = new Args.VAry();
		aary.A(args);
		aary.A(Args.A("a1", "1").A("b", "xx"));
		H.doPostData("http://" + ts_ip + ":8000/data_j", aary, new TestPostJsonCallback_(cdl, args.toString()));

		//
		cdl.await();
	}

	public void testDlm() throws Throwable {
		CDL cdl = new CDL(2);
		File sdir = this.getActivity().getExternalFilesDir("dd");
		String tf = sdir.getAbsolutePath() + File.separator + "22.png";
		Utils.del(new File(tf));
		Utils.del(new File(tf + ".awf.tmp"));
		String did;
		Hash hash;
		FileInputStream fis;
		//
		did = H.doGet("http://" + ts_ip + ":8000/22.png", tf, new DlmBack(cdl, 2));
		cdl.waitc(1);
		while (H.dlm().find(did) != null) {
			Thread.sleep(200);
		}
		assertNull(this.rerr);
		fis = new FileInputStream(tf);
		hash = FUtil.sha1(fis, null);
		fis.close();
		Assert.assertEquals(false,
				"c19441db790b519c33fbb775e82695c27e7afed4".equalsIgnoreCase(Utils.byte2hex(hash.hash)));
		did = H.doGet("http://" + ts_ip + ":8000/22.png", tf, new DlmBack(cdl, -1));
		cdl.await();
		while (H.dlm().find(did) != null) {
			Thread.sleep(200);
		}
		fis = new FileInputStream(tf);
		hash = FUtil.sha1(fis, null);
		fis.close();
		Assert.assertEquals(true,
				"c19441db790b519c33fbb775e82695c27e7afed4".equalsIgnoreCase(Utils.byte2hex(hash.hash)));
		assertNull(this.rerr);
		// cdl.await();
	}

	public void testDlm2() throws Exception {
		String url = "http://adload.kuxiao.cn/armeabi-v7a/libxwalkcore.zip";
		CDL cdl = new CDL(2);
		File sdir = this.getActivity().getExternalFilesDir("dd");
		String tf = sdir.getAbsolutePath() + File.separator + "xx.zip";
		Utils.del(new File(tf));
		Utils.del(new File(tf + ".awf.tmp"));
		String did;
		//
		did = H.doGet(url, tf, new DlmBack(cdl, 2));
		cdl.waitc(1);
		while (H.dlm().find(did) != null) {
			Thread.sleep(200);
		}
		assertNull(this.rerr);
	}

	public class DlmBack implements DlmCallback {
		CDL cdl;
		int times = -1;
		int times_ = 0;

		public DlmBack(CDL cdl, int times) {
			this.cdl = cdl;
			this.times = times;
		}

		@Override
		public void onProcess(DlmC c, float rate) {
			System.err.println(c.getFullUrl() + "->" + rate);
			this.times_++;
			if (rate > 1) {
				rerr = new Exception("rate is " + rate);
			}
			if (this.times == this.times_) {
				this.cdl.countDown();
			}
		}

		@Override
		public void onProcEnd(DlmC c, HResp res) throws Exception {
			System.err.println(c.getFullUrl() + "->end->");
		}

		@Override
		public void onSuccess(DlmC c, HResp res) throws Exception {
			System.err.println(c.getFullUrl() + "->OK->");
			this.cdl.countDown();
		}

		@Override
		public void onError(DlmC c, Throwable err) throws Exception {
			System.err.println(c.getFullUrl() + "->ERR->" + err.getMessage());
			if (this.times != this.times_) {
				this.cdl.countDown();
			}
			rerr = err;
		}

		@Override
		public void onExecErr(DlmC c, Throwable e) {
			System.err.println(c.getFullUrl() + "->ExecErr->" + e.getMessage());
			rerr = e;
		}
	}
}
