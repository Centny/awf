package org.cny.awf.util;

import junit.framework.Assert;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastAssert extends BroadcastReceiver {
	protected int times = 0;
	protected BroadcastReceiver brec;

	public BroadcastAssert() {
		super();
	}

	public BroadcastAssert(BroadcastReceiver brec) {
		super();
		this.brec = brec;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		times++;
		if (this.brec != null) {
			this.brec.onReceive(context, intent);
		}
	}

	public void except(int t) {
		Assert.assertEquals(t, this.times);
	}

	public void more(int t) {
		Assert.assertTrue(this.times > t);
	}

	public void less(int t) {
		Assert.assertTrue(this.times < t);
	}

}
