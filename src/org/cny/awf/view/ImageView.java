package org.cny.awf.view;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.cny.awf.net.http.Args;
import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HCallback.HBitmapCallback;
import org.cny.awf.net.http.HResp;
import org.cny.awf.pool.BitmapPool;
import org.cny.awf.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class ImageView extends android.widget.ImageView {
	private static final Logger L = LoggerFactory.getLogger(ImageView.class);
	// protected static final ColorDrawable CLS = new ColorDrawable();
	/**
	 * target URL.
	 */
	protected String url;
	protected int roundCorner = 0;
	protected int showTime = 500;
	protected Drawable bg;
	protected boolean usingBytePool = true;
	//
	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "ImgTask #" + mCount.getAndIncrement());
		}
	};
	private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);

	public static Executor IMG_POOL_EXECUTOR = new ThreadPoolExecutor(1, 3, 3, TimeUnit.SECONDS, sPoolWorkQueue,
			sThreadFactory);

	protected class ImgCallback extends HBitmapCallback {
		public String turl;

		public Bitmap img;

		public ImgCallback(String url, int roundCorner) {
			super(roundCorner);
			this.turl = url;
		}

		@Override
		public void onSuccess(CBase c, HResp res, Bitmap img) throws Exception {
			ImageView.this.onSuccess(this, c, res, img);
		}

		@Override
		public void onError(CBase c, Bitmap cache, Throwable err) throws Exception {
			ImageView.this.onError(this, c, cache, err);
		}

		@Override
		public int getImgWidth() {
			return ImageView.this.getImgWidth();
		}

		@Override
		public int getImgHeight() {
			return ImageView.this.getImgHeight();
		}

		@Override
		public int getImgMaxWidth() {
			return ImageView.this.getImgMaxWidth();
		}

		@Override
		public int getImgMaxHeight() {
			return ImageView.this.getImgMaxHeight();
		}

	};

	public void onSuccess(ImgCallback cb, CBase c, HResp res, Bitmap img) throws Exception {
		if (cb.turl.equals(this.url)) {
			cb.img = img;
			this.doAnimationH(cb);
		}
	}

	public void onError(ImgCallback cb, CBase c, Bitmap cache, Throwable err) throws Exception {
		if (cb.turl.equals(this.url)) {
			this.reset_bg_cbH(cb);
		}
	}

	public int getImgWidth() {
		ViewGroup.LayoutParams lo = this.getLayoutParams();
		if (lo == null) {
			return 0;
		}
		int w = lo.width;
		if (w > 0) {
			return w;
		} else {
			return 0;
		}
	}

	public int getImgHeight() {
		ViewGroup.LayoutParams lo = this.getLayoutParams();
		if (lo == null) {
			return 0;
		}
		int h = lo.height;
		if (h > 0) {
			return h;
		} else {
			return 0;
		}
	}

	@SuppressWarnings("deprecation")
	public int getImgMaxWidth() {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	@SuppressWarnings("deprecation")
	public int getImgMaxHeight() {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

	///
	public ImageView(Context context) {
		super(context);
	}

	public ImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public String getUrl() {
		return url;
	}

	public boolean isUrl(String turl) {
		if (this.url == null) {
			return turl == null;
		} else {
			return this.url.equals(turl);
		}
	}

	public synchronized boolean setUrl(String url) {
		if (Util.isNullOrEmpty(url)) {
			this.url = "";
			this.reset_bg();
			return false;
		}
		if (this.url != null && this.url.equals(url)) {
			return true;
		}
		try {
			this.reset_bg();
			this.url = url;
			ImgCallback imgc = new ImgCallback(this.url, this.roundCorner);
			org.cny.awf.pool.UrlKey key = org.cny.awf.pool.UrlKey.create(CBase.parseUrl(this.url), null,
					this.roundCorner, imgc.getImgWidth(), imgc.getImgHeight());
			Bitmap img;
			if (this.usingBytePool) {
				img = BitmapPool.bytedCache(key);
			} else {
				img = BitmapPool.cache(key);
			}
			if (img == null) {
				H.doGetNH(this.getContext(), IMG_POOL_EXECUTOR, this.url, Args.A("_hc_", "I"), null, imgc);
			} else {
				this.setImageBitmap(img);
			}
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	protected void reset_bg() {
		this.url = null;
		if (this.bg == null) {
			this.bg = this.getDrawable();
		}
		if (this.bg != null) {
			if (this.bg instanceof BitmapDrawable) {
				this.setImageBitmap(((BitmapDrawable) this.bg).getBitmap());
			} else {
				this.setImageDrawable(this.bg);
			}
		}
	}

	protected void reset_bg_cb(ImgCallback cb) {
		if (cb.turl.equals(this.url)) {
			this.reset_bg();
		}
	}

	protected void reset_bg_cbH(ImgCallback cb) {
		Message msg = new Message();
		msg.what = 3;
		msg.obj = new Pair<ImageView, ImgCallback>(ImageView.this, cb);
		h.sendMessage(msg);
	}

	public void clear() {
		this.reset_bg();
	}

	public int getRoundCorner() {
		return roundCorner;
	}

	public void setRoundCorner(int roundCorner) {
		this.roundCorner = roundCorner;
	}

	/**
	 * @return the showTime
	 */
	public int getShowTime() {
		return showTime;
	}

	/**
	 * @param showTime
	 *            the showTime to set
	 */
	public void setShowTime(int showTime) {
		this.showTime = showTime;
	}

	protected void setImg(String url, int rc) throws Exception {
		this.setImageBitmap(BitmapPool.dol(url, rc));
	}

	public void doAnimation(Bitmap img) {
		this.setImageBitmap(img);
		this.doAnimation();
	}

	protected void doAnimation(ImgCallback cb) {
		if (cb.turl.equals(url)) {
			this.setImageBitmap(cb.img);
			this.doAnimation();
		}
	}

	public void doAnimationH(Bitmap img) {
		Message msg = new Message();
		msg.what = 1;
		msg.obj = new Pair<ImageView, Bitmap>(ImageView.this, img);
		h.sendMessage(msg);
	}

	protected void doAnimationH(ImgCallback cb) {
		Message msg = new Message();
		msg.what = 2;
		msg.obj = new Pair<ImageView, ImgCallback>(ImageView.this, cb);
		h.sendMessage(msg);
	}

	public void doAnimation(String path) {
		try {
			this.setImg(path, this.roundCorner);
			this.doAnimation();
		} catch (Throwable e) {
			L.error("read bitmap file err:{}", e.getMessage());
		}
	}

	public void doAnimation() {
		Animation an = new AlphaAnimation(0, ImageView.this.getAlpha());
		an.setDuration(this.showTime);
		this.startAnimation(an);
	}

	public boolean isUsingBytePool() {
		return usingBytePool;
	}

	public void setUsingBytePool(boolean usingBytePool) {
		this.usingBytePool = usingBytePool;
	}

	private static Handler h = new Handler() {

		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Pair<ImageView, Bitmap> img = (Pair<ImageView, Bitmap>) msg.obj;
				img.first.doAnimation(img.second);
				break;
			case 2:
				Pair<ImageView, ImgCallback> cb = (Pair<ImageView, ImgCallback>) msg.obj;
				cb.first.doAnimation(cb.second);
				break;
			case 3:
				cb = (Pair<ImageView, ImgCallback>) msg.obj;
				cb.first.reset_bg_cb(cb.second);
				break;
			}
		}

	};
}
