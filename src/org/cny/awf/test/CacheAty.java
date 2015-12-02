package org.cny.awf.test;

import java.util.List;
import java.util.Map;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HCallback;
import org.cny.awf.net.http.HResp;
import org.cny.awf.view.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CacheAty extends Activity {
	private static final Logger L = LoggerFactory.getLogger(CacheAty.class);
	private TextView info;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache);
		this.info = (TextView) this.findViewById(R.id.cache_info);
		this.list = (ListView) this.findViewById(R.id.cache_list);
		CacheAdapter adp = new CacheAdapter();
		this.list.setAdapter(adp);
		adp.refresh();
	}

	public class CacheAdapter extends BaseAdapter {
		public List<Map<String, Object>> datas;

		public void refresh() {
			info.setText("刷新中...");
			H.doGet("http://www.kuxiao.cn/get-course-list?param={\"pa\":{\"pn\":1,\"ps\":50}}",
					new HCallback.GMapCallback() {

						@Override
						public void onCache(CBase c, HResp res, Map<String, Object> data) {
							this.onData(data);
							// info.setText("");
						}

						@Override
						public void onSuccess(CBase c, HResp res, Map<String, Object> data) throws Exception {
							this.onData(data);
							info.setText("");
						}

						@Override
						public void onError(CBase c, Map<String, Object> cache, Throwable err) throws Exception {
							L.error("load error->{}", err);
							info.setText("");
						}

						@SuppressWarnings("unchecked")
						private void onData(Map<String, Object> data) {
							if (((Number) data.get("code")).intValue() != 0) {
								L.error("load error->{}", data.get("msg"));
								return;
							}
							datas = (List<Map<String, Object>>) data.get("data");
							notifyDataSetChanged();
						}
					});
		}

		@Override
		public int getCount() {
			if (this.datas == null) {
				return 0;
			} else {
				return this.datas.size();
			}
		}

		@Override
		public Object getItem(int position) {
			return this.datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View cview, ViewGroup parent) {
			ViewHolder vh = null;
			if (cview == null) {
				cview = LayoutInflater.from(getBaseContext()).inflate(R.layout.cache_list_item, parent, false);
				vh = new ViewHolder();
				vh.iv = (ImageView) cview.findViewById(R.id.cache_list_item_img);
				vh.title = (TextView) cview.findViewById(R.id.cache_list_item_title);
				vh.desc = (TextView) cview.findViewById(R.id.cache_list_item_desc);
				cview.setTag(vh);
			} else {
				vh = (ViewHolder) cview.getTag();
			}
			vh.update(this.datas.get(position));
			return cview;
		}
	}

	private static class ViewHolder {
		ImageView iv;
		TextView title;
		TextView desc;
		boolean asc;

		public void update(Map<String, Object> data) {
			if (data.containsKey("imgs")) {
				if (asc) {
					iv.setUrl(data.get("imgs").toString().split(",")[0] + "?a=1&b=2&c=3");
					asc = false;
				} else {
					iv.setUrl(data.get("imgs").toString().split(",")[0] + "?c=3&b=2&a=1");
					asc = true;
				}
			} else {
				iv.setUrl("");
			}
			if (data.containsKey("name")) {
				title.setText(data.get("name").toString());
			} else {
				title.setText("");
			}
			if (data.containsKey("time")) {
				desc.setText(data.get("time").toString());
			} else {
				desc.setText("");
			}
		}
	}
}
