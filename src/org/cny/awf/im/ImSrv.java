package org.cny.awf.im;

import java.io.IOException;
import java.net.SocketException;
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
import org.cny.jwf.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;

public abstract class ImSrv extends BaseSrv implements MsgListener,
		EvnListener, Runnable {
	public static final String IMC_ACTION = "ON_IMC";
	public static final String BC_MSG = "msg";
	public static final String NOTIFY_TAG = "IMC";
	public static final int NOTIFY_ID = 10;
	protected final Logger L = LoggerFactory.getLogger(this.getClass());
	protected String host;
	protected int port;
	protected int retry;
	protected SckIMC imc;
	protected Thread thr;
	protected boolean running = false;
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
	public void begCon(NetwRunnable nr) throws Exception {

	}

	public void onRecon() {

	}

	public void begRun() {

	}

	public void endRun() {

	}

	@Override
	public void onMsg(Msg m) {
		L.debug("on recieve message:{}", m.toString());
		// this.db.add(m);
		// this.doNotify(m);
	}

	protected void doBroadcast(Msg m) {
		LocalBroadcastManager lbm;
		Intent it;
		boolean rec = false;
		lbm = LocalBroadcastManager.getInstance(this);
		// broadcast to all.
		it = new Intent(IMC_ACTION);
		it.putExtra(BC_MSG, m);
		rec = lbm.sendBroadcast(it) || rec;
		// broadcast by type.
		it = new Intent(IMC_ACTION + "-" + m.t);
		it.putExtra(BC_MSG, m);
		rec = lbm.sendBroadcast(it) || rec;
		// broadcast by sender.
		it = new Intent(IMC_ACTION + "-" + m.s);
		it.putExtra(BC_MSG, m);
		rec = lbm.sendBroadcast(it) || rec;
		// broadcast by R
		it = new Intent(IMC_ACTION + "-" + Utils.join(m.r));
		it.putExtra(BC_MSG, m);
		rec = lbm.sendBroadcast(it) || rec;
		if (rec) {// already received.
			return;
		}
		Notification nt = this.createNotify(m);
		if (nt == null) {
			return;
		}
		NotificationManager nm = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		nm.notify(NOTIFY_TAG, NOTIFY_ID, this.createNotify(m));
	}

	@Override
	public void run() {
		this.running = true;
		this.begRun();
		while (this.running) {
			try {
				this.running = this.run_();
			} catch (Exception e) {
				L.debug("try running err:", e);
			}
			// this.running = false;
		}
		this.endRun();
		this.running = false;
		L.warn("background thread is stopped");
	}

	protected void checkStart() {
		if (!this.running) {
			this.start();
		}
	}

	protected abstract ImDb Db();

	protected void create() {
		this.host = info.metaData.getString("host");
		this.port = info.metaData.getInt("port");
		this.retry = info.metaData.getInt("retry", 8000);
		this.imc = new PbSckIMC(this, this, this.host, this.port);
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

	public boolean isRunning() {
		return this.running;
	}

	protected void start() {
		synchronized (this) {
			if (this.isRunning()) {
				L.warn("the IMC thread already running");
				return;
			}
			L.debug("starting ImSrv--->");
			this.running = true;
			this.thr = new Thread(this);
			this.thr.start();
		}
	}

	protected boolean netAvaliable() throws SocketException {
		NetInfo.net().update(this);
		return NetInfo.net().isAvailable();
	}

	protected boolean run_() throws Exception {
		try {
			if (this.netAvaliable()) {
				this.imc.run_c();
			} else {
				L.debug("network is not available, thread will stop");
				return false;
			}
		} catch (Throwable e) {
			this.onRecon();
			L.error("running err,will retry after {} ms:", this.retry, e);
			Thread.sleep(this.retry);
			this.imc.rcClear(new Exception(e));
		}
		return true;
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
				onLo(nr, m.V(Con.Res.class));
			}

		});
	}

	public void ur() throws Exception {
		this.imc.ur();
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

	protected Object liArgs(Object v) {
		if (v == null) {
			return new HashMap<String, Object>();
		} else {
			return v;
		}
	}

	protected void close() throws IOException {
		this.imc.close();
	}

	/**
	 * create the notify when the message not receiver.
	 * 
	 * @return notify
	 */
	protected Notification createNotify(Msg m) {
		return null;
	}

	protected abstract void onLi(NetwRunnable nr, Con.Res m);

	protected abstract void onLo(NetwRunnable nr, Con.Res m);

}
