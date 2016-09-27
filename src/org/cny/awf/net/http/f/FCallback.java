package org.cny.awf.net.http.f;

import java.io.OutputStream;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.CBase.Policy;
import org.cny.awf.net.http.HCallback;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.PIS;
import org.cny.awf.util.Util;

/**
 * Extend class from http callback to upload the file.
 * 
 * @author cny
 *
 */
public class FCallback implements HCallback {
	/**
	 * the FUrlH interface extended from normal HCallback to create the uploaded
	 * file url.<br/>
	 * 
	 * @author cny
	 *
	 */
	public static interface FUrlH extends HCallback {
		/**
		 * creating the file url.
		 * 
		 * @param c
		 *            http CBase.
		 * @param res
		 *            http HResp.
		 * @param img
		 *            target FPis.
		 * @return the file public url.
		 */
		String createUrl(CBase c, HResp res, FPis img);

		/**
		 * on file upload and cache success.
		 * 
		 * @param c
		 *            http CBase.
		 * @param res
		 *            http HResp.
		 * @param img
		 *            target FPis.
		 * @param url
		 *            the file public url.
		 */
		void onFSuccess(CBase c, HResp res, FPis img, String url);
	}

	// private static final Logger L = LoggerFactory.getLogger(FCallback.class);
	/**
	 * target file FPis.
	 */
	protected FPis file;
	/**
	 * the FUrlH.
	 */
	protected FUrlH h;

	/**
	 * if clear the FPis when upload error.
	 */
	protected boolean clearf;

	/**
	 * constructor by FUrlH and clear FPis.
	 * 
	 * @param h
	 *            the file URL handler.
	 * @param clearf
	 *            if clear FPis when upload error.
	 */
	public FCallback(FUrlH h, boolean clearf) {
		this.h = h;
		this.clearf = clearf;
	}

	@Override
	public void onCreateR(CBase c, HResp res, Policy pc) throws Exception {
		for (PIS pis : c.getFiles()) {
			if (!(pis instanceof FPis)) {
				continue;
			}
			FPis img = (FPis) pis;
			img.doProc();
			c.addArg("sha", img.sha1);
		}
		this.h.onCreateR(c, res, pc);
	}

	@Override
	public void onProcess(CBase c, PIS pis, float rate) {
		if (pis instanceof FPis) {
			this.file = (FPis) pis;
		}
		this.h.onProcess(c, pis, rate);
	}

	@Override
	public void onExecErr(CBase c, Throwable e) {
		if (this.clearf && this.file != null) {
			this.file.clear();
		}
		this.h.onExecErr(c, e);
	}

	@Override
	public void onError(CBase c, Throwable err) throws Exception {
		if (this.clearf && this.file != null) {
			this.file.clear();
		}
		this.h.onError(c, err);
	}

	@Override
	public void onCache(CBase c, HResp res) throws Exception {
		this.h.onCache(c, res);
	}

	@Override
	public OutputStream createO(CBase c, HResp res) throws Exception {
		return this.h.createO(c, res);
	}

	@Override
	public void onProcess(CBase c, long clen, long rsize, long perid) {
		this.h.onProcess(c, clen, rsize, perid);
	}

	@Override
	public void onProcEnd(CBase c, HResp res, OutputStream o) throws Exception {
		this.h.onProcEnd(c, res, o);
	}

	@Override
	public void onSuccess(CBase c, HResp res) throws Exception {
		this.h.onSuccess(c, res);
		if (this.file == null) {
			throw new Exception("the image PIS is null");
		}
		String url = this.h.createUrl(c, res, this.file);
		if (Util.isNullOrEmpty(url)) {
			if (this.clearf) {
				this.file.clear();
			}
			throw new Exception("create url is null or empty");
		}
		this.file.addCache(url);
		this.h.onFSuccess(c, res, file, url);
	}

	/**
	 * @return the clearf
	 */
	public boolean isClearf() {
		return clearf;
	}

	/**
	 * @param clearf
	 *            the clearf to set
	 */
	public void setClearf(boolean clearf) {
		this.clearf = clearf;
	}

}
