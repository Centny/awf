package org.cny.awf.test;

import java.io.File;
import java.util.Locale;

import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.dlm.DlmC;
import org.cny.awf.net.http.dlm.DlmCallback;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

public class DlmAty extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dlm);
	}

	public String did;

	public void start(View v) {

		for (int i = 0; i < 10; i++) {
			File xx = new File(Environment.getExternalStorageDirectory(), "xxx" + i);
			xx.delete();
			String url = String.format(Locale.ENGLISH, "http://pb.dev.jxzy.com/img/F100%02d.jpg", i);
			this.did = H.doGet(url, xx.getAbsolutePath(), new DlmCallback() {

				@Override
				public void onSuccess(DlmC c, HResp res) throws Exception {
					System.err.println("success" + "---->");
				}

				@Override
				public void onProcess(DlmC c, float speed, float rate) {
					System.err.println(c.id + "-->" + (speed * 1000 / 1024) + "->" + rate + "---->");
				}

				@Override
				public void onProcEnd(DlmC c, HResp res) throws Exception {
					System.err.println("end" + "---->");
				}

				@Override
				public void onExecErr(DlmC c, Throwable e) {
					System.err.println("exe_err" + "---->");
					e.printStackTrace();
				}

				@Override
				public void onError(DlmC c, Throwable err) throws Exception {
					System.err.println("err" + "---->");
					err.printStackTrace();
				}
			});
		}
	}

	public void stop(View v) {
		H.dlm().poll(this.did);
	}
}
