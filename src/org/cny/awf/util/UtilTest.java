package org.cny.awf.util;

import java.io.InputStream;

import junit.framework.Assert;

import org.cny.awf.test.MainActivity;
import org.cny.awf.test.R;

import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

public class UtilTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public UtilTest() {
		super(MainActivity.class);
	}

	public void testFindResUri() throws Exception {
		Uri uri = Util.findResUri(getActivity(), R.drawable.ic_launcher);
		System.err.println(uri);
		InputStream in = this.getActivity().getContentResolver()
				.openInputStream(uri);
		assertNotNull(in);
		in.close();
	}

	public void testFindRId() throws ClassNotFoundException {
		System.err.println(Class.forName("org.cny.awf.test.R$id"));
		Assert.assertEquals(R.id.imgl,
				Util.findRId("org.cny.awf.test.R.id.imgl"));
		Assert.assertEquals(R.drawable.good,
				Util.findRId("org.cny.awf.test.R.drawable.good"));
	}
}
