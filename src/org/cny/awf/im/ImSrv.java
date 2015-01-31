package org.cny.awf.im;

import java.util.Date;

import org.cny.awf.base.BaseSrv;
import org.cny.awf.net.NetInfo;
import org.cny.jwf.im.IMC.MsgListener;
import org.cny.jwf.im.Msg;
import org.cny.jwf.im.PbSckIMC;
import org.cny.jwf.im.SckIMC;
import org.cny.jwf.netw.r.NetwRunnable;
import org.cny.jwf.netw.r.NetwRunnable.EvnListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public abstract class ImSrv extends BaseSrv implements MsgListener,
		EvnListener, Runnable {
	public static final String IMC_ACTION = "ON_IMC";
	protected final Logger L = LoggerFactory.getLogger(this.getClass());
	protected String host;
	protected int port;
	protected SckIMC imc;
	protected Thread thr;
	protected boolean running = false;
	protected ImDb db;

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
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (!this.running) {
			this.start();
		}
	}

	protected void doConAction(Context ctx, Intent it) {
		if (!this.running) {
			this.start();
		}
	}

	@Override
	public void onErr(NetwRunnable nr, Throwable e) {
		L.warn("ImSrv exec err:", e);
	}

	@Override
	public void onMsg(Msg m) {
		Intent it = new Intent(IMC_ACTION);
		it.putExtra("msg", m);
		this.sendBroadcast(it);
	}

	@Override
	public void run() {
		while (this.running) {
			try {
				this.run_(new Date().getTime());
			} catch (Exception e) {
				L.debug("try running err:", e);
			}
		}
		this.running = false;
		L.warn("background thread is stopped");
	}

	protected void create() {
		this.host = info.metaData.getString("host");
		this.port = info.metaData.getInt("port");
		this.imc = new PbSckIMC(this, this, this.host, this.port);
		this.db = ImDb.loadDb_(this);
		this.running = true;
		this.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context ctx, Intent it) {
				String action = it.getAction();
				if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					doConAction(ctx, it);
				}
			}
		}, null);
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
				this.imc.run();
			} else {
				L.debug("network is not available, thread will stop");
				this.running = false;
				return;
			}
		} catch (Throwable e) {
			L.error("running err:", e);
		}
		long now = new Date().getTime();
		if (now - last < 3000) {
			L.debug("retry after 3 s");
			Thread.sleep(3000);
		}
		last = now;
	}

}
