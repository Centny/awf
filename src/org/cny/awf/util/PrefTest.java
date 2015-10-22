package org.cny.awf.util;

import junit.framework.Assert;

import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class PrefTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public PrefTest() {
		super(MainActivity.class);
	}

	public void testGetSet() {
		Pref.CTX = this.getActivity();
		Pref.set("b", true);
		Pref.set("i", 1);
		Pref.set("l", 1l);
		Pref.set("f", 1f);
		Pref.set("s", "1");
		Assert.assertEquals(true, Pref.boolv("b"));
		Assert.assertEquals(1, Pref.intv("i"));
		Assert.assertEquals(1l, Pref.longv("l"));
		Assert.assertEquals(1f, Pref.floatv("f"));
		Assert.assertEquals("1", Pref.strv("s"));
	}
}
