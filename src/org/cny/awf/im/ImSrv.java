package org.cny.awf.im;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.cny.awf.base.BaseSrv;
import org.cny.awf.net.NetInfo;
import org.cny.jwf.im.IMC.MsgListener;
import org.cny.jwf.im.Msg;
import org.cny.jwf.im.PbSckIMC;
import org.cny.jwf.im.SckIMC;
import org.cny.jwf.netw.bean.Con;
import org.cny.jwf.netw.r.Cmd;
import org.cny.jwf.netw.r.Netw;
import org.cny.jwf.netw.r.NetwRunnable;
import org.cny.jwf.netw.r.NetwRunnable.CmdListener;
import org.cny.jwf.netw.r.NetwRunnable.EvnListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public abstract class ImSrv extends BaseSrv implements MsgListener,
		EvnListener, Runnable {
	public static final String IMC_ACTION = "ON_IMC";
	protected final Logger L = LoggerFactory.getLogger(this.getClass());
	protected String host;
	protected int port;
	protected int retry;
	protected SckIMC imc;
	protected Thread thr;
	protected boolean running = false;
	protected ImDb db;
	protected BroadcastReceiver con;

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			this.create();
		} catch (Throwable e) {
			L.error("create service err", e);
		}
	}

	@Override
	public void onDestroy() {
		this.running = false;
		try {
			this.close();
		} catch (Exception e) {
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		this.checkStart();
	}

	protected void doConAction(Context ctx, Intent it) {
		this.checkStart();
	}

	@Override
	public void onErr(NetwRunnable nr, Throwable e) {
		L.warn("ImSrv exec err:", e);
	}

	@Override
	public void onCon(NetwRunnable nr, Netw nw) throws Exception {
		this.li(null);
	}

	@Override
	public void onMsg(Msg m) {
		Intent it = new Intent(IMC_ACTION);
		it.putExtra("msg", m);
		this.sendBroadcast(it);
	}

	@Override
	public void run() {
		this.running = true;
		while (this.running) {
			try {
				this.run_(new Date().getTime());
			} catch (Exception e) {
				L.debug("try running err:", e);
			}
			// this.running = false;
		}
		this.running = false;
		L.warn("background thread is stopped");
	}

	protected void checkStart() {
		if (!this.running) {
			this.start();
		}
	}

	protected void create() {
		this.host = info.metaData.getString("host");
		this.port = info.metaData.getInt("port");
		this.retry = info.metaData.getInt("retry", 3000);
		this.imc = new PbSckIMC(this, this, this.host, this.port);
		this.db = ImDb.loadDb_(this);
		this.con = new BroadcastReceiver() {

			@Override
			public void onReceive(Context ctx, Intent it) {
				String action = it.getAction();
				if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					doConAction(ctx, it);
				}
			}
		};
		this.registerReceiver(this.con, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	protected void start() {
		synchronized (this) {
			if (this.running) {
				L.warn("the IMC thread already running");
				return;
			}
			this.running = true;
			this.thr = new Thread(this);
			this.thr.start();
		}
	}

	protected void run_(long last) throws Exception {
		try {
			NetInfo.net().update(this);
			if (NetInfo.net().isAvailable()) {
				this.imc.run_c();
			} else {
				L.debug("network is not available, thread will stop");
			}
		} catch (Throwable e) {
			L.error("running err:", e);
			this.imc.rcClear(new Exception(e));
		}

		long now = new Date().getTime();
		if (now - last < this.retry) {
			L.debug("retry after {} ms", this.retry);
			Thread.sleep(this.retry);
		}
		last = now;
	}

	public void li(Object v) throws Exception {
		this.imc.li(this.liArgs(v), new CmdListener() {

			@Override
			public void onCmd(NetwRunnable nr, Cmd m) {
				onLi(nr, m.V(Con.Res.class));
			}

		});
	}

	public void lo(Object v) throws Exception {
		this.imc.lo(this.loArgs(v), new CmdListener() {

			@Override
			public void onCmd(NetwRunnable nr, Cmd m) {
				onLo(nr, m);
			}

		});
	}

	public void sms(String[] r, byte t, byte[] c) throws IOException {
		this.imc.sms(r, t, c);
	}

	public void sms(String[] r, int t, byte[] c) throws IOException {
		this.sms(r, (byte) t, c);
	}

	public void sms(String r, int t, byte[] c) throws IOException {
		this.sms(new String[] { r }, t, c);
	}

	protected Object loArgs(Object v) {
		if (v == null) {
			return new HashMap<String, Object>();
		} else {
			return v;
		}
	}

	protected void close() throws IOException {
		this.imc.close();
	}

	protected abstract void onLi(NetwRunnable nr, Con.Res m);

	protected abstract void onLo(NetwRunnable nr, Cmd m);

	protected abstract Object liArgs(Object v);

}
