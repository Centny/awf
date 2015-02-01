package org.cny.awf.im;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cny.awf.im.ImSrvTest.TImSrv;
import org.cny.awf.util.CDL;
import org.cny.jwf.im.Msg;
import org.cny.jwf.netw.bean.Con.Res;
import org.cny.jwf.netw.r.Cmd;
import org.cny.jwf.netw.r.NetwRunnable;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.util.Log;

public class ImSrvTest extends ServiceTestCase<TImSrv> {

	public static class TImSrv extends ImSrv {
		public CDL cdl;

		@Override
		public void onCreate() {
			this.cdl = new CDL(207);
			super.onCreate();
		}

		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}

		@Override
		public void onErr(NetwRunnable nr, Throwable e) {
			super.onErr(nr, e);
			this.cdl.countDown();
			Log.e("TImSrv", e.getMessage() + "");
		}

		@Override
		public void onMsg(Msg m) {
			// System.out.println("receive message-->" + m.toString());
			super.onMsg(m);
			this.cdl.countDown();
		}

		@Override
		protected void onLi(NetwRunnable nr, final Res m) {
			if (this.cdl.getCurrent() > 5) {
				this.running = false;
			}
			System.err.println("onLi-->" + m.res.r);
			new Thread(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < 100; i++) {
						try {
							sms(m.res.r, 0, "abdd这是中文".getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			this.cdl.countDown();
		}

		@Override
		protected void onLo(NetwRunnable nr, Cmd m) {
			this.cdl.countDown();
			System.err.println("onLo-->");
		}

		@Override
		protected Object liArgs(Object v) {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("token", "abc");
			return args;
		}

		@Override
		public void run() {
			super.run();
			this.cdl.countDown();
		}

	}

	public ImSrvTest() {
		super(TImSrv.class);
	}

	public void testIm() throws Exception {
		this.startService(null);
		// this.startService(null);
		TImSrv srv = this.getService();
		Thread.sleep(200);
		// srv.imc.close();
		// Thread.sleep(2000);
		srv.cdl.waitc(101);
		srv.lo(null);
		srv.cdl.waitc(102);
		srv.imc.close();
		srv.cdl.waitc(103);
		//
		Thread.sleep(2000);
		srv.cdl.waitc(204);
		srv.lo(null);
		srv.cdl.waitc(205);
		srv.imc.close();
		srv.cdl.waitc(206);
		//
		srv.onDestroy();
		srv.cdl.await();
	}
}
