package org.cny.awf.base;

import junit.framework.Assert;

import org.cny.awf.test.MainActivity;
import org.slf4j.LoggerFactory;

import android.test.ActivityInstrumentationTestCase2;

public class BaseTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public BaseTest() {
		super(MainActivity.class);
	}

	public void testBaseApp() {
		BaseApp.addKv("a", "v");
		Assert.assertEquals("v", BaseApp.getKv("a").toString());

		BaseApp ba = new BaseApp() {

			@Override
			protected void erInit() throws Exception {
				throw new Exception();
			}

			@Override
			protected void erFree() throws Exception {
				throw new Exception();
			}

		};
		ba.onCreate();
		ba.onTerminate();
		new BaseApp() {
			{
				L = LoggerFactory.getLogger("abc");
			}
		}.onTerminate();
		new BaseAty() {

		}.onClkRet(null);
		;
	}
}
