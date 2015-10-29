package org.cny.awf.view;

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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class ImageView extends android.widget.ImageView {
	protected static final Logger L = LoggerFactory.getLogger(ImageView.class);
	// protected static final ColorDrawable CLS = new ColorDrawable();
	/**
	 * target URL.
	 */
	protected String url;
	protected int roundCorner = 0;
	protected int showTime = 500;
	protected Drawable bg;

	protected class ImgCallback extends HBitmapCallback {
		protected String turl;

		public ImgCallback(String url, int roundCorner) {
			super(roundCorner);
			this.turl = url;
		}

		@Override
		public void onSuccess(CBase c, HResp res, Bitmap img) throws Exception {
			if (this.turl.equals(url)) {
				ImageView.this.doAnimationH(img);
			}
		}

		@Override
		public void onError(CBase c, Bitmap cache, Throwable err) throws Exception {
			if (this.turl.equals(url)) {
				if (cache == null) {
					ImageView.this.reset_bg();
				} else {
					ImageView.this.doAnimationH(cache);
				}
			}
		}

		@Override
		public int getImgWidth() {
			int w = ImageView.this.getLayoutParams().width;
			if (w > 0) {
				return w;
			} else {
				return 0;
			}
		}

		@Override
		public int getImgHeight() {
			int h = ImageView.this.getLayoutParams().height;
			if (h > 0) {
				return h;
			} else {
				return 0;
			}
		}

	};

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

	public boolean setUrl(String url) {
		if (Util.isNullOrEmpty(url)) {
			this.reset_bg();
			return false;
		}
		if (this.url != null && this.url.equals(url)) {
			return true;
		}
		try {
			this.reset_bg();
			this.url = url;
			Bitmap img = BitmapPool.cache(this.url, this.roundCorner, this.getLayoutParams().width,
					this.getLayoutParams().height);
			if (img == null) {
				H.doGetNH(this.getContext(), this.url, Args.A("_hc_", "I"), null,
						new ImgCallback(this.url, this.roundCorner));
			} else {
				this.setImageBitmap(img);
			}
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	private void reset_bg() {
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

	public void doAnimationH(Bitmap img) {
		Message msg = new Message();
		msg.obj = new Pair<ImageView, Bitmap>(ImageView.this, img);
		h.sendMessage(msg);
	}

	public void doAnimation(String path) {
		try {
			this.setImg(path, this.roundCorner);
			this.doAnimation();
		} catch (Throwable e) {
			L.debug("read bitmap file err:{}", e.getMessage());
		}
	}

	public void doAnimation() {
		Animation an = new AlphaAnimation(0, ImageView.this.getAlpha());
		an.setDuration(this.showTime);
		this.startAnimation(an);
	}

	private static Handler h = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			@SuppressWarnings("unchecked")
			Pair<ImageView, Bitmap> img = (Pair<ImageView, Bitmap>) msg.obj;
			img.first.doAnimation(img.second);
		}

	};
}
