package org.cny.awf.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.cny.awf.test.MainActivity;
import org.cny.awf.test.R;
import org.cny.jwf.util.Utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import junit.framework.Assert;

public class UtilTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public UtilTest() {
		super(MainActivity.class);
	}

	public void testFindResUri() throws Exception {
		Uri uri = Util.findResUri(getActivity(), R.drawable.ic_launcher);
		System.err.println(uri);
		InputStream in = this.getActivity().getContentResolver().openInputStream(uri);
		assertNotNull(in);
		in.close();
	}

	public void testFindRId() throws ClassNotFoundException {
		System.err.println(Class.forName("org.cny.awf.test.R$id"));
		Assert.assertEquals(R.id.imgl, Util.findRId("org.cny.awf.test.R.id.imgl"));
		Assert.assertEquals(R.drawable.good, Util.findRId("org.cny.awf.test.R.drawable.good"));
	}

	public void testReadBitmap() throws Exception {
		Uri uri = Util.findResUri(getActivity(), R.drawable.ic_launcher);
		System.err.println(uri);
		File dir = this.getActivity().getExternalFilesDir("xxx");
		File tf = new File(dir, "abc.png");
		FileOutputStream fos = new FileOutputStream(tf);
		InputStream in = this.getActivity().getContentResolver().openInputStream(uri);
		assertNotNull(in);
		Utils.copy(fos, in);
		in.close();
		fos.close();
		Bitmap bimg, timg;
		bimg = Util.readBitmap(tf.getAbsolutePath(), 0, 0, -1, -1);
		Assert.assertNotNull(bimg);
		//
		timg = Util.readBitmap(tf.getAbsolutePath(), 5, 5, -1, -1);
		Assert.assertNotNull(timg);
		Assert.assertTrue(timg.getWidth() < 6 && timg.getHeight() < 6);
		//
		timg = Util.readBitmap(tf.getAbsolutePath(), 0, 0, 8, -1);
		Assert.assertNotNull(timg);
		System.err.println(timg.getWidth() + "->" + timg.getHeight());
		Assert.assertTrue(timg.getWidth() < 9 && timg.getHeight() < 9);
		//
		timg = Util.readBitmap(tf.getAbsolutePath(), 0, 0, -1, 8);
		Assert.assertNotNull(timg);
		System.err.println(timg.getWidth() + "->" + timg.getHeight());
		Assert.assertTrue(timg.getWidth() < 9 && timg.getHeight() < 9);
	}
}
