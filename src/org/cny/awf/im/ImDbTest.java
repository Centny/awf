package org.cny.awf.im;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.cny.awf.test.MainActivity;
import org.cny.jwf.im.Msg;

import android.test.ActivityInstrumentationTestCase2;

public class ImDbTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public ImDbTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testQuery() throws Exception {
		ImDb idb = new ImDb();
		idb.load(getActivity());
		idb.clearMsg("");
		Msg m = new Msg();
		m.d = "D";
		m.r = new String[] { "ss" };
		m.s = "SS";
		m.a = "SS";
		m.t = 0;
		m.time = 123434;
		m.c = "ssss".getBytes();
		m.i = "I-0";
		m.status = Msg.MS_REV;
		idb.add(m);
		m.i = "I-1";
		idb.add(m);
		m.r = new String[] { "s1" };
		m.i = "I-2";
		idb.add(m);
		m.r = new String[] { "s2", "s1" };
		m.i = "I-3";
		idb.add(m);
		m.r = new String[] { "s3", "s2" };
		m.i = "I-4";
		idb.add(m);
		List<Msg> ms;
		ms = idb.listMsgT(0);
		Assert.assertEquals(5, ms.size());
		ms = idb.listMsgS("SS");
		Assert.assertEquals(5, ms.size());
		ms = idb.listMsgR("");
		Assert.assertEquals(5, ms.size());
		ms = idb.listMsgR("s2");
		Assert.assertEquals(2, ms.size());
		ms = idb.listMsgR("s1");
		Assert.assertEquals(2, ms.size());
		ms = idb.listMsgR("ss");
		Assert.assertEquals(2, ms.size());
		Assert.assertEquals(5, idb.sumNoReadMsg().longValue());
		Assert.assertEquals(5, idb.sumMsgS(Msg.MS_REV).longValue());
		idb.update("I-0", Msg.MS_READED);
		Assert.assertEquals(4, idb.sumMsgS(Msg.MS_REV).longValue());
		idb.update(new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("I-1");
				add("I-2");
			}
		}, Msg.MS_READED);
		Assert.assertEquals(2, idb.sumNoReadMsg("SS").longValue());
		idb.markReaded("SS");
		Assert.assertEquals(0, idb.sumNoReadMsg("SS").longValue());
		System.out.println(ms);
		idb.close();
		// try {
		// ImDb.free();
		// ImDb.loadDb_(getActivity(), "sfsfs");
		// } catch (Exception E) {
		//
		// }
	}

	public void testLoadErr() {
		try {
			new ImDb().load(getActivity(), "sfsdfs");
		} catch (Exception e) {

		}
	}
}
