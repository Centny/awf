package org.cny.awf.pool;

import android.support.v4.util.LruCache;

public class BytePool extends LruCache<String, byte[]> {

	protected static BytePool POOL_;

	public static BytePool instance() {
		if (POOL_ == null) {
			POOL_ = new BytePool(200 * 1024 * 1024);
		}
		return POOL_;
	}

	public static void free() {
		POOL_ = null;
	}

	public static void init(int max) {
		POOL_ = new BytePool(max);
	}

	public static byte[] cache(String key) {
		return instance().get(key);
	}

	public BytePool(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, byte[] value) {
		return value.length;
	}
}
