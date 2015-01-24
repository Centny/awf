package org.cny.awf.net.http;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public interface HCallback {

	OutputStream createO(CBase c, HResp res) throws Exception;

	void onProcess(CBase c, float rate);

	void onProcEnd(CBase c, HResp res, OutputStream o) throws Exception;

	void onSuccess(CBase c, HResp res) throws Exception;

	void onError(CBase c, Throwable err) throws Exception;

	public static abstract class HDataCallback implements HCallback {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();

		@Override
		public OutputStream createO(CBase c, HResp res) throws Exception {
			this.buf.reset();
			return this.buf;
		}

		@Override
		public void onProcess(CBase c, float rate) {

		}

		@Override
		public void onProcEnd(CBase c, HResp res, OutputStream o)
				throws Exception {
		}

		@Override
		public void onSuccess(CBase c, HResp res) throws Exception {
			this.onSuccess(c, res, new String(this.buf.toByteArray(), res.enc));
		}

		public abstract void onSuccess(CBase c, HResp res, String data)
				throws Exception;
	}

	public static abstract class HCacheCallback extends HDataCallback {

		@Override
		public void onError(CBase c, Throwable err) throws Exception {
			this.onError(c, c.readCache(), err);
		}

		public abstract void onError(CBase c, String cache, Throwable err)
				throws Exception;
	}
}
