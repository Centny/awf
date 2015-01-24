package org.cny.awf.net.http;

import android.app.Activity;
import android.os.AsyncTask;

/**
 * The asynchronous task for HTTP request.
 * 
 * @author cny
 * 
 */
public class HAsyncTask extends C {
	private ATask atsk;

	/**
	 * The default constructor by URL and call back.
	 * 
	 * @param url
	 *            the URL.
	 * @param cback
	 *            the call back.
	 */
	public HAsyncTask(HDb db, String url, HCallback cback) {
		super(db, url, cback);
		this.atsk = new ATask();
	}

	public HAsyncTask(Activity aty, String url, HCallback cback) {
		super(aty, url, cback);
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
				params[0].run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return params[0];
		}

		@Override
		protected void onPostExecute(HAsyncTask result) {
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
	public void onProcess(float rate) {
		this.atsk.onProcess(rate);
	}

	/**
	 * Start the asynchronous task.
	 */
	public void asyncExec() {
		this.atsk.execute(new HAsyncTask[] { this });
	}
}
