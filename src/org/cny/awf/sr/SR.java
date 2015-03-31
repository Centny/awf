package org.cny.awf.sr;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Map;

import org.cny.awf.er.ER;
import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.HAsyncTask;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.PIS;
import org.cny.awf.net.http.cres.CRes;
import org.cny.awf.net.http.cres.CRes.HResCallbackNCaller;
import org.cny.awf.net.http.cres.CRes.HResCallbackNable;
import org.cny.jwf.io.RenameOutputStream;
import org.cny.jwf.util.Zip;
import org.slf4j.impl.SimpleLogger;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author cny
 *
 */
public class SR implements HResCallbackNable<String> {
	public static final String LOG_DIR = "_log_";
	public static final String ER_DIR = "_er_";
	public static final String SR_DIR = "_sr_";
	public static final String ER_FN = "er.dat";
	public static final String LOG_FN = "t.log";
	public static final String SR_FN = "sr.zip";
	protected static RenameOutputStream ROS;
	protected File sr, log, er;
	protected Context ctx;

	public SR(Context ctx) {
		this.sr = ctx.getExternalFilesDir(SR_DIR);
		this.log = ctx.getExternalFilesDir(LOG_DIR);
		this.er = ctx.getExternalFilesDir(ER_DIR);
		this.ctx = ctx;
	}

	/**
	 * do upload system record to server.
	 * 
	 * @param srv
	 *            the server address.
	 * @return code <br/>
	 *         1 :success, <br/>
	 *         0 :do nothing, <br/>
	 * @throws IOException
	 * 
	 */
	public int dou(String srv, Map<String, String> args) throws IOException {
		//
		File sr_er = new File(sr, ER_FN);
		File sr_l = new File(sr, LOG_FN);
		if (!sr_er.exists() && !ER.backup(sr_er)) {
			return 0;
		}
		File sr_sr = new File(sr, SR_FN);
		if (ROS == null) {
			Zip.zip(sr_sr, sr, sr_er);
		} else {
			ROS.rename(sr_l);
			Zip.zip(sr_sr, sr, sr_er, sr_l);
		}
		HAsyncTask hc = new HAsyncTask(this.ctx, srv,
				new CRes.HResCallbackNCaller<String>(this));
		hc.setMethod("POST");
		hc.addBinary(PIS.create("sr_f", sr_sr));
		if (args != null) {
			for (String key : args.keySet()) {
				hc.addArg(key, args.get(key));
			}
		}
		this.onPrepareHc(hc);
		hc.asyncExec();
		return 1;
	}

	/**
	 * clear all SR filter.
	 */
	protected void clear() {
		new File(sr, ER_FN).delete();
		new File(sr, LOG_FN).delete();
		new File(sr, SR_FN).delete();
	}

	@Override
	public Type createToken(HResCallbackNCaller<String> caller)
			throws Exception {
		return new TypeToken<CRes<String>>() {
		}.getType();
	}

	@Override
	public void onError(HResCallbackNCaller<String> caller, CBase c,
			CRes<String> cache, Throwable err) throws Exception {
		err.printStackTrace();
		Log.e("SR", "upload SR err:" + err.getMessage());
	}

	@Override
	public void onSuccess(HResCallbackNCaller<String> caller, CBase c,
			HResp res, CRes<String> data) throws Exception {
		if (data.code == 0) {
			this.clear();
			Log.i("SR", "upload SR success:" + data.data);
		} else {
			Log.e("SR", "upload SR err:" + data);
		}
	}

	protected void onPrepareHc(HAsyncTask hc) {

	}

	public static void initSimpleLog(Context ctx, boolean debug)
			throws IOException {
		File log = ctx.getExternalFilesDir(LOG_DIR);
		File log_f = new File(log, LOG_FN);
		if (ROS != null) {
			ROS.close();
			ROS = null;
		}
		ROS = new RenameOutputStream(log_f, true);
		SimpleLogger.EXT_WRITER = new PrintWriter(ROS);
		if (debug) {
			System.setProperty(
					org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
		} else {
			System.setProperty(
					org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
		}
	}
}
