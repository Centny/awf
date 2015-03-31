package org.cny.awf.base;

import java.util.HashMap;
import java.util.Map;

import org.cny.awf.er.ActType;
import org.cny.awf.er.CrashHandler;
import org.cny.awf.er.ER;
import org.cny.awf.net.http.H;
import org.cny.awf.sr.SR;
import org.cny.awf.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class BaseApp extends Application {
	public static final Map<String, Object> Kvs = new HashMap<String, Object>();
	protected Logger L;
	protected ApplicationInfo info;

	public static void addKv(String key, Object val) {
		Kvs.put(key, val);
	}

	public static Object getKv(String key) {
		return Kvs.get(key);
	}

	protected void erInit() throws Exception {
		ER.init(this);
	}

	protected void erFree() throws Exception {
		ER.free();
	}

	protected void doSr() {
		String srSrv = this.info.metaData.getString("sr-srv", "");
		if (srSrv.isEmpty()) {
			return;
		}
		try {
			final PackageInfo pinfo = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0);
			final Map<String, String> dinfo = Util.DevInfo(this);
			new SR(this).dou(srSrv, new HashMap<String, String>() {
				private static final long serialVersionUID = 1L;

				{
					put("aid", pinfo.packageName);
					put("ver", pinfo.versionName);
					put("dev", dinfo.get("IMEI"));
					put("exec", "A");
					put("_hc_", "NO");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate() {
		H.CTX = this;
		// initial meta info
		// this.info = this.getApplicationInfo();
		try {
			this.info = this.getPackageManager().getApplicationInfo(
					this.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (this.info == null) {
			return;
		}
		// initial logger.
		try {
			SR.initSimpleLog(this,
					this.info.metaData.getBoolean("debug", false));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// initial the ER.
		try {
			this.erInit();
		} catch (Exception e) {
			L.warn("the ER init err:", e);
		}
		// CBase.ShowLog = true;
		// upload the SR data.
		this.doSr();
		// adding crash handler.
		Thread.setDefaultUncaughtExceptionHandler(CrashHandler.instance());
		//
		super.onCreate();
		L = LoggerFactory.getLogger(this.getClass());
		L.debug("running application on thread:{},{}", Thread.currentThread()
				.getId(), Thread.currentThread().getName());
		ER.writem(this.getClass(), ER.ACT_IN, ActType.APP.getVal());
	}

	@Override
	public void onTerminate() {
		try {
			this.erFree();
		} catch (Exception e) {
			L.warn("the ER free err:", e);
		}
		super.onTerminate();
		ER.writem(this.getClass(), ER.ACT_OUT, ActType.APP.getVal());
	}
}
