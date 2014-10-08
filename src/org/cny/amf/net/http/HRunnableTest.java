package org.cny.amf.net.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import org.cny.amf.net.http.H.HDownCallback;
import org.cny.amf.net.http.HRunnable.HThreadTask;
import org.cny.amf.test.MainActivity;

import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class HRunnableTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public HRunnableTest() {
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
	}

	public void testThread() throws Throwable {
		System.out.println("abcc");
		this.rerr = null;
		final CountDownLatch cdl = new CountDownLatch(1);
		final File p = new File(this.dl, "www.txt");
		// this.runTestOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		HThreadTask hr = new HThreadTask("http://" + ts_ip
				+ ":8000/g_args?a=1&b=abc&c=这是中文", new HDownCallback(
				p.getAbsolutePath()) {

			@Override
			public void onSuccess(HClient c) {
				System.out.println("abcc-01");
				cdl.countDown();
				super.onSuccess(c);
			}

			@Override
			public void onError(HClient c, Throwable err) {
				System.out.println("abcc-02");
				cdl.countDown();
				super.onError(c, err);
				rerr = err;
			}

		});
		hr.start();
		// try {
		// hr.join();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// });
		cdl.await();
		if (this.rerr != null) {
			assertNull(this.rerr.getMessage(), this.rerr);
		}
		File rp = new File(this.dl, "www.txt");
		assertTrue(rp.getAbsolutePath() + " not found", rp.exists());
		FileReader r = new FileReader(rp);
		BufferedReader reader = new BufferedReader(r);
		String line = null;
		while ((line = reader.readLine()) != null) {
			Log.e("Line", line);
		}
		reader.close();
		assertTrue(new File(this.dl, "www.txt").delete());
	}

	public void testThreadErr() throws Throwable {
		this.rerr = null;
		final CountDownLatch cdl = new CountDownLatch(1);
		final File p = new File(this.dl, "www.txt");
		this.runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				HThreadTask hr = new HThreadTask("http://" + ts_ip
						+ ":8000/g_args?a=1&b=abc&c=这是中文", new HDownCallback(p
						.getAbsolutePath()) {

					@Override
					public OutputStream onBebin(HClient c, HResp r)
							throws Exception {
						return null;
					}

					@Override
					public void onSuccess(HClient c) {
						cdl.countDown();
						super.onSuccess(c);
						rerr = new Exception("error message");
					}

					@Override
					public void onError(HClient c, Throwable err) {
						cdl.countDown();
						super.onError(c, err);

					}

				});
				hr.start();
				try {
					hr.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		cdl.await();
		if (this.rerr != null) {
			assertNull(this.rerr.getMessage(), this.rerr);
		}
	}

	public void testJoinErr() throws Throwable {
		this.runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				HThreadTask hr = new HThreadTask("http://" + ts_ip
						+ ":8000/g_args?a=1&b=abc&c=这是中文",
						new H.HDownCallback());
				System.err.println(hr.getThr() == null);
				try {
					hr.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
