package org.cny.awf.net.http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.entity.ContentType;

public abstract class PIS extends InputStream {
	private long readed = 0;
	private InputStream in;
	private PisH h;

	protected String name;
	protected String filename;
	protected ContentType ct = ContentType.DEFAULT_BINARY;
	protected boolean autoclose = true;
	protected long length;

	protected PIS() {

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
	public int read(byte[] buffer, int byteOffset, int byteCount)
			throws IOException {
		if (this.in == null) {
			this.in = this.createIn();
		}
		int rlen = this.in.read(buffer, byteOffset, byteCount);
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

	protected abstract InputStream createIn() throws IOException;

	protected void onProcess(PIS in, long t) {
		if (this.h != null) {
			float rate = 0;
			if (this.length > 0) {
				rate = ((float) t) / ((float) this.length);
			}
			this.h.onProcess(this, rate);
		}
	}

	public static interface PisH {
		public void onProcess(PIS pis, float rate);
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

	public static PIS create(String name, String filename, InputStream in,
			boolean autoclose) {
		return create(name, filename, in, 0, autoclose);
	}

	public static PIS create(String name, String filename, InputStream in,
			long length) {
		return create(name, filename, in, length, ContentType.DEFAULT_BINARY,
				true);
	}

	public static PIS create(String name, String filename, InputStream in,
			long length, boolean autoclose) {
		return create(name, filename, in, length, ContentType.DEFAULT_BINARY,
				autoclose);
	}

	public static PIS create(String name, String filename, InputStream in,
			long length, String mimeType) {
		return create(name, filename, in, length, mimeType, true);
	}

	public static PIS create(String name, String filename, InputStream in,
			long length, String mimeType, boolean autoclose) {
		return create(name, filename, in, length, ContentType.create(mimeType),
				autoclose);
	}

	public static PIS create(String name, String filename, InputStream in,
			long length, ContentType mimeType) {
		return create(name, filename, in, length, mimeType, true);
	}

	public static PIS create(String name, String filename, InputStream in,
			long length, ContentType mimeType, boolean autoclose) {
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

	public static PIS create(String name, String filename, File f,
			String mimeType) {
		return create(name, filename, f, ContentType.create(mimeType));
	}

	public static PIS create(String name, String filename, File f,
			ContentType mimeType) {
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
