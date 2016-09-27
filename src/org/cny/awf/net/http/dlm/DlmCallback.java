package org.cny.awf.net.http.dlm;

import org.cny.awf.net.http.HResp;

public interface DlmCallback {
	void onProcess(DlmC c, float speed, float rate);

	void onProcEnd(DlmC c, HResp res) throws Exception;

	void onSuccess(DlmC c, HResp res) throws Exception;

	void onError(DlmC c, Throwable err) throws Exception;

	void onExecErr(DlmC c, Throwable e);

	public static abstract class DlmBaseCallback implements DlmCallback {

		@Override
		public void onProcess(DlmC c, float speed, float rate) {
		}

		@Override
		public void onProcEnd(DlmC c, HResp res) throws Exception {
		}

		@Override
		public void onExecErr(DlmC c, Throwable e) {
			System.err.println("DlmC exec error->");
			e.printStackTrace();
		}

	}
}
