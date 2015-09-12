package org.cny.awf.net.http;

import junit.framework.Assert;

import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class CBaseTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public CBaseTest() {
		super(MainActivity.class);
	}

	public void testParseUrl() {
		Assert.assertEquals("http://ww.baidu.com",
				CBase.parseUrl("http://ww.baidu.com"));
		Assert.assertEquals("http://ww.baidu.com",
				CBase.parseUrl("http://ww.baidu.com?"));
		Assert.assertEquals("http://ww.baidu.com?a=1",
				CBase.parseUrl("http://ww.baidu.com?a=1"));
		Assert.assertEquals("http://ww.baidu.com?a=1&b=2",
				CBase.parseUrl("http://ww.baidu.com?b=2&a=1"));
	}
}
