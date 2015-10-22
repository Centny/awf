package org.cny.awf.net.http.f;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HAsyncTask;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.cres.CRes;
import org.cny.awf.net.http.cres.ResException;
import org.cny.awf.net.http.f.FCallback.FUrlH;

import android.graphics.Bitmap;

public class F {

	/**
	 * FStrCallback extends from common CRes string callback.<br>
	 * it already implemented the FUsrH.createImgUrl and
	 * CRes.HResStrCallback.onSuccess
	 * 
	 * @author cny
	 *
	 */
	public static abstract class FStrCallback extends CRes.HResStrCallback
			implements FUrlH {
		protected String url = null;

		@Override
		public String createUrl(CBase c, HResp res, FPis img) {
			return this.url;
		}

		@Override
		public void onSuccess(CBase c, HResp res, CRes<String> data)
				throws Exception {
			if (data.code == 0) {
				this.url = data.data;
			} else {
				throw new ResException(data);
			}
		}
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
	public static FPis create(String name, Bitmap bm) {
		return new FPis(H.CTX, name, bm);
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
	public static FPis create(String name, File f) throws FileNotFoundException {
		return new FPis(H.CTX, name, f);
	}

	/**
	 * create FPis by the input stream.
	 * 
	 * @param name
	 *            the form name.
	 * @param filename
	 *            the file name to display.
	 * @param ct
	 *            the file content type name (mime type name).
	 * @param autoclose
	 *            whether auto close input stream after used.
	 * @param in
	 *            the target input stream.
	 * @return the FPis input stream.
	 */
	public static FPis create(String name, String filename, String ct,
			boolean autoclose, InputStream in) {
		return new FPis(H.CTX, name, filename, ct, autoclose, 0, in);
	}

	/**
	 * create FPis by the input stream.
	 * 
	 * @param name
	 *            the form name.
	 * @param filename
	 *            the file name to display.
	 * @param ct
	 *            the file content type (mime type).
	 * @param autoclose
	 *            whether auto close input stream after used.
	 * @param in
	 *            the target input stream.
	 * @return the FPis input stream.
	 */
	public static FPis create(String name, String filename, ContentType ct,
			boolean autoclose, InputStream in) {
		return new FPis(H.CTX, name, filename, ct, autoclose, 0, in);
	}

	/**
	 * to upload file by FPis
	 * 
	 * @param url
	 *            the fs server api.
	 * @param pis
	 *            target FPis.
	 * @param h
	 *            handler.
	 * @return the task.
	 */
	public static HAsyncTask doPost(String url, FPis pis, FUrlH h) {
		return doPost(url, null, null, pis, h);
	}

	/**
	 * to upload file by FPis
	 * 
	 * @param url
	 *            the fs server api.
	 * @param args
	 *            the upload arguments.
	 * @param pis
	 *            target FPis.
	 * @param h
	 *            handler.
	 * @return the task.
	 */
	public static HAsyncTask doPost(String url, List<BasicNameValuePair> args,
			FPis pis, FUrlH h) {
		return doPost(url, args, null, pis, h);
	}

	/**
	 * to upload file by FPis
	 * 
	 * @param url
	 *            the fs server api.
	 * @param args
	 *            the upload http arguments.
	 * @param heads
	 *            the upload http heads.
	 * @param pis
	 *            target FPis.
	 * @param h
	 *            handler.
	 * @return the task.
	 */
	public static HAsyncTask doPost(String url, List<BasicNameValuePair> args,
			List<BasicNameValuePair> heads, FPis pis, FUrlH h) {
		return H.doPost(url, args, heads, pis, new FCallback(h, false));
	}

	protected F() {

	}
}
