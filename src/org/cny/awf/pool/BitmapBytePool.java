package org.cny.awf.pool;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapBytePool extends LruCache<String, BitmapByte> {
	protected static BitmapBytePool POOL_;

	public static BitmapBytePool instance() {
		if (POOL_ == null) {
			Runtime rt = Runtime.getRuntime();
			POOL_ = new BitmapBytePool((int) rt.maxMemory() / 2);
		}
		return POOL_;
	}

	public static void free() {
		POOL_ = null;
	}

	public static void init(int max) {
		POOL_ = new BitmapBytePool(max);
	}

	public static Bitmap cache(String key) {
		return instance().load(key);
	}

	public BitmapBytePool(int maxSize) {
		super(maxSize);
	}

	public Bitmap load(String key) {
		BitmapByte bm = this.get(key);
		if (bm == null) {
			return null;
		}
		return bm.create();
	}

	public void put(String key, Bitmap bm) {
		this.put(key, new BitmapByte(bm));
	}

	@Override
	protected int sizeOf(String key, BitmapByte value) {
		return value.count;
	}

}
