package org.cny.awf.test;

import org.cny.awf.base.BaseApp;

public class MainApp extends BaseApp {
	@Override
	public void onCreate() {
		{
			System.setProperty(
					org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
//			System.setProperty(org.slf4j.impl.SimpleLogger.LOG_FILE_KEY, this
//					.getExternalFilesDir("_log_").getAbsolutePath() + "/t.log");
		}
		super.onCreate();

	}
}
