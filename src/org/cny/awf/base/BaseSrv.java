package org.cny.awf.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

public abstract class BaseSrv extends Service {
	protected final Logger L = LoggerFactory.getLogger(this.getClass());
	protected ServiceInfo info;

	@Override
	public void onCreate() {
		L.debug("running service on thread:{},{}", Thread.currentThread()
				.getId(), Thread.currentThread().getName());
		try {
			ComponentName cn = this.createComponentName();
			this.info = this.getPackageManager().getServiceInfo(cn,
					PackageManager.GET_META_DATA);
		} catch (Exception e) {
			L.error("on create service error:", e);
		}
	}

	protected ComponentName createComponentName() {
		return new ComponentName(this, this.getClass());
	}
}
