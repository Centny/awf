package org.cny.awf.test;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.cny.awf.view.ImageView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class ViewPagerActivity extends Activity {

	ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pager);
		this.pager = (ViewPager) this.findViewById(R.id.viewpager);
		this.pager.setAdapter(new Pager());
		this.pager.setCurrentItem(0);
	}

	public class Pager extends PagerAdapter {
		// View[] xx = new View[1000];
		Queue<View> v = new LinkedBlockingQueue<View>();
		int count = 0;

		public Object getItem(int position) {
			// if (position % 3 == 1) {
			// return String.format(Locale.ENGLISH,
			// "http://pb.dev.jxzy.com/img/F1%04d.jpg", position);
			// } else {
			// return "";
			// }
			return "http://fs.dyfchk2.kuxiao.cn/usr/api/dload?mark=attach-5816abcc27076f1410adf939-1477881127477&type=D_pdfx&token=5816C66C27076F14026BBE36&idx="
					+ (position % 130);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			this.v.add((View) object);
		}

		@Override
		public View instantiateItem(ViewGroup container, final int position) {
			View cview = this.v.poll();
			if (cview == null) {
				cview = LayoutInflater.from(getBaseContext()).inflate(R.layout.view_pager_item, container, false);
				this.count += 1;
			}
			ImageView iv = (ImageView) cview.findViewById(R.id.view_pager_item_i);
			iv.setUrl(this.getItem(position).toString());
			container.addView(cview, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return cview;
		}

		@Override
		public int getCount() {
			return 100;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

}
