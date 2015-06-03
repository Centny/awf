package org.cny.awf.mr;

import java.util.Date;
import java.util.List;

import org.cny.awf.net.http.H;
import org.cny.awf.net.http.cres.CRes;
import org.cny.awf.net.http.cres.CRes.BaseRes;
import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

import com.google.gson.reflect.TypeToken;

public class MrTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public MrTest() {
		super(MainActivity.class);
	}

	public static class Mrv extends BaseRes<Mrv> {
		public String a;
		public String b;

		public Mrv() {

		}

		public Mrv(String a, String b) {
			super();
			this.a = a;
			this.b = b;
		}

		@Override
		public TypeToken<CRes<Mrv>> createToken() {
			return new TypeToken<CRes<Mrv>>() {
			};
		}

		@Override
		public TypeToken<CRes<List<Mrv>>> createTokenL() {
			return new TypeToken<CRes<List<Mrv>>>() {
			};
		}

	}

	public void testMr() {
		H.CTX = this.getActivity();
		Mr mr = new Mr("http://192.168.2.57:9904/mr/", "mr-"
				+ new Date().getTime());
		mr.setBase("http://192.168.2.57:9904/mr");
		mr.set("i", 1);
		mr.set("f", 1.1f);
		mr.set("d", 1.2d);
		mr.set("s", "abc");
		mr.set("j", new Mrv("adata", "bdata"));
		mr.inc("i", 2);
		mr.inc("f", 2f);
		mr.inc("d", 2d);
		mr.push("is", 1);
		mr.push("fs", 1.1f);
		mr.push("ds", 1.2d);
		mr.push("ss", "abc");
		mr.push("js", new Mrv("adata0", "bdata0"));
		mr.push("js", new Mrv("adata1", "bdata1"));
		mr.push("js", new Mrv("adata2", "bdata2"));
		// List<Mrv> ms;
		// ms = new ArrayList<MrTest.Mrv>();
		// ms.add(new Mrv("a1", "b1"));
		// ms.add(new Mrv("a2", "b2"));
		// ms.add(new Mrv("a3", "b3"));
		// mr.set("js", ms);
		//
		assertEquals(3, mr.intv("i"));
		assertEquals(3, mr.longv("i"));
		assertEquals(3.1f, mr.floatv("f"));
		assertEquals(3.2d, mr.doublev("d"));
		assertEquals("abc", mr.stringv("s"));
		Mrv m = mr.objv("j", Mrv.class);
		assertEquals("adata", m.a);
		assertEquals("bdata", m.b);
		//
		List<Mrv> ms = mr.objvs("js", Mrv.class);
		assertEquals(3, ms.size());
		for (int i = 0; i < 3; i++) {
			assertEquals("adata" + i, ms.get(i).a);
		}
		//
		mr.del("i");

		Object err = null;
		try {
			err = null;
			mr.intv("i");
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			mr.intv("xx");
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			mr.stringv("xdxxx");
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			mr.objv("xsdfsdj", Mrv.class);
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			mr.objvs("xsdfsdj", Mrv.class);
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			mr.inc("sdfd", "S", "sdfsdf");
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			mr.set("sdfs", "I", "sdfsf");
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			mr.push("sdfs", "I", "sdfsf");
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			new Mr("http://192.168.2.57:9904/").del("mr");
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			new Mr("http://192.168.2.57:9x904/").intv("xxd");
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			new Mr("http://192.168.2.57:9x904/").stringv("xxd");
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			new Mr("http://192.168.2.57:9x904/", "sdd").objv("xsdfsdj",
					Mrv.class);
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		try {
			err = null;
			new Mr("http://192.168.2.57:9x904/", "sdd").objvs("xsdfsdj",
					Mrv.class);
		} catch (Exception e) {
			err = e;
		}
		assertNotNull(err);
		// ms = mr.objvs("js", Mrv.class);
		// assertEquals(3, ms.size());
		// for (int i = 0; i < 3; i++) {
		// assertEquals("a" + (i + 1), ms.get(i).a);
		// }
	}
}
