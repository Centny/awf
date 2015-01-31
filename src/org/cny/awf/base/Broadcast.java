package org.cny.awf.base;

import org.cny.awf.net.NetInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class Broadcast extends BroadcastReceiver {
	private static final Logger L = LoggerFactory.getLogger(NetInfo.class);

	@Override
	public void onReceive(Context ctx, Intent it) {
		String action = it.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			this.doConAction(ctx, it);
		}
	}

	protected void doConAction(Context ctx, Intent it) {
		try {
			NetInfo.net().update(ctx);
		} catch (Exception e) {
			L.error("doing connect action err:", e);
		}
	}
}
