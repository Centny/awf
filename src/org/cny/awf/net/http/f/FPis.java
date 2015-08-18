package org.cny.awf.net.http.f;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.entity.ContentType;
import org.cny.awf.net.http.C;
import org.cny.awf.net.http.HDb;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.PIS.PathPis;
import org.cny.awf.util.Util;
import org.cny.jwf.util.FUtil;
import org.cny.jwf.util.FUtil.Hash;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Class FPis provider copy the target input stream to cache file and add http
 * cache.
 * 
 * @author cny
 *
 */
public class FPis extends PathPis {

	/**
	 * the initial input stream
	 */
	protected InputStream in;
	/**
	 * the true input steam to upload.<br/>
	 * it is created by copy the initial input stream.
	 */
	protected InputStream rin;
	/**
	 * the android context.
	 */
	protected Context ctx;
	/**
	 * the input data Sha1.
	 */
	protected String sha1;
	/**
	 * the cache file.
	 */
	protected File cf;// cache file.
	/**
	 * whether cache the input stream to file, default is true.
	 */
	protected boolean cached = true;

	/**
	 * create FPis by the input stream.
	 * 
	 * @param ctx
	 *            the android context.
	 * @param name
	 *            the form name.
	 * @param filename
	 *            the file name to display.
	 * @param ct
	 *            the file content type (mime type).
	 * @param length
	 *            the input data length.
	 * @param autoclose
	 *            whether auto close input stream after used.
	 * @param in
	 *            the target input stream.
	 */
	public FPis(Context ctx, String name, String filename, ContentType ct,
			boolean autoclose, long length, InputStream in) {
		super(name, filename, ct, autoclose, length);
		this.ctx = ctx;
		this.in = in;
	}

	/**
	 * create FPis by the input stream.
	 * 
	 * @param ctx
	 *            the android context.
	 * @param name
	 *            the form name.
	 * @param filename
	 *            the file name to display.
	 * @param ct
	 *            the file content type name (mime type name).
	 * @param length
	 *            the input data length.
	 * @param autoclose
	 *            whether auto close input stream after used.
	 * @param in
	 *            the target input stream.
	 */
	public FPis(Context ctx, String name, String filename, String ct,
			boolean autoclose, long length, InputStream in) {
		super(name, filename, ContentType.create(ct), autoclose, length);
		this.ctx = ctx;
		this.in = in;
	}

	/**
	 * create FPis by file.<br/>
	 * it will auto detect the file type.
	 * 
	 * @param name
	 *            the form name.
	 * @param f
	 *            target file.
	 * @return the FPis input stream.
	 * @throws FileNotFoundException
	 */
	public FPis(Context ctx, String name, File f) throws FileNotFoundException {
		this.ctx = ctx;
		this.name = name;
		this.filename = f.getName();
		this.ct = ContentType.DEFAULT_BINARY;
		this.autoclose = true;
		this.length = f.length();
		this.path = f.getAbsolutePath();
		this.in = new java.io.FileInputStream(f);
	}

	/**
	 * create FPis by bitmap.<br/>
	 * it will convert bitmap to png image.
	 * 
	 * @param name
	 *            the form name.
	 * @param bm
	 *            target bitmap.
	 * @return the FPis input stream.
	 */
	public FPis(Context ctx, String name, Bitmap bm) {
		super();
		this.ctx = ctx;
		this.name = name;
		this.filename = "u.png";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.ct = ContentType.create("image/png");
		bm.compress(Bitmap.CompressFormat.PNG, 30, out);
		this.autoclose = true;
		this.length = out.size();
		this.in = new ByteArrayInputStream(out.toByteArray());
	}

	/**
	 * create FPis by file info.
	 * 
	 * @param ctx
	 *            content.
	 * @param info
	 *            file info.
	 * @throws FileNotFoundException
	 *             try open the path file error.
	 */
	public FPis(Context ctx, FInfo info) throws FileNotFoundException {
		super();
		this.ctx = ctx;
		this.name = info.getName();
		this.path = info.getPath();
		this.sha1 = info.getSha();
		this.length = info.getSize();
		this.ct = ContentType.create(info.getType());
		this.autoclose = true;
		this.in = new java.io.FileInputStream(this.path);
	}

	@Override
	protected InputStream createIn() throws IOException {
		if (this.rin == null) {
			throw new IOException("not initial");
		}
		return this.rin;
	}

	/**
	 * do the process to copy the input stream to cache file.
	 * 
	 * @throws IOException
	 *             IOException for file process.
	 */
	public void doProc() throws Exception {
		FileOutputStream fos = null;
		if (this.cached) {
			this.cf = HDb.loadDb_(this.ctx).newCacheF();
			try {
				fos = new FileOutputStream(this.cf);
				this.path = this.cf.getAbsolutePath();
				Hash hash = FUtil.sha1(this.in, fos);
				this.sha1 = Util.base64(hash.hash);
				this.length = hash.length;
				fos.close();
				this.autoclose = true;
				this.rin = new java.io.FileInputStream(this.cf);
			} catch (Exception e) {
				this.cf.deleteOnExit();
				if (fos != null) {
					fos.close();
				}
				throw new IOException(e);
			}
		} else {
			if (Util.isNullOrEmpty(this.sha1)) {
				Hash hash = FUtil.sha1(this.in, null);
				this.sha1 = Util.base64(hash.hash);
				this.length = hash.length;
				this.in.reset();
			}
			this.rin = this.in;
		}
	}

	/**
	 * get the file sha1.<br/>
	 * it will be null before doProc called.
	 * 
	 * @return the sha1 base64 string.
	 */
	public String Sha1() {
		return sha1;
	}

	/**
	 * the the cache file.<br/>
	 * it will be null before doProc called.
	 * 
	 * @return
	 */
	public File getCf() {
		return cf;
	}

	/**
	 * clear the cache file.it may be called after upload error.
	 */
	public void clear() {
		if (this.cf != null) {
			this.cf.delete();
			this.cf = null;
		}
	}

	/**
	 * add http cache by cahce file and url.
	 * 
	 * @param url
	 *            target url.
	 */
	public void addCache(String url) {
		HDb db = HDb.loadDb_(this.ctx);
		HResp res = new HResp().init(new C(this.ctx, url, null), this);
		if (db.find(res) == null) {
			db.add(res);
		}
	}

	/**
	 * get the file Pojo info.
	 * 
	 * @return the FInfo.
	 */
	public FInfo info() {
		return new FInfo(this);
	}

	/**
	 * @return the cached
	 */
	public boolean isCached() {
		return cached;
	}

	/**
	 * @param cached
	 *            the cached to set
	 */
	public void setCached(boolean cached) {
		this.cached = cached;
	}
}
