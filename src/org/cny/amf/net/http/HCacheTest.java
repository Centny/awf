package org.cny.amf.net.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

import org.cny.amf.test.MainActivity;

import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;

public class HCacheTest extends ActivityInstrumentationTestCase2<MainActivity> {
	public HCacheTest() {
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
		File ext = Environment.getExternalStorageDirectory();
		System.out.println(ext.getAbsolutePath());
		HDb.loadDb(this.getActivity()).clearR();
	}

	public void testHCache() throws IOException, InterruptedException {
		final CountDownLatch cdl = new CountDownLatch(4);
//		H.doGet("http://" + ts_ip + ":8000/g_args?a=1&b=abc&c=这是中文",
//				new HCache(this.getActivity()) {
//
//					@Override
//					public void onSuccess(HClient c, String data) {
//						System.out.println(data);
//						cdl.countDown();
//					}
//
//					@Override
//					public void onError(HClient c, String cache, Throwable err) {
//						err.printStackTrace();
//						cdl.countDown();
//					}
//				});
//		H.doGet("http://" + ts_ip + ":8000/g_args?a=1&b=abc&c=这是中文",
//				new HCache(this.getActivity()) {
//
//					@Override
//					public void onSuccess(HClient c, String data) {
//						System.out.println(data);
//						cdl.countDown();
//					}
//
//					@Override
//					public void onError(HClient c, String cache, Throwable err) {
//						err.printStackTrace();
//						cdl.countDown();
//					}
//				});
//		H.doGet("http://" + ts_ip + ":8000/g_args?a=1&b=abc&c=这是中文&_hc_=C",
//				new HCache(this.getActivity()) {
//
//					@Override
//					public void onSuccess(HClient c, String data) {
//						System.out.println(data);
//						cdl.countDown();
//					}
//
//					@Override
//					public void onError(HClient c, String cache, Throwable err) {
//						err.printStackTrace();
//						cdl.countDown();
//					}
//				});
//		H.doGet("http://sfsd00/g_args?a=1&b=abc&c=这是中文",
//				new HCache(this.getActivity()) {
//
//					@Override
//					public void onSuccess(HClient c, String data) {
//						System.out.println(data);
//						cdl.countDown();
//					}
//
//					@Override
//					public void onError(HClient c, String cache, Throwable err) {
//						System.out.println(err.getMessage());
//						cdl.countDown();
//					}
//				});
		cdl.await();
	}
}
