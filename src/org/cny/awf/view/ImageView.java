package org.cny.awf.view;

import java.io.File;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HCallback.HCacheCallback;
import org.cny.awf.net.http.HResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class ImageView extends android.widget.ImageView {
	protected static final Logger L = LoggerFactory.getLogger(ImageView.class);
	protected static final ColorDrawable CLS = new ColorDrawable();
	/**
	 * target URL.
	 */
	protected String url;
	protected HCacheCallback cback = new HCacheCallback() {

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			ImageView.this.doAnimation(data);
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

	public void setUrl(String url) {
		this.url = url;
		this.setImageDrawable(CLS);
		H.doGet(this.getContext(), this.url + "?_hc_=I", this.cback);
	}

	protected void doAnimation(String path) {
		ImageView.this.setImageURI(Uri.fromFile(new File(path)));
		Animation an = new AlphaAnimation(0, ImageView.this.getAlpha());
		an.setDuration(800);
		this.startAnimation(an);
	}

}
