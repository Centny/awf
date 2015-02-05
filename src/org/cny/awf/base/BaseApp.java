package org.cny.awf.base;

import java.util.HashMap;
import java.util.Map;

import org.cny.awf.er.ActType;
import org.cny.awf.er.CrashHandler;
import org.cny.awf.er.ER;
import org.cny.awf.net.http.H;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Application;

public class BaseApp extends Application {
	public static final Map<String, Object> Kvs = new HashMap<String, Object>();
	protected Logger L;

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

	@Override
	public void onCreate() {
		H.CTX = this;
		L = LoggerFactory.getLogger(this.getClass());
		L.debug("running application on thread:{},{}", Thread.currentThread()
				.getId(), Thread.currentThread().getName());
		Thread.setDefaultUncaughtExceptionHandler(CrashHandler.instance());
		try {
			this.erInit();
		} catch (Exception e) {
			L.warn("the ER init err:", e);
		}
		ER.writem(this.getClass(), ER.ACT_IN, ActType.APP.getVal());
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		ER.writem(this.getClass(), ER.ACT_OUT, ActType.APP.getVal());
		try {
			this.erFree();
		} catch (Exception e) {
			L.warn("the ER free err:", e);
		}
		super.onTerminate();
	}
}
