package org.cny.awf.net.http;

import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class H2Test extends ActivityInstrumentationTestCase2<MainActivity> {

	public H2Test() {
		super(MainActivity.class);
	}

	public void testMulti() throws Throwable {
		// this.runTestOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// new HCallback.HandlerCallback(null);// for register handler.
		//
		// }
		// });
		// HDb hdb = HDb.loadDb_(this.getActivity());
		// List<HResp> rs = hdb.list();
		// for (HResp r : rs) {
		// System.err.println(r.u);
		// }
		// hdb.clearR();
		// final CDL cdl = new CDL(10);
		// for (int i = 0; i < 5; i++) {
		// H.doGet("http://192.168.1.101/Tmp/AA/1.png?_hc_=I",
		// new HCacheCallback() {
		//
		// @Override
		// public void onSuccess(CBase c, HResp res, String data)
		// throws Exception {
		// System.out.println("----->" + data);
		// cdl.countDown();
		// }
		//
		// @Override
		// public void onError(CBase c, String cache, Throwable err)
		// throws Exception {
		// err.printStackTrace();
		// }
		// });
		// H.doGet("http://192.168.1.101/Tmp/AA/2.png?_hc_=I",
		// new HCacheCallback() {
		//
		// @Override
		// public void onSuccess(CBase c, HResp res, String data)
		// throws Exception {
		// System.out.println("----->" + data);
		// cdl.countDown();
		// }
		//
		// @Override
		// public void onError(CBase c, String cache, Throwable err)
		// throws Exception {
		// err.printStackTrace();
		// }
		// });
		// }
		// cdl.await();
	}
}
