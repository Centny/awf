package org.cny.awf.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapPool extends LruCache<UrlKey, Bitmap> {
	private static final Logger L = LoggerFactory.getLogger(BitmapPool.class);
	public static boolean ShowLog = false;

	public BitmapPool(int maxSize) {
		super(maxSize);
	}

	protected static BitmapPool POOL_;

	public static BitmapPool instance() {
		synchronized (L) {
			if (POOL_ == null) {
				POOL_ = new BitmapPool(5 * 1024 * 1024);
			}
		}
		return POOL_;
	}

	public static void free() {
		POOL_ = null;
	}

	public static void init(int max) {
		POOL_ = new BitmapPool(max);
	}

	public static void gc() {
		instance().evictAll();
		BitmapBytePool.gc();
		System.gc();
	}

	public static UrlKey createKey(Object key, Object[] args) {
		UrlKey tk;
		if (key instanceof UrlKey) {
			tk = (UrlKey) key;
		} else {
			tk = UrlKey.create(null, key.toString());
		}
		if (args != null) {
			tk.initrc(args);
		}
		return tk;
	}

	public static Bitmap dol(Object key, Object... args) throws Exception {
		return instance().load(createKey(key, args));
	}

	public static Bitmap dol(String path) throws Exception {
		return instance().load(createKey(path, new Object[] {}));
	}

	public static Bitmap dol(String path, int roundCorner, int w, int h) throws Exception {
		return instance().load(createKey(path, new Object[] { roundCorner, w, h }));
	}

	public static Bitmap dol(UrlKey key) throws Exception {
		return instance().load(key);
	}

	public static Bitmap cache(Object key, Object... args) {
		return instance().loadCache(createKey(key, args));
	}

	public static Bitmap cache(UrlKey key) {
		return instance().loadCache(key);
	}

	public boolean usingBytePool = true;
	public boolean usingBitmapPool = false;

	@Override
	protected void entryRemoved(boolean evicted, UrlKey key, Bitmap oldValue, Bitmap newValue) {
		super.entryRemoved(evicted, key, oldValue, newValue);
		if (this.usingBytePool) {
			BitmapBytePool.instance().put(key.toString(), oldValue);
		}
		this.slog("remove bimap cache({}) on pool", key);
	}

	public synchronized Bitmap loadCache(UrlKey key) {
		Bitmap img = null;
		OutOfMemoryError err = null;
		for (int i = 0; i < 2; i++) {
			try {
				img = this.get(key);
				if (img == null && this.usingBytePool) {
					String bkey = key.toString();
					img = BitmapBytePool.cache(bkey);
				}
				if (img != null) {
					this.slog("using bimap cache({}) on pool", key);
				}
				return img;
			} catch (OutOfMemoryError e) {
				gc();
				err = e;
			}
		}
		if (err != null) {
			throw err;
		}
		return img;
	}

	public synchronized Bitmap load(UrlKey key) throws Exception {
		Bitmap img = this.loadCache(key);
		if (img != null) {
			this.slog("using cache for bitmap({}) on pool", key);
			return img;
		}
		OutOfMemoryError err = null;
		for (int i = 0; i < 2; i++) {
			try {
				img = key.read();
				if (this.usingBitmapPool) {
					this.put(key, img);
					this.slog("put bitmap({}) to pool", key);
				} else if (this.usingBytePool) {
					BitmapBytePool.instance().put(key.toString(), img);
				}
				return img;
			} catch (OutOfMemoryError e) {
				gc();
				err = e;
			}
		}
		if (err != null) {
			throw err;
		}
		return img;
	}

	@Override
	protected int sizeOf(UrlKey key, Bitmap value) {
		return value.getByteCount();
	}

	protected void slog(String fmt, Object arg1) {
		if (ShowLog) {
			L.debug(fmt, arg1);
		}
	}
}
