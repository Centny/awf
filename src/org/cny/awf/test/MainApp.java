package org.cny.awf.test;

import org.cny.awf.base.BaseApp;

public class MainApp extends BaseApp {
	@Override
	public void onCreate() {
		Runtime rt = Runtime.getRuntime();
		long maxMemory = rt.maxMemory();
		System.out.println(maxMemory / 1024 / 1024);
		// try {
		// ER.init(getBaseContext());
		// ER.writem("sfsdf", "A", 0);
		// ER.free();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// SR sr = new SR(this.getBaseContext());
		// sr.dob();
		// SR.initSimpleLog(getBaseContext(), true);
		super.onCreate();
		// sr.dou("");
	}
}
