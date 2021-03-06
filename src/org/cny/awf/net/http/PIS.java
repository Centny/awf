package org.cny.awf.net.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.entity.ContentType;
import org.cny.awf.util.Util;

import android.graphics.Bitmap;

public abstract class PIS extends InputStream {
	private long readed = 0;
	private InputStream in;
	private PisH h;
	private long last_proc;

	protected String name;
	protected String filename;
	protected ContentType ct = ContentType.DEFAULT_BINARY;
	protected boolean autoclose = true;
	protected long length;
	protected long delay = 1000;

	protected PIS() {

	}

	public PIS(String name, String filename, ContentType ct, boolean autoclose, long length) {
		super();
		this.name = name;
		this.filename = filename;
		this.ct = ct;
		this.autoclose = autoclose;
		this.length = length;
	}

	@Override
	public int available() throws IOException {
		if (this.in == null) {
			this.in = this.createIn();
		}
		return this.in.available();
	}

	@Override
	public void close() throws IOException {
		if (this.in != null && this.autoclose) {
			this.in.close();
			this.in = null;
		}
	}

	@Override
	public void mark(int readlimit) {
		if (this.in != null) {
			this.in.mark(readlimit);
		}
	}

	@Override
	public boolean markSupported() {
		return this.in != null && this.in.markSupported();
	}

