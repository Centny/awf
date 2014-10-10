package org.cny.amf.net.http;

import android.os.AsyncTask;

/**
 * The asynchronous task for HTTP request.
 * 
 * @author cny
 * 
 */
public class HAsyncTask extends HClientM {
	private ATask atsk;

	/**
	 * The default constructor by URL and call back.
	 * 
	 * @param url
	 *            the URL.
	 * @param cback
	 *            the call back.
	 */
	public HAsyncTask(String url, HCallback cback) {
		super(url, cback);
		this.atsk = new ATask();
	}

	/**
	 * Asynchronous task implementation class.
	 * 
	 * @author cny
	 * 
	 */
	public class ATask extends AsyncTask<HAsyncTask, Float, HAsyncTask> {

		@Override
		protected HAsyncTask doInBackground(HAsyncTask... params) {
			try {
				params[0].exec();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return params[0];
		}

		@Override
		protected void onPostExecute(HAsyncTask result) {
			if (error == null) {
				cback.onSuccess(HAsyncTask.this);
			} else {
				cback.onError(HAsyncTask.this, error);
			}
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			cback.onProcess(HAsyncTask.this, values[0]);
		}

		public void onProcess(float rate) {
			this.publishProgress(rate);
		}
	};

	@Override
	public void onProcess(HClient c, float rate) {
		this.atsk.onProcess(rate);
	}

	/**
	 * Start the asynchronous task.
	 */
	public void asyncExec() {
		this.atsk.execute(this);
	}
}
