package org.cny.awf.net.http;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.google.gson.Gson;

public interface HCallback {
	void onProcess(CBase c, PIS pis, float rate);

	OutputStream createO(CBase c, HResp res) throws Exception;

	void onProcess(CBase c, float rate);

	void onProcEnd(CBase c, HResp res, OutputStream o) throws Exception;

	void onSuccess(CBase c, HResp res) throws Exception;

	void onError(CBase c, Throwable err) throws Exception;

	public static abstract class HDataCallback implements HCallback {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();

		@Override
		public void onProcess(CBase c, PIS pis, float rate) {

		}

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

	public abstract class GDataCallback<T> extends HDataCallback {
		protected Class<?> cls;
		protected Gson gs = new Gson();

		public GDataCallback(Class<?> cls) {
			this.cls = cls;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			if (data == null || data.isEmpty()) {
				this.onSuccess(c, res, (T) null);
			} else {
				this.onSuccess(c, res, (T) this.gs.fromJson(data, this.cls));
			}
		}

		public abstract void onSuccess(CBase c, HResp res, T data)
				throws Exception;
	}

	public abstract class GCacheCallback<T> extends HCacheCallback {
		protected Class<?> cls;
		protected Gson gs = new Gson();

		public GCacheCallback(Class<?> cls) {
			this.cls = cls;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onError(CBase c, String cache, Throwable err)
				throws Exception {
			if (cache == null || cache.isEmpty()) {
				this.onError(c, (T) null, err);
			} else {
				this.onError(c, (T) this.gs.fromJson(cache, this.cls), err);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			if (data == null || data.isEmpty()) {
				this.onSuccess(c, res, (T) null);
			} else {
				this.onSuccess(c, res, (T) this.gs.fromJson(data, this.cls));
			}
		}

		public abstract void onError(CBase c, T cache, Throwable err)
				throws Exception;

		public abstract void onSuccess(CBase c, HResp res, T data)
				throws Exception;
	}

}
