package org.cny.awf.net.http.dlm;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.message.BasicNameValuePair;
import org.cny.awf.net.http.CBase.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

public class DLM extends ThreadPoolExecutor {
	private static Logger L = LoggerFactory.getLogger(DLM.class);
	private int idc = 0;
	protected DlmQueue queue;

	public DLM(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new DlmQueue(100));
		this.queue = (DlmQueue) this.getQueue();
	}

	public synchronized String put(Context ctx, String url, String method, String spath, List<BasicNameValuePair> args,
			List<BasicNameValuePair> heads, DlmCallback cback) {
		DlmHCallback cb = new DlmHCallback(this, cback);
		DlmC c = new DlmC(ctx, url, spath, cb);
		c.id = "C_" + idc++;
		cb.c = c;
		c.dlm = this;
		c.setMethod(method);
		c.addArg("_hc_", Policy.NO.toString());
		if (new File(c.spath).exists() && !new File(c.tpath).exists()) {
			throw new RuntimeException("having file on loc->" + spath);
		}
		if (args != null) {
			for (BasicNameValuePair arg : args) {
				c.addArg(arg.getName(), arg.getValue());
			}
		}
		if (heads != null) {
			for (BasicNameValuePair head : heads) {
				c.addHead(head.getName(), head.getValue());
			}
		}
		String turl = c.getFullUrl();
		if (this.queue.isExistUrl(turl)) {
			throw new RuntimeException("having task for url->" + turl);
		}
		if (this.queue.isExistLoc(c.spath)) {
			throw new RuntimeException("having task using local file->" + turl);
		}
		L.info("add task({}) to poll by url->{}", c.id, c.getFullUrl());
		this.execute(c);
		return c.id;
	}

	public synchronized void poll(String id) {
		DlmC c = (DlmC) this.queue.find(id);
		if (c == null) {
			return;
		}
		if (c.isRunning()) {
			c.interrupt();
			L.info("interrupt task({}) for url->{}", c.id, c.getFullUrl());
		}
	}

	public DlmC find(Object key) {
		return (DlmC) this.queue.find(key);
	}

	public boolean isExistUrl(String key) {
		return this.queue.isExistUrl(key);
	}

	public boolean isExistLoc(String key) {
		return this.queue.isExistLoc(key);
	}
}
