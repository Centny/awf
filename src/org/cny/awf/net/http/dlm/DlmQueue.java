package org.cny.awf.net.http.dlm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DlmQueue extends LinkedBlockingQueue<Runnable> {
	private static Logger L = LoggerFactory.getLogger(DlmQueue.class);
	protected Map<String, Runnable> cs = new HashMap<String, Runnable>();
	protected Set<String> cs_url = new HashSet<String>();
	protected Set<String> cs_loc = new HashSet<String>();
	private static final long serialVersionUID = 7930207852923527667L;

	public DlmQueue(int capacity) {
		super(capacity);
	}

	@Override
	public boolean add(Runnable e) {
		DlmC c = (DlmC) e;
		String id = c.id;
		L.debug("add task({}) to queue", id);
		this.cs.put(id, e);
		this.cs_url.add(c.getFullUrl());
		this.cs_loc.add(c.spath);
		return super.add(e);
	}

	@Override
	public boolean remove(Object o) {
		DlmC c = (DlmC) o;
		String id = c.id;
		L.debug("remove task({}) from queue", id);
		this.cs.remove(id);
		this.cs_url.remove(c.getFullUrl());
		this.cs_loc.remove(c.spath);
		return super.remove(o);
	}

	public Runnable find(Object key) {
		return this.cs.get(key);
	}

	public boolean isExistUrl(String key) {
		return this.cs_url.contains(key);
	}

	public boolean isExistLoc(String key) {
		return this.cs_loc.contains(key);
	}
}
