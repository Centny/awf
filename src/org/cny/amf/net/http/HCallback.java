package org.cny.amf.net.http;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface HCallback {
	/**
	 * called it before request network.<br/>
	 * (not UI thread)
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 * @param r
	 *            the HttpUriRequest instance.
	 */
	public void onRequest(HClient c, HttpUriRequest uri) throws Exception;

	/**
	 * called it before data transfer.return the OutputStream to store the HTTP
	 * response data.<br/>
	 * return null or throw exception will stop data transfer and call onErr.<br/>
	 * (not UI thread)
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 * @param r
	 *            the HTTPReponse instance.
	 * @return the OutputStream for store response data.
	 * @throws Exception
	 *             all Exception.
	 */
	public OutputStream onBebin(HClient c, HResp r) throws Exception;

	/**
	 * called it after data transfered.<br/>
	 * throw exception will call onErr.<br/>
	 * (not UI thread)
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 * @param out
	 *            which OutpuStream is created by onBegin.
	 * @throws Exception
	 *             all Exception.
	 */
	public void onEnd(HClient c, OutputStream out);

	//
	/**
	 * called it on data transfer rate changed. (UI thread)
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 * @param rate
	 *            data transfer rate.
	 */
	public void onProcess(HClient c, float rate);

	/**
	 * called it on HTTP completed success.
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 */
	public void onSuccess(HClient c);

	/**
	 * called it on HTTP completed error.
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 * @param err
	 *            target exception.
	 */
	public void onError(HClient c, Throwable err);

	/**
	 * the normal method call back class implemented HTTPCallback for GET/POST.
	 * 
	 * @author cny
	 * 
	 */
	public abstract static class HMCallback implements HCallback {
		private ByteArrayOutputStream out = new ByteArrayOutputStream();
		private HClient c;
		protected String bencoding;

		@Override
		public OutputStream onBebin(HClient c, HResp r) throws Exception {
			this.c = c;
			this.bencoding = r.getEncoding();
			this.out.reset();
			return out;
		}

		@Override
		public void onEnd(HClient c, OutputStream out) {
		}

		@Override
		public void onProcess(HClient c, float rate) {
		}

		@Override
		public void onRequest(HClient c, HttpUriRequest r) throws Exception {
		}

		@Override
		public void onSuccess(HClient c) {
			try {
				this.onSuccess(c, this.data());
			} catch (Exception e) {
				this.onError(c, e);
			}
		}

		/**
		 * Get response data.
		 * 
		 * @return the response data string.
		 * @throws Exception
		 *             exception when error,like encoding error.
		 */
		public String data() throws Exception {
			String bcode = this.c.response.getEncoding();
			if (bcode != null) {
				this.bencoding = bcode;
			}
			return new String(this.out.toByteArray(), this.bencoding);
		}

		/**
		 * Get the OutputStream for store HTTP response data.
		 * 
		 * @return the OutputStream.
		 */
		public OutputStream getOut() {
			return this.out;
		}

		/**
		 * call it when response data success.
		 * 
		 * @param c
		 *            the HTTPClient instance.
		 * @param data
		 *            the response data.
		 */
		public abstract void onSuccess(HClient c, String data);
	}

	/**
	 * the normal JSON call back class implemented HTTPMCallback for GET/POST.
	 * 
	 * @author cny
	 * 
	 */
	public abstract static class HJsonCallback extends HMCallback {

		@Override
		public void onSuccess(HClient c, String data) {
			Throwable err;
			try {
				this.onSuccess(c, new JSONObject(data));
				return;
			} catch (JSONException e) {
				err = e;
			}
			try {
				this.onSuccess(c, new JSONArray(data));
				return;
			} catch (JSONException e) {
				err = e;
			}
			this.onFailure(c, err);
		}

		@Override
		public void onError(HClient c, Throwable error) {
			this.onFailure(c, error);
		}

		/**
		 * call it when response JSON array.
		 * 
		 * @param c
		 *            the HTTPClient instance.
		 * @param ary
		 *            the JSONArray object.
		 */
		public abstract void onSuccess(HClient c, JSONArray ary);

		/**
		 * call it when response JSON object.
		 * 
		 * @param c
		 *            the HTTPClient instance.
		 * @param obj
		 *            the JSONObject object.
		 */
		public abstract void onSuccess(HClient c, JSONObject obj);

		/**
		 * call it when error occur.
		 * 
		 * @param c
		 *            the HTTPClient instance.
		 * @param err
		 *            the error instance.
		 */
		public abstract void onFailure(HClient c, Throwable err);
	}

	/**
	 * the normal download call back class implemented HTTPCallback for
	 * GET/POST.<br/>
	 * it will auto download the file data to specified file path.
	 * 
	 * @author cny
	 * 
	 */
	public static class HDownCallback implements HCallback {
		private String filepath;
		private FileOutputStream fos;

		/**
		 * the default constructor.
		 */
		public HDownCallback() {

		}

		/**
		 * the constructor by file path.
		 * 
		 * @param filepath
		 *            the file path to save.
		 */
		public HDownCallback(String filepath) {
			this.filepath = filepath;
		}

		@Override
		public OutputStream onBebin(HClient c, HResp r) throws Exception {
			this.fos = new FileOutputStream(this.filepath, false);
			return new BufferedOutputStream(this.fos);
		}

		@Override
		public void onEnd(HClient c, OutputStream out) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onProcess(HClient c, float rate) {

		}

		@Override
		public void onSuccess(HClient c) {
			try {
				this.fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onError(HClient c, Throwable err) {
			try {
				this.fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onRequest(HClient c, HttpUriRequest r) throws Exception {
		}

		/**
		 * Get the file path.
		 * 
		 * @return the file path.
		 */
		public String getFilepath() {
			return filepath;
		}

		/**
		 * Set the file path.
		 * 
		 * @param filepath
		 *            the file path.
		 */
		public void setFilepath(String filepath) {
			this.filepath = filepath;
		}

	}

	/**
	 * the auto check file name download call back class extends
	 * HTTPDownCallback.<br/>
	 * it will use the filename to save file in Content-Disposition response
	 * header.using default name or end node of URL when Content-Disposition is
	 * not found.
	 * 
	 * @author cny
	 * 
	 */
	public static class HNameDlCallback extends HDownCallback {
		private String defaultName;
		private String sdir;
		private String fname;

		/**
		 * the save folder path.
		 * 
		 * @param sdir
		 *            the folder path to save.
		 */
		public HNameDlCallback(String sdir) {
			this.sdir = sdir;
		}

		@Override
		public OutputStream onBebin(HClient c, HResp r) throws Exception {
			this.fname = r.getFilename();
			if (this.fname == null && this.defaultName != null) {
				this.fname = this.defaultName;
			}
			if (this.fname == null) {
				this.fname = this.getUrlName(c);
			}
			File f = new File(this.sdir, this.fname);
			this.setFilepath(f.getAbsolutePath());
			return super.onBebin(c, r);
		}

		/**
		 * Get the end node of URL as file name.
		 * 
		 * @param c
		 *            the HTTPClient instance.
		 * @return the URL file name.
		 */
		private String getUrlName(HClient c) {
			String url = c.getUrl();
			url = url.split("\\?")[0];
			String[] urls = url.split("/");
			return urls[urls.length - 1];
		}

		/**
		 * Get the default name.
		 * 
		 * @return the default name.
		 */
		public String getDefaultName() {
			return defaultName;
		}

		/**
		 * Set the default name.
		 * 
		 * @param defaultName
		 *            the default name.
		 */
		public void setDefaultName(String defaultName) {
			this.defaultName = defaultName;
		}

		/**
		 * Get the folder path for saving file.
		 * 
		 * @return the folder path.
		 */
		public String getSdir() {
			return sdir;
		}

		/**
		 * Set the folder path for saving file.
		 * 
		 * @param sdir
		 *            the folder path.
		 */
		public void setSdir(String sdir) {
			this.sdir = sdir;
		}

		/**
		 * Get file name.
		 * 
		 * @return the file name is saved.
		 */
		public String getFname() {
			return fname;
		}

	}
}
