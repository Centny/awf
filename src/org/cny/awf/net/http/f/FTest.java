package org.cny.awf.net.http.f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HDb;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.cres.CRes;
import org.cny.awf.test.MainActivity;
import org.cny.awf.test.R;
import org.cny.awf.util.CDL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;

public class FTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private Throwable rerr = null;
	private String ts_ip;
	private String turl;

	public FTest() {
		super(MainActivity.class);
	}

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
		H.CTX = this.getActivity();
		HDb.loadDb_(this.getActivity()).clearR();
	}

	public void testImg() throws Exception {
		this.rerr = null;
		final CDL cdl = new CDL(2);
		Bitmap bm = BitmapFactory.decodeResource(this.getActivity()
				.getResources(), R.drawable.ic_launcher);
		F.FStrCallback cb = new F.FStrCallback() {

			@Override
			public void onFSuccess(CBase c, HResp res, FPis img, String url) {
				turl = url;
				cdl.countDown();
			}

			@Override
			public void onError(CBase c, CRes<String> cache, Throwable err)
					throws Exception {
				rerr = err;
				err.printStackTrace();
				cdl.countDown();
			}
		};
		F.doPost("http://" + ts_ip + ":8000/rec_x?_hc_=NO",
				F.create("file", bm), cb);
		F.doPost("http://" + ts_ip + ":8000/rec_x?_hc_=NO",
				F.create("file", bm), cb);
		Thread.sleep(200);
		cdl.waitc(2);
		assertNotNull(this.turl);
		HDb db = HDb.loadDb_(this.getActivity());
		HResp res = db.find(this.turl, "GET", "");
		assertNotNull(res);
		assertNull(this.rerr);
		//
	}
}
