package org.cny.awf.test;

import java.io.File;

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

		File xx = new File(Environment.getExternalStorageDirectory(), "xxx");
		this.did = H.doGet("http://pb.dev.jxzy.com/armeabi-v7a/libxwalkcore.zip?", xx.getAbsolutePath(),
				new DlmCallback() {

					@Override
					public void onSuccess(DlmC c, HResp res) throws Exception {
						System.err.println("success" + "---->");
					}

					@Override
					public void onProcess(DlmC c, float rate) {
						System.err.println(rate + "---->");
					}

					@Override
					public void onProcEnd(DlmC c, HResp res) throws Exception {
						System.err.println("end" + "---->");
					}

					@Override
					public void onExecErr(DlmC c, Throwable e) {
						System.err.println("exe_err" + "---->");
					}

					@Override
					public void onError(DlmC c, Throwable err) throws Exception {
						System.err.println("err" + "---->");
					}
				});
	}
	
	public void stop(View v){
		H.dlm().poll(this.did);
	}
}
