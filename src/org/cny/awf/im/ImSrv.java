package org.cny.awf.im;

import java.io.IOException;
import java.net.SocketException;
import java.util.Map;

import org.cny.awf.base.BaseSrv;
import org.cny.awf.net.NetInfo;
import org.cny.jwf.im.IMC.MsgListener;
import org.cny.jwf.im.Msg;
import org.cny.jwf.im.PbSckIMC;
import org.cny.jwf.im.SckIMC;
import org.cny.jwf.netw.bean.Con;
import org.cny.jwf.netw.r.Netw;
import org.cny.jwf.netw.r.NetwRunnable;
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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;

public abstract class ImSrv extends BaseSrv implements MsgListener, EvnListener, Runnable {
	public static final String IMC_ACTION = "ON_IMC";
	public static final String BC_MSG = "msg";
	public static final String NOTIFY_TAG = "IMC";
	public static final int NOTIFY_ID = 10;
	private static final Logger L = LoggerFactory.getLogger(ImSrv.class);
	protected String host;
	protected int port;
	protected int retry;
	protected SckIMC imc;
	protected Thread thr;
	protected boolean running = false;
	protected boolean execing = false;
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
		try {
			this.unregisterReceiver(this.con);
		} catch (Exception e) {

		}
		try {
			this.close();
		} catch (Exception e) {
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		this.registerReceiver(this.con, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
	}

	@Override
	public void begCon(NetwRunnable nr) throws Exception {

	}

	public void onRecon() {
		this.imc.rcClear(new Exception("ImSrv Reconnecting..."));
	}

	public void begRun() {

	}

	public void endRun() {

	}

	@Override
	public void onMsg(Msg m) {
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
		// broadcast by available.
		it = new Intent(IMC_ACTION + "-A-" + m.a);
		it.putExtra(BC_MSG, m);
		rec = lbm.sendBroadcast(it) || rec;
		// broadcast by R
		it = new Intent(IMC_ACTION + "-" + Utils.join(m.r));
		it.putExtra(BC_MSG, m);
		rec = lbm.sendBroadcast(it) || rec;
		if (rec) {// already received.
			return;
		}
		this.doNotify(m);
	}

	protected void doNotify(Msg m) {
		Notification nt = this.createNotify(m);
		if (nt == null) {
			return;
		}
		NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		nm.notify(NOTIFY_TAG, NOTIFY_ID, this.createNotify(m));
	}

	@Override
	public void run() {
		L.debug("running im service on thread:{},{}", Thread.currentThread().getId(), Thread.currentThread().getName());
		this.running = true;
		this.execing = true;
		this.begRun();
		while (this.running && this.execing) {
			try {
				this.running = this.run_();
			} catch (Exception e) {
				L.debug("try running err:", e);
			}
			// this.running = false;
		}
		this.endRun();
		this.running = false;
		this.execing = false;
		L.warn("background thread is stopped");
	}

	protected void checkStart() {
		if (!this.running) {
			this.start();
		}
	}

	protected abstract ImDb Db();

	public ApplicationInfo getAppInfo() {
		try {
			return this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String findMetaData(String key) {
		return this.findMetaData(key, null);
	}

	protected String findMetaData(String key, String defaultValue) {
		String val = this.info.metaData.getString(key, null);
		if (val == null) {
			ApplicationInfo ainfo = this.getAppInfo();
			if (defaultValue == null) {
				return ainfo.metaData.getString(key);
			} else {
				return ainfo.metaData.getString(key, defaultValue);
			}
		} else {
			return val;
		}
	}

	protected int findMetaDataI(String key) {
		return this.findMetaDataI(key, -1);
	}

	protected int findMetaDataI(String key, int defaultValue) {
		int val = this.info.metaData.getInt(key, -1);
		if (val == -1) {
			ApplicationInfo ainfo = this.getAppInfo();
			if (defaultValue == -1) {
				return ainfo.metaData.getInt(key);
			} else {
				return ainfo.metaData.getInt(key, defaultValue);
			}
		} else {
			return val;
		}
	}

	protected void create() {
		this.host = this.findMetaData("host");
		this.port = this.findMetaDataI("port");
		this.retry = this.findMetaDataI("retry", 8000);
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
			L.debug("start run ImSrv-->");
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

	public Con.Res li(Object v) throws Exception {
		return this.imc.li(v, Con.Res.class);
	}

	public Con.Res lo(Object v) throws Exception {
		return this.imc.lo(v, Con.Res.class);
	}

	public void ur() throws Exception {
		this.imc.ur();
	}

	public void ur(Map<String, Object> args) throws Exception {
		this.imc.ur(args);
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

	protected void close() throws IOException {
		this.execing = false;
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

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
