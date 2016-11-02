package org.cny.awf.test;

import org.cny.awf.view.ImageView;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ImgListActivity extends Activity {

	private ListView ilist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_img_list);
		this.ilist = (ListView) this.findViewById(R.id.imgl);
		this.ilist.setAdapter(new ImgListAdapter());
		ActivityManager.MemoryInfo info = this.getAvailableMemory();
		System.out.println(info.availMem / 1024 / 1024);
		// for (UrlKey key : BitmapPool.instance().keys) {
		// System.out.println("A->" + key + "->");
		//
		// }
		// for (String key : BitmapBytePool.instance().keys) {
		// System.out.println("B->" + key);
		// }
		// for (String url : this.data) {
		// org.cny.awf.pool.UrlKey key =
		// org.cny.awf.pool.UrlKey.create(CBase.parseUrl(url), null, 0, 0, 0);
		// System.out.println("C->" + BitmapPool.cache(key) == null);
		// }
	}

	private ActivityManager.MemoryInfo getAvailableMemory() {
		ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		return memoryInfo;
	}

	String[] data = new String[] { "http://fs.dev.gdy.io/bD9FnA\u003d\u003d", "http://fs.dev.gdy.io/mPfG6t\u003d\u003d",
			"http://fs.dev.gdy.io/\u003dv5Gi1\u003d\u003d", "http://fs.dev.gdy.io/g\u003dbcQx\u003d\u003d",
			"http://fs.dev.gdy.io/wtf\u003d2t\u003d\u003d", "http://fs.dev.gdy.io/tq0zzt\u003d\u003d",
			"http://fs.dev.gdy.io/ucgV7x\u003d\u003d", "http://fs.dev.gdy.io/TXkRYt\u003d\u003d",
			"http://fs.dev.gdy.io/VnDu\u003dA\u003d\u003d" };

	public class ImgListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 1000;
		}

		@Override
		public Object getItem(int position) {
			// if (position % 3 == 1) {
			// return String.format(Locale.ENGLISH,
			// "http://pb.dev.jxzy.com/img/F1%04d.jpg", position);
			// } else {
			// return "";
			// }
			// return
			// "http://fs.dyfchk2.kuxiao.cn/usr/api/dload?mark=attach-5816abcc27076f1410adf939-1477881127477&type=D_pdfx&token=5816C66C27076F14026BBE36&idx="
			// + (position % 130);
			return data[position % data.length];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View cview, ViewGroup parent) {
			ViewHolder vh = null;
			if (cview == null) {
				cview = LayoutInflater.from(getBaseContext()).inflate(R.layout.img_list_item, parent, false);
				vh = new ViewHolder();
				vh.iv = (ImageView) cview.findViewById(R.id.img_list_item_i);
				cview.setTag(vh);
			} else {
				vh = (ViewHolder) cview.getTag();
			}
			// H.doGetNH(ImgListActivity.this, ImageView.IMG_POOL_EXECUTOR,
			// this.getItem(position).toString(),
			// Args.A("_hc_", "I"), null, new HCallback.HDataCallback() {
			//
			// // @Override
			// // public void onSuccess(CBase c, HResp res, Bitmap img)
			// // throws Exception {
			// // // TODO Auto-generated method stub
			// // System.out.println("done....->" +
			// // ImageView.sPoolWorkQueue.size());
			// // img.recycle();
			// // }
			// //
			// // @Override
			// // public void onError(CBase c, Bitmap cache, Throwable
			// // err) throws Exception {
			// // // TODO Auto-generated method stub
			// // System.out
			// // .println("error....->" + err.getMessage() + "->" +
			// // ImageView.sPoolWorkQueue.size());
			// // }
			//
			// @Override
			// public void onError(CBase c, Throwable err) throws Exception {
			// // TODO Auto-generated method stub
			//
			// }
			//
			// @Override
			// public void onSuccess(CBase c, HResp res, String data) throws
			// Exception {
			// // TODO Auto-generated method stub
			// System.out.println("done....->" +
			// ImageView.sPoolWorkQueue.size());
			// // Util.readBitmap(data);
			// // BitmapPool.dol(data);
			// UrlKey.create(c.getFullUrl(), data, 0, 0, 0, 1468, 2024).read();
			// // BitmapPool.dol();
			//
			// }
			// });
			vh.iv.setUrl(this.getItem(position).toString());
			return cview;
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

	private static class ViewHolder {
		ImageView iv;
	}
}
