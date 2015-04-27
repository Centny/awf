package org.cny.awf.im;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.cny.awf.im.ImSrvTest.TImSrv;
import org.cny.awf.util.CDL;
import org.cny.jwf.im.Msg;
import org.cny.jwf.im.c.RC;
import org.cny.jwf.netw.bean.Con;
import org.cny.jwf.netw.bean.Con.Res;
import org.cny.jwf.netw.r.NetwRunnable;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ServiceTestCase;
import android.util.Log;

public class ImSrvTest extends ServiceTestCase<TImSrv> {

	public static class TImSrv extends ImSrv {
		public CDL cdl;
		public RC rc;

		@Override
		public void onCreate() {
			this.cdl = new CDL(208);
			super.onCreate();
			this.rc = new RC(this.host, this.port) {

				@Override
				public void onMsg(Msg m) {
					super.onMsg(m);
					try {
						this.sms(m.s, 0, "msg->back");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			};
			try {
				this.rc.li("10", "xxx");
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
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
			try {
				this.ur();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (rc.r == null) {
							Thread.sleep(100);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < 100; i++) {
						try {
							sms(rc.r, 0, "abdd这是中文".getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			this.cdl.countDown();
		}

		@Override
		protected void onLo(NetwRunnable nr, Con.Res m) {
			this.cdl.countDown();
			System.err.println("onLo-->");
		}

		@Override
		protected Object liArgs(Object v) {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("token", "abc");
			super.liArgs(null);// for test.
			super.loArgs(args);// for test.
			return super.liArgs(args);
		}

		@Override
		public void run() {
			super.run();
			this.cdl.countDown();
		}

		@Override
		protected ImDb Db() {
			try {
				return new ImDb().load(getBaseContext());
			} catch (IOException e) {
				return null;
			}
		}

	}

	public ImSrvTest() {
		super(TImSrv.class);
	}

	public void testIm() throws Exception {
		this.startService(null);
		Thread.sleep(500);
		this.startService(null);
		final TImSrv srv = this.getService();
		Thread.sleep(200);
		srv.cdl.setLog(true);
		srv.start();// for test.
		// srv.imc.close();
		// Thread.sleep(2000);
		System.err.println("waiting login and messsage");
		srv.cdl.waitc(102);
		srv.lo(null);
		srv.cdl.waitc(103);
		srv.imc.close();
		srv.cdl.waitc(104);
		// //
		Thread.sleep(2000);
		srv.cdl.waitc(205);
		srv.lo(null);
		srv.cdl.waitc(206);
		srv.imc.close();
		srv.cdl.waitc(207);
		// //
		//
		srv.liArgs(null);
		srv.loArgs(null);
		// srv.onDestroy();
		srv.cdl.await();

		ImSrv is = new ImSrv() {

			@Override
			protected boolean netAvaliable() {
				this.imc = srv.imc;
				throw new RuntimeException();
			}

			@Override
			protected void onLi(NetwRunnable nr, Res m) {
			}

			@Override
			protected void onLo(NetwRunnable nr, Res m) {

			}

			@Override
			public IBinder onBind(Intent arg0) {
				return null;
			}

			@Override
			protected ImDb Db() {
				return null;
			}

		};
		try {
			is.run_();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testErr1() {
		ImSrv is = new ImSrv() {

			@Override
			protected boolean run_() throws Exception {
				this.running = false;
				throw new Exception();
			}

			@Override
			protected void onLi(NetwRunnable nr, Res m) {
			}

			@Override
			protected void onLo(NetwRunnable nr, Res m) {

			}

			@Override
			public IBinder onBind(Intent arg0) {
				return null;
			}

			@Override
			protected void create() {
				throw new RuntimeException();
			}

			@Override
			protected void close() throws IOException {
				throw new IOException();
			}

			@Override
			protected ImDb Db() {
				try {
					return new ImDb().load(getBaseContext());
				} catch (IOException e) {
					return null;
				}
			}

		};
		try {
			is.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			is.onDestroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			is.onCreate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testErr2() {
		ImSrv is = new ImSrv() {

			@Override
			protected boolean netAvaliable() {
				return false;
			}

			@Override
			protected void onLi(NetwRunnable nr, Res m) {
			}

			@Override
			protected void onLo(NetwRunnable nr, Res m) {

			}

			@Override
			public IBinder onBind(Intent arg0) {
				return null;
			}

			@Override
			protected ImDb Db() {
				try {
					return new ImDb().load(getBaseContext());
				} catch (IOException e) {
					return null;
				}
			}

		};
		try {
			is.run_();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Notification nt;

	public void testOnMsg() throws IOException {
		ImSrv is = new ImSrv() {
			@Override
			protected void onLi(NetwRunnable nr, Res m) {
			}

			@Override
			protected void onLo(NetwRunnable nr, Res m) {

			}

			@Override
			public IBinder onBind(Intent arg0) {
				return null;
			}

			@Override
			protected ImDb Db() {
				return null;
			}

			@Override
			public void onMsg(Msg m) {
				super.onMsg(m);
				// this.Db().add(m);
				this.doBroadcast(m);
			}

			@Override
			protected Notification createNotify(Msg m) {
				if (nt == null) {
					return super.createNotify(m);
				} else {
					return nt;
				}
			}

			@Override
			public Object getSystemService(String name) {
				return ImSrvTest.this.getContext().getSystemService(name);
			}

		};
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this
				.getContext());
		Msg msg = new Msg();
		msg.i = "ssx" + new Date().getTime();
		msg.t = 1;
		msg.s = "S";
		msg.r = new String[] { "R" };
		msg.c = "abcc".getBytes();
		is.onMsg(msg);
		nt = new NotificationCompat.Builder(this.getContext()).build();
		is.onMsg(msg);
		lbm.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {

			}
		}, new IntentFilter(ImSrv.IMC_ACTION));
		is.onMsg(msg);
		lbm.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {

			}
		}, new IntentFilter(ImSrv.IMC_ACTION + "1"));
		is.onMsg(msg);
		lbm.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {

			}
		}, new IntentFilter(ImSrv.IMC_ACTION + "S"));
		is.onMsg(msg);
		lbm.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {

			}
		}, new IntentFilter(ImSrv.IMC_ACTION + "R"));
		is.onMsg(msg);
	}

	public void testErr() {
		try {
			new ImSrv() {

				@Override
				public boolean isRunning() {
					throw new RuntimeException();
				}

				@Override
				protected void onLi(NetwRunnable nr, Res m) {

				}

				@Override
				protected void onLo(NetwRunnable nr, Res m) {

				}

				@Override
				public IBinder onBind(Intent arg0) {
					return null;
				}

				@Override
				protected ImDb Db() {
					try {
						return new ImDb().load(getBaseContext());
					} catch (IOException e) {
						return null;
					}
				}

			}.start();
		} catch (Exception e) {

		}
	}
}
