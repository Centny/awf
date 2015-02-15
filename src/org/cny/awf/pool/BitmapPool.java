package org.cny.awf.pool;

import org.cny.awf.util.Util;
import org.cny.jwf.util.ObjPool;

import android.graphics.Bitmap;

public class BitmapPool extends ObjPool<Bitmap> {

	protected static BitmapPool POOL_;

	public static void init(int max) {
		POOL_ = new BitmapPool(max);
	}

	public static BitmapPool instance() {
		if (POOL_ == null) {
			POOL_ = new BitmapPool(20);
		}
		return POOL_;
	}

	public static void free() {
		POOL_ = null;
	}

	public static Bitmap dol(Object key, Object... args) throws Exception {
		return instance().load_(key, args);
	}

	public BitmapPool(int max) {
		super(max);
	}

	@Override
	protected Bitmap create(Object key, Object[] args) throws Exception {
		int cr = 0;
		if (args.length > 0) {
			cr = ((Integer) args[0]);
		}
		Bitmap img = Util.readBitmap(key.toString());
		if (cr > 0) {
			img = Util.toRoundCorner(img, cr);
		}
		return img;
	}

}
