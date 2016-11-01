package org.cny.awf.pool;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class BitmapByte {

	public byte[] buf;
	public Config config;
	public int width, height;
	public int count;

	public BitmapByte(Bitmap bm) {
		ByteBuffer tmp = ByteBuffer.allocate(bm.getByteCount());
		bm.copyPixelsToBuffer(tmp);
		this.buf = tmp.array();
		this.config = bm.getConfig();
		this.width = bm.getWidth();
		this.height = bm.getHeight();
		this.count = bm.getByteCount();
	}

	public Bitmap create() {
		Bitmap bm = null;
		try {
			bm = Bitmap.createBitmap(this.width, this.height, this.config);
			bm.copyPixelsFromBuffer(ByteBuffer.wrap(this.buf));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bm;
	}
}
