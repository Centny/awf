package org.cny.awf.test;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.PIS;
import org.cny.awf.net.http.cres.CRes;
import org.cny.awf.net.http.f.F.FStrCallback;
import org.cny.awf.net.http.f.FPis;
import org.cny.jwf.util.Donable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

//import com.ipaulpro.afilechooser.utils.FileUtils;

public class FUpActivity extends Activity implements Donable<FPis> {

	private static final int REQUEST_CHOOSER = 1234;
	protected TextView info;
	LocalBroadcastManager lbm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fup);
//		Intent getContentIntent = FileUtils.createGetContentIntent();
//
//		Intent intent = Intent.createChooser(getContentIntent, "Select a file");
//		startActivityForResult(intent, REQUEST_CHOOSER);
//		this.info = (TextView) this.findViewById(R.id.fup_info);
//		this.lbm = LocalBroadcastManager.getInstance(this);
//		this.lbm.registerReceiver(this.brec, new IntentFilter("fup"));
	}

	FStrCallback fsc = new FStrCallback() {

		@Override
		public void onExecErr(CBase c, Throwable e) {
			// TODO Auto-generated method stub
			super.onExecErr(c, e);
		}

		@Override
		public void onProcess(CBase c, PIS pis, float rate) {
			// TODO Auto-generated method stub
			super.onProcess(c, pis, rate);
			info.setText("" + rate * 100);
		}

		@Override
		public void onFSuccess(CBase c, HResp res, FPis img, String url) {
			System.err.println(url);
		}

		@Override
		public void onError(CBase c, CRes<String> cache, Throwable err)
				throws Exception {
			System.err.println(err);
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CHOOSER:
			if (resultCode == RESULT_OK) {

//				final Uri uri = data.getData();
//
//				// Get the File path from the Uri
//				String path = FileUtils.getPath(this, uri);
//
//				// Alternatively, use FileUtils.getFile(Context, Uri)
//				if (path != null && FileUtils.isLocal(path)) {
//					File file = new File(path);
//					try {
//						FPis pis = F.create("file", file);
//						pis.setDoneh(this);
//						F.doPost("http://www.jxzy.com", Args.A("n", "v"), pis,
//								this.fsc);
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
			}
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler h = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			info.setText("" + msg.obj);
		}

	};

	BroadcastReceiver brec = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent it) {
			info.setText(it.getStringExtra("proc"));
		}

	};

	@Override
	public void onProc(FPis tg, long done) {
		// Message m = new Message();
		// m.obj = done;
		// h.sendMessage(m);
		Intent it = new Intent();
		it.putExtra("proc", done + "");
		this.lbm.sendBroadcast(it);
	}
}
