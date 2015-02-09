package org.cny.awf.pool;

import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class BitmapPoolTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public BitmapPoolTest() {
		super(MainActivity.class);
	}

	public void testBitmapPool() throws Exception {
		BitmapPool.init(1);
		BitmapPool.dol("file:///android_asset/1.png", 8);
		BitmapPool.dol("file:///android_asset/1.png", 8);
		BitmapPool.dol("file:///android_asset/2.png", 8);
		BitmapPool.dol("file:///android_asset/2.png");
		BitmapPool.free();
		BitmapPool.dol("file:///android_asset/2.png");
	}
}
