package org.cny.awf.pool;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import org.cny.awf.util.Util;

import android.graphics.Bitmap;

public class UrlKey {
	public String url;
	public String loc;
	public int roundCorner;
	public int w, h, maxw, maxh;

	public UrlKey(String url, String loc, int roundCorner, int w, int h, int maxw, int maxh) {
		super();
		this.url = url;
		this.loc = loc;
		this.roundCorner = roundCorner;
		this.w = w;
		this.h = h;
		this.maxw = maxw;
		this.maxh = maxh;
	}

	protected void initrc(Object[] args) {
		if (args.length > 0) {
			this.roundCorner = ((Integer) args[0]);
		}
		if (args.length > 2) {
			this.w = ((Integer) args[1]);
			this.h = ((Integer) args[2]);
		}
	}

	public Bitmap read() throws Exception {
		String key = this.toString();
		Bitmap img = Util.readBitmap(this.loc, this.w, this.h, this.maxw, this.maxh);
		if (img == null) {
			throw new Exception("read bitmap error from" + this.loc);
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		if (img.hasAlpha()) {
			img.compress(Bitmap.CompressFormat.PNG, 100, stream);
		} else {
			img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		}
		BytePool.instance().put(key, stream.toByteArray());
		if (this.roundCorner > 0) {
			Bitmap timg = img;
			img = Util.toRoundCorner(timg, this.roundCorner);
			timg.recycle();
			// System.gc();
		}
		return img;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UrlKey)) {
			return false;
		}
		UrlKey key = (UrlKey) o;
		if (this.loc != null && key.loc != null) {
			return this.loc.equals(key.loc);
		}
		if (this.url != null && key.url != null) {
			return this.url.equals(key.url);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public static UrlKey create(String url, String loc, int roundCorner, int w, int h, int maxw, int maxh) {
		return new UrlKey(url, loc, roundCorner, w, h, maxw, maxh);
	}

	public static UrlKey create(String url, String loc, int roundCorner, int w, int h) {
		return new UrlKey(url, loc, roundCorner, w, h, 0, 0);
	}

	public static UrlKey create(String loc, int roundCorner, int w, int h) {
		return new UrlKey(null, loc, roundCorner, w, h, 0, 0);
	}

	public static UrlKey create(String loc) {
		return new UrlKey(null, loc, 0, 0, 0, 0, 0);
	}

	public static UrlKey create(String url, String loc) {
		return new UrlKey(url, loc, 0, 0, 0, 0, 0);
	}

	@Override
	public String toString() {
		if (Util.isNoEmpty(this.url)) {
			return String.format(Locale.ENGLISH, "url(%s)@%d-%d", this.url, this.w, this.h);
		} else {
			return String.format(Locale.ENGLISH, "url(%s)@%d-%d", this.loc, this.w, this.h);
		}
	}

}
