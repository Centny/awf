package org.cny.awf.net.http.dlm;

import java.io.File;

import org.cny.awf.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class DlmTmp {
	private static Logger L = LoggerFactory.getLogger(DlmTmp.class);
	public String url;
	public long range;

	public DlmTmp() {
	}

	public DlmTmp(String url, long range) {
		super();
		this.url = url;
		this.range = range;
	}

	public static DlmTmp read(String f) {
		try {
			String data = Util.readAll(new File(f));
			return new Gson().fromJson(data, DlmTmp.class);
		} catch (Exception e) {
			L.debug("read DCTmp from {} err->{}", f, e.getMessage());
			return null;
		}
	}

	public void write(String f) throws Exception {
		String data = new Gson().toJson(this);
		Util.write(new File(f), data.getBytes());
	}
}
