package org.cny.awf.er;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;

import org.cny.jwf.util.Utils;

public class CrashHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread thr, Throwable e) {
		e.printStackTrace();
		Map<String, Object> kvs = new HashMap<String, Object>();
		kvs.put("thr_id", thr.getId());
		kvs.put("thr_name", thr.getName());
		kvs.put("thr_priority", thr.getPriority());
		kvs.put("thr_stack", Utils.stack(thr.getStackTrace()));
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		kvs.put("err_stack", sw.toString());
		ER.writem(thr.getClass(), ER.CRASH, ActType.APP.getVal(), kvs);
		try {
			ER.free();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private static CrashHandler CH_ = null;

	public static CrashHandler instance() {
		if (CH_ == null) {
			CH_ = new CrashHandler();
		}
		return CH_;
	}
}