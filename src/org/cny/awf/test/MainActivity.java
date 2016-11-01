package org.cny.awf.test;

import java.util.ArrayList;
import java.util.List;

import org.cny.awf.base.BaseAty;
import org.cny.awf.net.http.Args;
import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HCallback;
import org.cny.awf.net.http.HResp;
import org.cny.awf.util.Util;
import org.cny.awf.view.ImageView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends BaseAty {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Intent service = new Intent(this,ImSrv.class);
		// this.startService(service);
		// throw new RuntimeException();
		System.err.println(Util.DevInfo(this));
		System.err.println(Util.SysInfo(this));
		LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				System.err.println("----->");
				addm("Broadcast", "a", "val");
			}
		}, new IntentFilter("sss"));
		System.out.println(LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("sss")));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void toImageView(View v) {
		this.startActivity(new Intent(this, ImageViewActivity.class));
	}

	public void toCrash(View v) {
		this.startActivity(new Intent(this, CrashAty.class));
	}

	public void toFUp(View v) {
		this.startActivity(new Intent(this, FUpActivity.class));
	}

	public void toImgList(View v) {
		this.startActivity(new Intent(this, ImgListActivity.class));
	}

	public void toCache(View v) {
		this.startActivity(new Intent(this, CacheAty.class));
	}

	public void toDlm(View v) {
		this.startActivity(new Intent(this, DlmAty.class));
	}

	public void toViewPagerActivity(View v) {
		this.startActivity(new Intent(this, ViewPagerActivity.class));
	}

	public void toThread(View v) {
		// Bitmap bm = BitmapFactory.decodeResource(this.getResources(),
		// R.drawable.ic_launcher);
		// BitmapByte bb = new BitmapByte(bm);
		// Bitmap xx = bb.create();
		// try {
		// this.testPool();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	List<byte[]> m = new ArrayList<byte[]>();
	List<Bitmap> bm = new ArrayList<Bitmap>();

	public void toMemory(View v) {
		// try {
		// System.out.println("awf->" + this.m.size() * 100 + "MB");
		// this.m.add(new byte[100 * 1024 * 1024]);
		// } catch (OutOfMemoryError e) {
		// e.printStackTrace();
		// }
		try {
			if (this.bm.size() > 0) {
				System.out.println("awf->" + bm.size() * this.bm.get(0).getByteCount() + "MB");
			}
			this.m.add(new byte[100 * 1024 * 1024]);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		H.doGetNH(this, ImageView.IMG_POOL_EXECUTOR,
				"http://fs.dyfchk2.kuxiao.cn/usr/api/dload?mark=attach-5816abcc27076f1410adf939-1477881127477&type=D_pdfx&token=5816C66C27076F14026BBE36&idx=0",
				Args.A("_hc_", "I"), null, new HCallback.HDataCallback() {

					// @Override
					// public void onSuccess(CBase c, HResp res, Bitmap img)
					// throws Exception {
					// // TODO Auto-generated method stub
					// System.out.println("done....->" +
					// ImageView.sPoolWorkQueue.size());
					// img.recycle();
					// }
					//
					// @Override
					// public void onError(CBase c, Bitmap cache, Throwable
					// err) throws Exception {
					// // TODO Auto-generated method stub
					// System.out
					// .println("error....->" + err.getMessage() + "->" +
					// ImageView.sPoolWorkQueue.size());
					// }

					@Override
					public void onError(CBase c, Throwable err) throws Exception {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(CBase c, HResp res, String data) throws Exception {
						try {
							bm.add(Util.readBitmap(data));
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
						}
					}
				});
	}

	//
	// private static final ThreadFactory sThreadFactory = new ThreadFactory() {
	// private final AtomicInteger mCount = new AtomicInteger(1);
	//
	// public Thread newThread(Runnable r) {
	// return new Thread(r, "ImgTask #" + mCount.getAndIncrement());
	// }
	// };
	//
	// private static final BlockingQueue<Runnable> sPoolWorkQueue = new
	// LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE);
	//
	// public static Executor IMG_POOL_EXECUTOR = new ThreadPoolExecutor(3, 3,
	// 60, TimeUnit.SECONDS, sPoolWorkQueue,
	// sThreadFactory);

	public void testPool() throws InterruptedException {
		for (int i = 0; i < 100; i++) {

			// ImageView.IMG_POOL_EXECUTOR.execute(new Runnable() {
			//
			// @Override
			// public void run() {
			// try {
			// Thread.sleep(3000);
			// System.out.println("done....->" +
			// ImageView.sPoolWorkQueue.size());
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// }
			// });
			// new ATask().executeOnExecutor(ImageView.IMG_POOL_EXECUTOR, 0f);
			H.doGetNH(this, ImageView.IMG_POOL_EXECUTOR, "http://rcp.dev.gdy.io", Args.A("_hc_", "I"), null,
					new HCallback.HDataCallback() {

						@Override
						public void onError(CBase c, Throwable err) throws Exception {
							sleep();
						}

						@Override
						public void onSuccess(CBase c, HResp res, String data) throws Exception {
							sleep();
						}
					});
		}
	}

	public void sleep() {
		try {
			Thread.sleep(3000);
			// System.out.println("done....->" +
			// ImageView.sPoolWorkQueue.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public class ATask extends AsyncTask<Float, Float, Float> {

		@Override
		protected Float doInBackground(Float... params) {

			return 0f;
		}

		@Override
		protected void onPostExecute(Float result) {
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			// cback.onProcess(HAsyncTask.this, values[0]);
		}

		public void onProcess(float rate) {
		}
	};
}
