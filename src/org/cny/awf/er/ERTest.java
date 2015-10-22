package org.cny.awf.er;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.cny.awf.test.MainActivity;
import org.cny.jwf.im.pb.Msg;
import org.cny.jwf.im.pb.Msg.RC;
import org.cny.jwf.util.Utils;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

public class ERTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public ERTest() {
		super(MainActivity.class);
	}

	public class TER extends ER {

		public TER(Context ctx) throws FileNotFoundException {
			super(ctx);
		}

	}

	@Info(name = "TER", info = { "1", "2" })
	public class C1 {

	}

	@Info(info = { "1", "2" })
	public class C2 {

	}

	public void testRecorder() throws Exception {
		File ddir = this.getActivity().getDir("_data_", Context.MODE_PRIVATE);
		Utils.del(ddir);
		try {
			ER.free();
		} catch (Exception e) {

		}
		RC.Builder rcb;
		try {
			rcb = Msg.RC.newBuilder();
			rcb.setC("C0");
			rcb.setR("R0");
			ER.writem(rcb.build());
		} catch (Exception e) {

		}
		ER er = ER.init(getActivity());
		ER.init(getActivity());
		ER.writem("U", "N", "A", 1, null);
		ER.writem("U", "N", "A", 1, new HashMap<String, Object>() {
			private static final long serialVersionUID = -1379506763832319807L;

			{
				put("a", "1");
				put("2", "2");
			}
		});

		ER.writem("U", this.getClass(), "A", 1, new HashMap<String, Object>() {
			private static final long serialVersionUID = -1379506763832319807L;

			{
				put("a", "1");
				put("2", "2");
			}
		});
		ER.writem("U", C1.class, "A", 1, null);
		ER.writem("U", C2.class, "A", 1, null);
		//
		er.close();
		rcb = Msg.RC.newBuilder();
		rcb.setC("C2");
		rcb.setR("R2");
		ER.writem(rcb.build());

		//
		er.close();
		er.close();
		rcb = Msg.RC.newBuilder();
		rcb.setC("C3");
		rcb.setR("R3");
		try {
			er.write(rcb.build());
		} catch (Exception e) {

		}
		try {
			ER.writem("", C1.class, "A", 1, null);
		} catch (Exception e) {

		}
		try {
			ER.writem("", "sfds", "A", 1, null);
		} catch (Exception e) {

		}
		er.close();
		er.ctx = null;
		ER.writem("U", C2.class, "A", 1, null);
		er.close();
		er.out = null;
		er.close();
		ER.free();
	}

	public void testER2() throws Exception {
		ER.init(this.getActivity());
		ER.setUid("UU");
		ER.writem("ss", "sdfsf", 11);
		ER.backup();
		Thread thr = new Thread(new Runnable() {

			@Override
			public void run() {
				throw new RuntimeException();
			}
		});
		thr.setUncaughtExceptionHandler(CrashHandler.instance());
		thr.start();
		thr.join();
		//
		ER.free();
		ER.backup();
		try {
			ER.free();
		} catch (Exception e) {

		}
		try {
			new ER(this.getActivity()) {

				@Override
				public void close() {
					throw new RuntimeException();
				}

			}.backup_();
		} catch (Exception e) {

		}
	}

	// public void testValueOf() {
	// ActType.valueOf("100");
	// ActType.valueOf("101");
	// ActType.valueOf("102");
	// ActType.values();
	// try {
	// ActType.valueOf(ActType.class, "100");
	// } catch (Exception e) {
	//
	// }
	// try {
	// ActType.valueOf("100ss");
	// } catch (Exception e) {
	//
	// }
	// }
}