	@Override
	public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
		if (this.in == null) {
			this.in = this.createIn();
		}
		int rlen = this.in.read(buffer, byteOffset, byteCount);
		if (rlen < 1) {
			return rlen;
		}
		this.readed += rlen;
		this.onProcess(this, this.readed);
		return rlen;
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		if (this.in == null) {
			this.in = this.createIn();
		}
		int rlen = this.in.read(buffer);
		if (rlen < 1) {
			return rlen;
		}
		this.readed += rlen;
		this.onProcess(this, this.readed);
		return rlen;
	}

	@Override
	public synchronized void reset() throws IOException {
		if (this.in == null) {
			this.in = this.createIn();
		}
		this.in.reset();
	}

	@Override
	public long skip(long byteCount) throws IOException {
		if (this.in == null) {
			this.in = this.createIn();
		}
		return this.in.skip(byteCount);
	}

	@Override
	public int read() throws IOException {
		if (this.in == null) {
			this.in = this.createIn();
		}
		int val = this.in.read();
		this.readed += 1;
		this.onProcess(this, this.readed);
		return val;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public ContentType getCt() {
		return ct;
	}

	public void setCt(ContentType ct) {
		this.ct = ct;
	}

	public PisH getH() {
		return h;
	}

	public void setH(PisH h) {
		this.h = h;
	}

	public long getLength() {
		return length;
	}

	protected abstract InputStream createIn() throws IOException;

	protected void onProcess(PIS in, long t) {
		if (this.h == null) {
			return;
		}
		long now = new Date().getTime();
		if (now - this.last_proc > this.delay || t == this.length) {
			float rate = 0;
			if (this.length > 0) {
				rate = ((float) t) / ((float) this.length);
			}
			this.h.onProcess(this, rate);
			this.last_proc = now;
		}
	}

	/**
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * @param delay
	 *            the delay to set
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	public static interface PisH {
		public void onProcess(PIS pis, float rate);
	}

	public static abstract class PathPis extends PIS {
		protected String path;

		public PathPis() {
			super();
		}

		public PathPis(String name, String filename, ContentType ct, boolean autoclose, long length) {
			super(name, filename, ct, autoclose, length);
		}

		public String getPath() {
			return path;
		}

		/**
		 * @param path
		 *            the path to set
		 */
		public void setPath(String path) {
			this.path = path;
		}

	}

	public static class FileInputStream extends NProcInputStream {

		protected File f;

		protected FileInputStream() {
		}

		@Override
		protected InputStream createIn() throws IOException {
			return new BufferedInputStream(new java.io.FileInputStream(this.f));
		}

	}

	public static class NProcInputStream extends PIS {
		protected InputStream nin;

		protected NProcInputStream() {
		}

		@Override
		protected InputStream createIn() throws IOException {
			return this.nin;
		}

	}

	public static PIS create(String name, String filename, InputStream in) {
		return create(name, filename, in, true);
	}

	public static PIS create(String name, String filename, InputStream in, boolean autoclose) {
		return create(name, filename, in, 0, autoclose);
	}

	public static PIS create(String name, String filename, InputStream in, long length) {
		return create(name, filename, in, length, ContentType.DEFAULT_BINARY, true);
	}

	public static PIS create(String name, String filename, InputStream in, long length, boolean autoclose) {
		return create(name, filename, in, length, ContentType.DEFAULT_BINARY, autoclose);
	}

	public static PIS create(String name, String filename, InputStream in, long length, String mimeType) {
		return create(name, filename, in, length, mimeType, true);
	}

	public static PIS create(String name, String filename, InputStream in, long length, String mimeType,
			boolean autoclose) {
		return create(name, filename, in, length, ContentType.create(mimeType), autoclose);
	}

	public static PIS create(String name, String filename, InputStream in, long length, ContentType mimeType) {
		return create(name, filename, in, length, mimeType, true);
	}

	public static PIS create(String name, Bitmap bm) {
		return create(name, bm, 0, 0, 30);
	}

	public static PIS create(String name, Bitmap bm, int maxWidth, int maxHeight, int quality) {
		return create(name, bm, !bm.hasAlpha(), maxWidth, maxHeight, quality);
	}

	public static PIS create(String name, Bitmap bm, boolean isjpg, int maxWidth, int maxHeight, int quality) {
		if (isjpg) {
			return create(name, "u.jpg", bm, isjpg, maxWidth, maxHeight, quality);
		} else {
			return create(name, "u.png", bm, isjpg, maxWidth, maxHeight, quality);
		}
	}

	public static PIS create(String name, String filename, Bitmap bm, boolean isjpg, int maxWidth, int maxHeight,
			int quality) {
		Bitmap tmp = bm;
		if (maxWidth > 0 && maxHeight > 0) {
			tmp = Util.zoomBitmap(bm, maxWidth, maxHeight);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String mimeType;
		if (isjpg) {
			mimeType = "image/jpeg";
			tmp.compress(Bitmap.CompressFormat.JPEG, quality, out);
		} else {
			mimeType = "image/png";
			tmp.compress(Bitmap.CompressFormat.PNG, quality, out);
		}
		if (tmp != bm && !tmp.isRecycled()) {
			tmp.recycle();
		}
		return create(name, filename, new ByteArrayInputStream(out.toByteArray()), out.size(), mimeType, false);
	}

	public static PIS create(String name, String filename, String filepath, int maxWidth, int maxHeight, int quality)
			throws FileNotFoundException {
		Bitmap bm = Util.readBitmap(filepath, maxWidth, maxHeight);
		PIS res = create(name, filename, bm, !bm.hasAlpha(), maxWidth, maxHeight, quality);
		bm.recycle();
		return res;
	}

	public static PIS create(String name, String filename, InputStream in, long length, ContentType mimeType,
			boolean autoclose) {
		NProcInputStream pis = new NProcInputStream();
		pis.nin = in;
		pis.name = name;
		pis.filename = filename;
		pis.length = length;
		pis.ct = mimeType;
		pis.autoclose = autoclose;
		return pis;
	}

	public static PIS create(String name, File f) {
		return create(name, f.getName(), f);
	}

	public static PIS create(String name, File f, String mimeType) {
		return create(name, f, ContentType.create(mimeType));
	}

	public static PIS create(String name, File f, ContentType mimeType) {
		return create(name, f.getName(), f, mimeType);
	}

	public static PIS create(String name, String filename, File f) {
		return create(name, filename, f, ContentType.DEFAULT_BINARY);
	}

	public static PIS create(String name, String filename, File f, String mimeType) {
		return create(name, filename, f, ContentType.create(mimeType));
	}

	public static PIS create(String name, String filename, File f, ContentType mimeType) {
		FileInputStream pis = new FileInputStream();
		pis.f = f;
		pis.name = name;
		pis.filename = filename;
		pis.length = f.length();
		pis.ct = mimeType;
		pis.autoclose = true;
		return pis;
	}
}
