package org.cny.awf.net;

import java.net.SocketException;

import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class NetInfoTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public NetInfoTest() {
		super(MainActivity.class);
	}

	public void testNw() throws SocketException {
		NetInfo ni = new NetInfo();
		ni.update(getActivity());
		System.err.println("sss->");
	}
}
