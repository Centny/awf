package org.cny.awf.pool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.cny.awf.test.MainActivity;
import org.cny.jwf.util.Utils;

import android.test.ActivityInstrumentationTestCase2;

public class BitmapPoolTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public BitmapPoolTest() {
		super(MainActivity.class);
	}

	public void testBitmapPool() throws Exception {
		String[] ss = this.getActivity().getAssets().list("");
		for (String s : ss) {
			System.err.println(s);
		}
		InputStream is = this.getActivity().getResources().getAssets()
				.open("1.png");
		File tf1 = new File(this.getActivity().getExternalCacheDir(), "1.png");
		FileOutputStream fos = new FileOutputStream(tf1);
		// Utils.c
		Utils.copy(fos, is);
		fos.close();
		is = this.getActivity().getResources().getAssets().open("2.png");
		File tf2 = new File(this.getActivity().getExternalCacheDir(), "2.png");
		fos = new FileOutputStream(tf2);
		Utils.copy(fos, is);
		BitmapPool.init(1);
		BitmapPool.dol(tf1.getAbsolutePath(), 8);
		BitmapPool.dol(tf1.getAbsolutePath(), 8);
		BitmapPool.dol(tf2.getAbsolutePath(), 8);
		BitmapPool.dol(tf2.getAbsolutePath());
		BitmapPool.free();
		BitmapPool.dol(tf2.getAbsolutePath());
	}
}
