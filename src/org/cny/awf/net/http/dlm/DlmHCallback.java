package org.cny.awf.net.http.dlm;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.CBase.Policy;
import org.cny.awf.net.http.HCallback;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.PIS;

public class DlmHCallback implements HCallback {
	protected DLM dlm;
	protected DlmC c;
	protected DlmCallback cback;
	protected long lastProc;

	public DlmHCallback(DLM dlm, DlmCallback cback) {
		super();
		this.dlm = dlm;
		this.cback = cback;
		this.lastProc = 0;
	}

	@Override
	public void onCreateR(CBase c, HResp res, Policy pc) throws Exception {

	}

	@Override
	public void onCache(CBase c, HResp res) throws Exception {

	}

	@Override
	public void onProcess(CBase c, PIS pis, float rate) {

	}

	@Override
	public OutputStream createO(CBase c, HResp res) throws Exception {
		return new ByteArrayOutputStream();
	}

	@Override
	public void onProcess(CBase c, long clen, long rsize, long perid) {
		float rate = 0, speed = 0;
		if (clen > 0) {
			rate = (float) rsize / (float) clen;
		}
		long now = new Date().getTime();
		if (this.lastProc > 0 & (now - this.lastProc) > 0) {
			speed = (float) perid / (float) (now - this.lastProc);
		}
		this.cback.onProcess(this.c, speed, rate);
		this.lastProc = now;
	}

	@Override
	public void onProcEnd(CBase c, HResp res, OutputStream o) throws Exception {
		o.close();
	}

	@Override
	public void onSuccess(CBase c, HResp res) throws Exception {
		this.dlm.queue.del(this.c);
		this.cback.onSuccess(this.c, res);
	}

	@Override
	public void onError(CBase c, Throwable err) throws Exception {
		this.dlm.queue.del(this.c);
		this.cback.onError(this.c, err);
	}

	@Override
	public void onExecErr(CBase c, Throwable e) {
		this.dlm.queue.del(this.c);
		this.cback.onExecErr(this.c, e);
	}

	public DlmC getC() {
		return c;
	}

	public void setC(DlmC c) {
		this.c = c;
	}

	public DlmCallback getCback() {
		return cback;
	}

}
