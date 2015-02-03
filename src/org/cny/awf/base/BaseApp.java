package org.cny.awf.base;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.cny.awf.er.ActType;
import org.cny.awf.er.CrashHandler;
import org.cny.awf.er.ER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Application;

public class BaseApp extends Application {
	public static final Map<String, Object> Kvs = new HashMap<String, Object>();
	private Logger L;

	public static void addKv(String key, Object val) {
		Kvs.put(key, val);
	}

	public static Object getKv(String key) {
		return Kvs.get(key);
	}

	@Override
	public void onCreate() {
		L = LoggerFactory.getLogger(this.getClass());
		super.onCreate();
		L.debug("running application on thread:{},{}", Thread.currentThread()
				.getId(), Thread.currentThread().getName());
		Thread.setDefaultUncaughtExceptionHandler(CrashHandler.instance());
		try {
			ER.init(this);
		} catch (FileNotFoundException e) {
			L.warn("the ER init err:", e);
		}
		ER.writem(this.getClass(), ER.ACT_IN, ActType.APP.getVal());
	}

	@Override
	public void onTerminate() {
		ER.writem(this.getClass(), ER.ACT_OUT, ActType.APP.getVal());
		try {
			ER.free();
		} catch (Exception e) {
			L.warn("the ER free err:", e);
		}
	}
}
