package org.cny.awf.view;

import org.cny.awf.net.http.Args;
import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HCallback.HCacheCallback;
import org.cny.awf.net.http.HResp;
import org.cny.awf.pool.BitmapPool;
import org.cny.awf.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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
	protected final HCacheCallback cback = new HCacheCallback() {

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			ImageView.this.doAnimation(data);
			// System.err.println("---->" + data);
		}

		@Override
		public void onError(CBase c, String cache, Throwable err)
				throws Exception {
			L.debug("load image by url({}) err:", err.getMessage());
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
			return false;
		}
		if (this.url != null && this.url.equals(url)) {
			return true;
		}
		try {
			this.reset_bg();
			this.url = url;
			String curl = H.findCache(this.url);
			if (curl == null) {
				this.setImageDrawable(this.bg);
				H.doGet(this.getContext(), this.url, Args.A("_hc_", "I"),
						this.cback);
			} else {
				this.setImg(curl, this.roundCorner);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void reset_bg() {
		if (this.bg == null) {
			this.bg = this.getBackground();
			this.setBackgroundColor(0);
			this.setImageDrawable(this.bg);
		}
	}

	public void clear() {
		this.reset_bg();
		this.setImageDrawable(this.bg);
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

	protected void doAnimation(String path) {
		// this.setImageURI(Uri.fromFile(new File(path)));
		// System.err.println(path + "---->");
		try {
			this.setImg(path, this.roundCorner);
			Animation an = new AlphaAnimation(0, ImageView.this.getAlpha());
			an.setDuration(this.showTime);
			this.startAnimation(an);
			// this.setBackgroundColor(0);
		} catch (Exception e) {
			L.debug("read bitmap file err:{}", e.getMessage());
		}
	}

}
