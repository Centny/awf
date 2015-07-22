package org.cny.awf.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

public abstract class BaseSrv extends Service {
	private static final Logger L = LoggerFactory.getLogger(BaseSrv.class);
	protected ServiceInfo info;

	@Override
	public void onCreate() {
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

	public ServiceInfo getInfo() {
		return info;
	}

}
