package org.cny.awf.sr;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.cny.awf.er.ActType;
import org.cny.awf.er.ER;
import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HCallback;
import org.cny.awf.net.http.HDb;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.cres.CRes;
import org.cny.awf.net.http.cres.CRes.HResCallbackNCaller;
import org.cny.awf.test.MainActivity;
import org.cny.awf.util.CDL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

public class SRTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public SRTest() {
		super(MainActivity.class);
	}

	private String ts_ip;

	private Throwable rerr = null;

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
		H.CTX = this.getActivity();
		HDb.loadDb_(this.getActivity()).clearR();
		System.out.println("test ip:" + ts_ip);
		System.out.println("test ip:" + ts_ip);
		System.out.println("test ip:" + ts_ip);
		System.out.println("test ip:" + ts_ip);
	}

	public void testSr() throws Exception {
		final CDL cdl = new CDL(4);
		ER.free();
		Context ctx = this.getActivity();
		new File(ctx.getExternalFilesDir(SR.SR_DIR), SR.SR_FN).delete();
		new File(ctx.getExternalFilesDir(SR.LOG_DIR), SR.LOG_FN).delete();
		new File(ctx.getExternalFilesDir(SR.ER_DIR), SR.ER_FN).delete();
		new SR(this.getActivity()).clear();
		Assert.assertEquals(
				0,
				new SR(getActivity()).dou("http://" + ts_ip
						+ ":8000/rec_sr?_hc_=NO&sr=0", null));
		cdl.countDown();
		cdl.waitc(1);
		ER.init(this.getActivity());
		ER.writem("TEST", "T", ActType.ATY.getVal());
		Assert.assertEquals(1, new SR(getActivity()) {

			@Override
			public void onError(HResCallbackNCaller<String> caller, CBase c,
					CRes<String> cache, Throwable err) throws Exception {
				super.onError(caller, c, cache, err);
				rerr = err;
				System.err.println(caller.rdata());
				cdl.countDown();
			}

			@Override
			public void onSuccess(HResCallbackNCaller<String> caller, CBase c,
					HResp res, CRes<String> data) throws Exception {
				super.onSuccess(caller, c, res, data);
				cdl.countDown();
			}

		}.dou("http://" + ts_ip + ":8000/rec_sr?_hc_=NO&sr=0", null));
		cdl.waitc(2);
		System.err.println("2222->");
		//
		SR.initSimpleLog(getActivity(), false);
		SR.initSimpleLog(getActivity(), true);
		ER.writem("TEST", "T", ActType.ATY.getVal());
		Logger l = LoggerFactory.getLogger(this.getClass());
		l.debug("sdfsdf");
		l.debug("sdfsdf");
		l.info("sdfdsfsd");
		l.error("--->");
		Assert.assertEquals(1, new SR(getActivity()) {

			@Override
			public void onError(HResCallbackNCaller<String> caller, CBase c,
					CRes<String> cache, Throwable err) throws Exception {
				super.onError(caller, c, cache, err);
				rerr = err;
				System.err.println(caller.rdata());
				cdl.countDown();
			}

			@Override
			public void onSuccess(HResCallbackNCaller<String> caller, CBase c,
					HResp res, CRes<String> data) throws Exception {
				super.onSuccess(caller, c, res, data);
				cdl.countDown();
			}

		}.dou("http://" + ts_ip + ":8000/rec_sr?_hc_=NO&sr=0", null));
		cdl.waitc(3);
		if (this.rerr != null) {
			this.rerr.printStackTrace();
			Assert.assertTrue(false);
		}
		System.err.println("3333->");
		ER.writem("TEST", "T", ActType.ATY.getVal());
		Assert.assertEquals(1, new SR(getActivity()) {

			@Override
			public void onError(HResCallbackNCaller<String> caller, CBase c,
					CRes<String> cache, Throwable err) throws Exception {
				super.onError(caller, c, cache, err);
				rerr = err;
				System.err.println(caller.rdata());
				cdl.countDown();
			}

			@Override
			public void onSuccess(HResCallbackNCaller<String> caller, CBase c,
					HResp res, CRes<String> data) throws Exception {
				super.onSuccess(caller, c, res, data);
				cdl.countDown();
			}

		}.dou("http://" + ts_ip + ":8000/rec_sr?_hc_=NO&sr=0", null));
		cdl.waitc(4);
		if (this.rerr != null) {
			this.rerr.printStackTrace();
			Assert.assertTrue(false);
		}
		System.err.println("4444->");
		new SR(getActivity()).onError(null, null, null, new Exception());
		CRes<String> res = new CRes<String>();
		res.code = 1;
		new SR(getActivity()).onSuccess(null, null, null, res);
	}
}
