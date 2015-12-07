package org.cny.awf.net.http.dlm;

import org.cny.awf.net.http.HResp;

public interface DlmCallback {
	void onProcess(DlmC c, float rate);

	void onProcEnd(DlmC c, HResp res) throws Exception;

	void onSuccess(DlmC c, HResp res) throws Exception;

	void onError(DlmC c, Throwable err) throws Exception;

	void onExecErr(DlmC c, Throwable e);
}
