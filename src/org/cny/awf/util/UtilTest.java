package org.cny.awf.util;

import java.io.InputStream;

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
}
