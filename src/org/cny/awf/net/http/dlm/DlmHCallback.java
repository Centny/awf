package org.cny.awf.net.http.dlm;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.CBase.Policy;
import org.cny.awf.net.http.HCallback;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.PIS;

public class DlmHCallback implements HCallback {
	protected DLM dlm;
	protected DlmC c;
	protected DlmCallback cback;

	public DlmHCallback(DLM dlm, DlmCallback cback) {
		super();
		this.dlm = dlm;
		this.cback = cback;
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
	public void onProcess(CBase c, float rate) {
		this.cback.onProcess(this.c, rate);
	}

	@Override
	public void onProcEnd(CBase c, HResp res, OutputStream o) throws Exception {
		o.close();
	}

	@Override
	public void onSuccess(CBase c, HResp res) throws Exception {
		this.cback.onSuccess(this.c, res);
	}

	@Override
	public void onError(CBase c, Throwable err) throws Exception {
		this.cback.onError(this.c, err);
	}

	@Override
	public void onExecErr(CBase c, Throwable e) {
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
