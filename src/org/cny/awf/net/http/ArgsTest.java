package org.cny.awf.net.http;

import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class ArgsTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public ArgsTest() {
		super(MainActivity.class);
	}

	public void testArgs() {
		System.err
				.println(Args.A("a", "1").A("b", "2").J("c", new Object()).Args
						.toString());
	}
}
