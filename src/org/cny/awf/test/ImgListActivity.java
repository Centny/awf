package org.cny.awf.test;

import java.util.Locale;

import org.cny.awf.view.ImageView;

import android.app.Activity;
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
	}

	public class ImgListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 1000;
		}

		@Override
		public Object getItem(int position) {
			return String.format(Locale.ENGLISH,
					"http://pb.dev.jxzy.com/img/F1%04d.jpg", position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View cview, ViewGroup parent) {
			ViewHolder vh = null;
			if (cview == null) {
				cview = LayoutInflater.from(getBaseContext()).inflate(
						R.layout.img_list_item, parent, false);
				vh = new ViewHolder();
				vh.iv = (ImageView) cview.findViewById(R.id.img_list_item_i);
				cview.setTag(vh);
			} else {
				vh = (ViewHolder) cview.getTag();
			}
			vh.iv.setUrl(this.getItem(position).toString());
			return cview;
		}
	}

	private static class ViewHolder {
		ImageView iv;
	}
}
