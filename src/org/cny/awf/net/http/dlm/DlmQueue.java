package org.cny.awf.net.http.dlm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DlmQueue {
	private static Logger L = LoggerFactory.getLogger(DlmQueue.class);
	protected Map<String, DlmC> cs = new HashMap<String, DlmC>();
	protected Set<String> cs_url = new HashSet<String>();
	protected Set<String> cs_loc = new HashSet<String>();

	public void add(DlmC c) {
		String id = c.id;
		L.debug("add task({}) to queue", id);
		this.cs.put(id, c);
		this.cs_url.add(c.getFullUrl());
		this.cs_loc.add(c.spath);
	}

	public void del(DlmC c) {
		String id = c.id;
		L.debug("remove task({}) from queue", id);
		this.cs.remove(id);
		this.cs_url.remove(c.getFullUrl());
		this.cs_loc.remove(c.spath);
	}

	public DlmC find(String key) {
		return this.cs.get(key);
	}

	public boolean isExistUrl(String key) {
		return this.cs_url.contains(key);
	}

	public boolean isExistLoc(String key) {
		return this.cs_loc.contains(key);
	}

	public int size() {
		return this.cs.size();
	}
}
