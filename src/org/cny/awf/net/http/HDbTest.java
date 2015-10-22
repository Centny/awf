package org.cny.awf.net.http;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.cny.awf.test.MainActivity;
import org.cny.jwf.util.Utils;

import android.test.ActivityInstrumentationTestCase2;

public class HDbTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public HDbTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		HDb.free();
		super.tearDown();
	}

	public void testQuery() throws Exception {
		HDb hdb = HDb.loadDb_(this.getActivity());
		hdb.clearR();
		Utils.del(new File(this.getActivity().getExternalCacheDir(), "_hc_"));
		hdb.newCacheF();
		HResp r;
		List<HResp> rs;
		r = new HResp();
		r.u = "http://localhost";
		r.m = "GET";
		r.arg = "A=1";
		r.lmt = new Date().getTime();
		r.type = "Abc";
		r.len = 100;
		r.path = "abc";
		hdb.del(r.u, r.m, r.arg);
		hdb.add(r);
		rs = hdb.findv(r.u, r.m, r.arg);
		assertEquals(1, rs.size());
		HResp tr = rs.get(0);
		assertEquals(r.arg, tr.arg);
		assertEquals(r.type, tr.type);
		r = rs.get(0);
		assertTrue(r.tid > 0);
		hdb.del(r.tid);
		rs = hdb.findv(r.u, r.m, r.arg);
		assertEquals(rs.size(), 0);
		for (int i = 0; i < 10; i++) {
			System.out.println(hdb.fname());
		}
		hdb.flush(r);
		System.out.println(this.getActivity().getExternalCacheDir()
				.getAbsolutePath());
		try {
			HDb.free();
			HDb.loadDb_(this.getActivity(), "sssdfs");
		} catch (Exception E) {

		}
	}

	public void testList() {
		HDb hdb = HDb.loadDb_(this.getActivity());
		List<HResp> rs = hdb.list();
		for (HResp r : rs) {
			System.err.println(r.u);
		}
	}
}
