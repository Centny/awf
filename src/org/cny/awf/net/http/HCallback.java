package org.cny.awf.net.http;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

import org.cny.awf.net.http.CBase.Policy;
import org.cny.awf.pool.BitmapPool;
import org.cny.awf.pool.BitmapPool.UrlKey;
import org.cny.awf.util.Util;
import org.cny.jwf.hook.Hooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public interface HCallback {
	void onCreateR(CBase c, HResp res, Policy pc) throws Exception;

	void onCache(CBase c, HResp res) throws Exception;

	void onProcess(CBase c, PIS pis, float rate);

	OutputStream createO(CBase c, HResp res) throws Exception;

	void onProcess(CBase c, float rate);

	void onProcEnd(CBase c, HResp res, OutputStream o) throws Exception;

	void onSuccess(CBase c, HResp res) throws Exception;

	void onError(CBase c, Throwable err) throws Exception;

	void onExecErr(CBase c, Throwable e);

	public static abstract class HDataCallback implements HCallback {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		protected String tdata;

		@Override
		public void onCache(CBase c, HResp res) throws Exception {

		}

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
			this.tdata = new String(this.buf.toByteArray(), res.enc);
			// sending hook
			if (Hooks
					.call(HDataCallback.class, "onSuccess", c, res, this.tdata) < 1) {
				this.onSuccess(c, res, this.tdata);
			}
		}

		@Override
		public void onExecErr(CBase c, Throwable e) {
			e.printStackTrace();
		}

		public abstract void onSuccess(CBase c, HResp res, String data)
				throws Exception;

		public String rdata() {
			return this.tdata;
		}

		@Override
		public void onCreateR(CBase c, HResp res, Policy pc) throws Exception {

		}

	}

	public static abstract class HCacheCallback extends HDataCallback {

		@Override
		public void onError(CBase c, Throwable err) throws Exception {
			String cache = c.readCache();
			if (Hooks.call(HCacheCallback.class, "onError", c, cache, err) < 1) {
				this.onError(c, cache, err);
			}
		}

		public abstract void onError(CBase c, String cache, Throwable err)
				throws Exception;
	}

	public static abstract class HBitmapCallback extends HCacheCallback {
		protected int roundCorner = 0;

		public HBitmapCallback() {
			super();
		}

		public HBitmapCallback(int roundCorner) {
			super();
			this.roundCorner = roundCorner;
		}

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			this.onSuccess(c, res, BitmapPool.dol(new UrlKey(c.getUrl(), data),
					this.roundCorner));
		}

		@Override
		public void onError(CBase c, String cache, Throwable err)
				throws Exception {
			Bitmap img = null;
			if (Util.isNoEmpty(cache)) {
				img = BitmapPool.dol(new UrlKey(c.getUrl(), cache),
						this.roundCorner);
			}
			this.onError(c, img, err);
		}

		public abstract void onSuccess(CBase c, HResp res, Bitmap img)
				throws Exception;

		public abstract void onError(CBase c, Bitmap cache, Throwable err)
				throws Exception;
	}

	public abstract class GDataCallback<T> extends HDataCallback {
		protected Class<?> cls;
		protected Gson gs = new Gson();

		public GDataCallback(Class<?> cls) {
			this.cls = cls;
		}

		@SuppressWarnings("unchecked")
		protected T toT(String data) {
			T val;
			if (data == null || data.isEmpty()) {
				val = null;
			} else {
				val = (T) this.gs.fromJson(data.trim(), this.cls);
			}
			return val;
		}

		@Override
		public void onCache(CBase c, HResp res) throws Exception {
			String data = c.readCahce(res);
			T val = this.toT(data);
			this.onCache(c, res, val);
		}

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			T val = this.toT(data);
			if (Hooks.call(HCacheCallback.class, "onSuccess", c, res, val) < 1) {
				this.onSuccess(c, res, val);
			}
		}

		public void onCache(CBase c, HResp res, T data) {

		}

		public abstract void onSuccess(CBase c, HResp res, T data)
				throws Exception;
	}

	public class GDataCallbackS<T> extends GDataCallback<T> {
		public Throwable err;
		public T data;

		public GDataCallbackS(Class<?> cls) {
			super(cls);
		}

		@Override
		public void onError(CBase c, Throwable err) throws Exception {
			this.err = err;
		}

		@Override
		public void onSuccess(CBase c, HResp res, T data) throws Exception {
			this.data = data;
		}

	}

	public abstract class GCacheCallback<T> extends HCacheCallback {
		protected Class<?> cls;
		protected Gson gs = new Gson();

		public GCacheCallback(Class<?> cls) {
			this.cls = cls;
		}

		protected T toT(String data) {
			return toT(data, true);
		}

		@SuppressWarnings("unchecked")
		protected T toT(String data, boolean err) {
			if (data == null || data.isEmpty()) {
				return null;
			}
			try {
				return (T) this.gs.fromJson(data.trim(), this.cls);
			} catch (RuntimeException e) {
				if (err) {
					throw new RuntimeException(data, e);
				} else {
					return null;
				}
			}
		}

		@Override
		public void onCache(CBase c, HResp res) throws Exception {
			String data = c.readCahce(res);
			T val = this.toT(data);
			this.onCache(c, res, val);
		}

		@Override
		public void onError(CBase c, String cache, Throwable err)
				throws Exception {
			T val = this.toT(cache, false);
			if (Hooks.call(HCacheCallback.class, "onError", c, val, err) < 1) {
				this.onError(c, val, err);
			}
		}

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			T val = this.toT(data);
			if (Hooks.call(HCacheCallback.class, "onSuccess", c, res, val) < 1) {
				this.onSuccess(c, res, val);
			}
		}

		public void onCache(CBase c, HResp res, T data) {

		}

		public abstract void onError(CBase c, T cache, Throwable err)
				throws Exception;

		public abstract void onSuccess(CBase c, HResp res, T data)
				throws Exception;
	}

	public class GCacheCallbackS<T> extends GCacheCallback<T> {

		public Throwable err;
		public T data;

		public GCacheCallbackS(Class<?> cls) {
			super(cls);
		}

		@Override
		public void onError(CBase c, T cache, Throwable err) throws Exception {
			this.data = cache;
			this.err = err;
		}

		@Override
		public void onSuccess(CBase c, HResp res, T data) throws Exception {
			this.data = data;
		}

	}

	public abstract class GMapCallback extends
			GCacheCallback<Map<String, Object>> {

		public GMapCallback() {
			super(Map.class);
		}

		@Override
		protected Map<String, Object> toT(String data, boolean err) {
			return this.gs.fromJson(data, new TypeToken<Map<String, String>>() {
			}.getType());
		}
	}

	public static class HandlerCallback implements HCallback {
		private static final Logger L = LoggerFactory
				.getLogger(HandlerCallback.class);

		public static int vvv = 0;
		protected static Handler H = new Handler() {

			@Override
			public void dispatchMessage(Message msg) {
				CBase c = null;
				try {
					Object[] args = (Object[]) msg.obj;
					HCallback tg = (HCallback) args[0];
					c = (CBase) args[1];
					switch (msg.what) {
					case 0:
						tg.onProcess(c, (PIS) args[2], (Float) args[3]);
						break;
					case 1:
						tg.onProcess(c, (Float) args[2]);
						break;
					case 2:
						tg.onSuccess(c, (HResp) args[2]);
						break;
					case 3:
						tg.onError(c, (Throwable) args[2]);
						break;
					case 4:
						tg.onCache(c, (HResp) args[2]);
						break;
					case 5:
						tg.onExecErr(c, (Throwable) args[2]);
						break;
					default:
						throw new Exception("invalid message type for"
								+ msg.what);
					}
				} catch (Exception e) {
					L.warn("exec H({}) HCallback({}) err", c, msg.what, e);
				}
			}

		};

		protected HCallback target;

		public HandlerCallback(HCallback target) {
			this.target = target;
		}

		@Override
		public void onProcess(CBase c, PIS pis, float rate) {
			Message msg = new Message();
			msg.what = 0;
			msg.obj = new Object[] { this.target, c, pis, (Float) rate };
			H.sendMessage(msg);
		}

		@Override
		public void onProcess(CBase c, float rate) {
			Message msg = new Message();
			msg.what = 1;
			msg.obj = new Object[] { this.target, c, (Float) rate };
			H.sendMessage(msg);
		}

		@Override
		public void onSuccess(CBase c, HResp res) throws Exception {
			Message msg = new Message();
			msg.what = 2;
			msg.obj = new Object[] { this.target, c, res };
			H.sendMessage(msg);
		}

		@Override
		public void onError(CBase c, Throwable err) throws Exception {
			Message msg = new Message();
			msg.what = 3;
			msg.obj = new Object[] { this.target, c, err };
			H.sendMessage(msg);
		}

		@Override
		public OutputStream createO(CBase c, HResp res) throws Exception {
			return this.target.createO(c, res);
		}

		@Override
		public void onProcEnd(CBase c, HResp res, OutputStream o)
				throws Exception {
			this.target.onProcEnd(c, res, o);
		}

		@Override
		public void onCreateR(CBase c, HResp res, Policy pc) throws Exception {
			this.target.onCreateR(c, res, pc);
		}

		@Override
		public void onCache(CBase c, HResp res) throws Exception {
			Message msg = new Message();
			msg.what = 4;
			msg.obj = new Object[] { this.target, c, res };
			H.sendMessage(msg);
		}

		@Override
		public void onExecErr(CBase c, Throwable e) {
			Message msg = new Message();
			msg.what = 5;
			msg.obj = new Object[] { this.target, c, e };
			H.sendMessage(msg);
		}
	}
}
