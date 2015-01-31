package org.cny.awf.net;

import java.net.SocketException;

import org.cny.awf.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetInfo {
	private static final Logger L = LoggerFactory.getLogger(NetInfo.class);
	public static final int NT_2G = 10;
	public static final int NT_3G = 11;
	public static final int NT_4G = 12;
	public static final int NT_WF = 13;
	public static final int NT_OTHER = 14;

	private boolean available;
	private int ntype;
	private int type;
	private int subtype;
	private String ip = "";

	public boolean isAvailable() {
		return available;
	}

	public int getNtype() {
		return ntype;
	}

	public int getType() {
		return type;
	}

	public int getSubtype() {
		return subtype;
	}

	public String getIp() {
		return ip;
	}

	public void update(Context ctx) throws SocketException {
		L.debug("updating the network info");
		ConnectivityManager conm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conm.getActiveNetworkInfo();
		if (info == null) {
			this.available = false;
			return;
		}
		this.available = info.isAvailable();
		if (!this.available) {
			return;
		}
		this.type = info.getType();
		this.subtype = info.getSubtype();
		this.ip = Util.localIpAddress(ctx, false);
		if (this.type != ConnectivityManager.TYPE_WIFI) {
			this.ntype = NT_WF;
			return;
		}
		switch (this.subtype) {
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_GPRS:
			this.ntype = NT_2G;
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case TelephonyManager.NETWORK_TYPE_HSPAP:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			this.ntype = NT_3G;
			break;
		case TelephonyManager.NETWORK_TYPE_LTE:
			this.ntype = NT_4G;
			break;
		default:
			this.ntype = NT_OTHER;
			break;
		}
	}

	private static NetInfo NET_;

	public static NetInfo net() {
		if (NET_ == null) {
			NET_ = new NetInfo();
		}
		return NET_;
	}
}
