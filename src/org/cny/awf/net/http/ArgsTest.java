package org.cny.awf.net.http;

import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class ArgsTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public ArgsTest() {
		super(MainActivity.class);
	}

	public void testArgs() {
		System.err.println(Args.A("a", "1").A("b", "2").J("c", new Object())
				.toString());
		new Args();
		Args.J("ab", null);
	}

	public void testJsonArgs() {
		Args.V args = Args.A("a", "123").A("b", "xxx");
		System.out.println(args.toString());
		System.out.println(args.asAry().toString());
	}
}
