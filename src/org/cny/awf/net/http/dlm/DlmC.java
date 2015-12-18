package org.cny.awf.net.http.dlm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.http.client.methods.HttpUriRequest;
import org.cny.awf.net.http.C;
import org.cny.awf.net.http.HCallback;
import org.cny.awf.net.http.HResp;
import org.cny.awf.util.MultiOutputStream;
import org.cny.awf.util.RAFOutputStream;
import org.cny.jwf.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

public class DlmC extends C {
	private static Logger L = LoggerFactory.getLogger(DlmC.class);

	public String id;
	protected String spath;
	protected String tpath;
	protected DlmTmp tmp;
	protected DLM dlm;
	protected long loaded = 0;

	public DlmC(Context ctx, String url, String spath, HCallback cback) {
		super(ctx, url, cback);
		this.spath = spath;
		this.tpath = spath + ".awf.tmp";
	}

	@Override
	protected void exec() throws Exception {
		try {
			super.exec();
		} catch (Exception e) {
			throw e;
		} finally {
			this.dlm.queue.del(this);
		}
	}

	protected DlmTmp readTmp() {
		return DlmTmp.read(this.tpath);
	}

	protected void writeTmp(DlmTmp t) throws Exception {
		t.write(this.tpath);
	}

	protected void saveTmp() {
		try {
			this.writeTmp(this.tmp);
		} catch (Exception e) {
			L.warn("save DCTmp to {} err->{}", this.spath, e.getMessage());
		}
	}

	@Override
	protected HttpUriRequest createR() throws Exception {
		this.tmp = this.readTmp();
		String furl = this.getFullUrl();
		if (this.tmp == null) {
			this.tmp = new DlmTmp(furl, 0);
		}
		this.loaded = this.tmp.range;
		if (!furl.equals(this.tmp.url)) {
			throw new Exception("url is not equal to saved awf.tmp.url, must set new file path to new url");
		}
		L.debug("do task({}) by range->{}", this.id, this.tmp.range);
		this.addHead("Range", " bytes=" + this.tmp.range + "-");
		return super.createR();
	}

	@SuppressWarnings("resource")
	@Override
	protected MultiOutputStream createO(HResp res, Policy pc) throws Exception {
		OutputStream tout = this.cback.createO(this, res);
		tout.write(this.spath.getBytes());
		FileOutputStream fos;
		int code = res.getCode();
		L.debug("do task({}) by status code->{}", this.id, code);
		if (code == 206) {
			RandomAccessFile raf = new RandomAccessFile(this.spath, "rw");
			raf.seek(res.getRg_beg());
			fos = new RAFOutputStream(raf);
		} else {
			this.tmp.range = 0;
			fos = new FileOutputStream(this.spath);
		}
		this.saveTmp();
		return new MultiOutputStream(fos, tout).mark(1, false);
	}

	@Override
	protected void onProcess(HResp res, long rsize, long clen) {
		this.tmp.range = this.loaded + rsize;
		this.saveTmp();
		if (res.getRg_len() > 0) {
			super.onProcess(res, this.tmp.range, res.getRg_len());
		} else {
			super.onProcess(res, rsize, clen);
		}
	}

	@Override
	protected void onSuccess(HResp res) throws Exception {
		super.onSuccess(res);
		Utils.del(new File(this.tpath));
	}

	public void interrupt() {
		this.running = false;
	}

	public String getSpath() {
		return spath;
	}

	public String getTpath() {
		return tpath;
	}

	public DlmTmp getTmp() {
		return tmp;
	}

	public DLM getDlm() {
		return dlm;
	}

}