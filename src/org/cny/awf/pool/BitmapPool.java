package org.cny.awf.pool;

import org.cny.awf.util.Util;
import org.cny.jwf.util.ObjPool;

import android.graphics.Bitmap;

public class BitmapPool extends ObjPool<Bitmap> {

	protected static BitmapPool POOL_;

	public static BitmapPool instance() {
		if (POOL_ == null) {
			POOL_ = new BitmapPool();
		}
		return POOL_;
	}

	public static void free() {
		POOL_ = null;
	}

	public static Bitmap dol(Object key, Object... args) throws Exception {
		return instance().load_(key, args);
	}

	public static Bitmap cache(Object key, Object... args) {
		return instance().find(key, args);
	}

	public BitmapPool() {
	}

	@Override
	protected Object createKey(Object key, Object[] args) {
		String ks = super.createKey(key, args).toString();
		if (args.length > 0) {
			ks += "@" + ((Integer) args[0]);
		}
		if (args.length > 2) {
			ks += "," + ((Integer) args[1]);
			ks += "," + ((Integer) args[2]);
		}
		return ks;
	}

	@Override
	protected Bitmap create(Object key, Object[] args) throws Exception {
		int cr = 0, w = 0, h = 0;
		if (args.length > 0) {
			cr = ((Integer) args[0]);
		}
		if (args.length > 2) {
			w = ((Integer) args[1]);
			h = ((Integer) args[2]);
		}
		Bitmap img = Util.readBitmap(key.toString(), w, h);
		if (img == null) {
			throw new Exception("read bitmap error:" + key);
		}
		if (cr > 0) {
			Bitmap timg = img;
			img = Util.toRoundCorner(timg, cr);
			timg.recycle();
			// System.gc();
		}
		return img;
	}

	public static class UrlKey implements Key {
		public String url;
		public String loc;

		public UrlKey(String url, String loc) {
			super();
			this.url = url;
			this.loc = loc;
		}

		@Override
		public String toString() {
			return this.loc;
		}

		@Override
		public Object findKey() {
			return this.url;
		}

	}
}
