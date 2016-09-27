package org.cny.awf.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.dlm.DlmC;
import org.cny.awf.net.http.dlm.DlmCallback;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DlmAty extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dlm);
		this.adapter = new ListViewAdapter();
		((ListView) this.findViewById(R.id.tasks)).setAdapter(this.adapter);
	}

	public class ListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return idList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return idList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = new TextView(getBaseContext());
			String id = idList.get(position);
			tv.setText(id + "->" + speedString(speedList.get(id) * 1000));
			return tv;
		}

	}

	private String speedString(float speed) {
		if (speed < 1024) {
			return (int) speed + "B/s";
		}
		speed /= 1024;
		if (speed < 1024) {
			return (int) speed + "KB/s";
		}
		speed /= 1024;
		if (speed < 1024) {
			return (int) speed + "MB/s";
		}
		speed /= 1024;
		return (int) speed + "GB/s";
	}

	public String did;
	public ListView tasks;
	public BaseAdapter adapter;
	public List<String> idList = new ArrayList<String>();
	public Map<String, Float> speedList = new HashMap<String, Float>();
	public long lastNotify;

	public synchronized void tryNotify(boolean refresh) {
		long now = new Date().getTime();
		if (!refresh && now - this.lastNotify < 1000) {
			return;
		}
		this.idList.clear();
		this.idList.addAll(this.speedList.keySet());
		Message msg = new Message();
		msg.obj = this;
		handler.sendMessage(msg);
		this.lastNotify = now;
	}

	private static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			((DlmAty) msg.obj).adapter.notifyDataSetChanged();
		}

	};

	private void addDown(String url, String name) {
		File xx = new File(Environment.getExternalStorageDirectory(), "xxx" + name);
		xx.delete();
		this.did = H.doGet(url, xx.getAbsolutePath(), new DlmCallback() {

			@Override
			public void onSuccess(DlmC c, HResp res) throws Exception {
				System.err.println("success" + "---->");
				speedList.remove(c.getFullUrl());
				tryNotify(true);
			}

			@Override
			public void onProcess(DlmC c, float speed, float rate) {
				System.err.println(c.id + "-->" + (speed * 1000 / 1024) + "->" + rate + "---->");
				speedList.put(c.getFullUrl(), speed);
				tryNotify(false);
			}

			@Override
			public void onProcEnd(DlmC c, HResp res) throws Exception {
				System.err.println("end" + "---->");
			}

			@Override
			public void onExecErr(DlmC c, Throwable e) {
				System.err.println("exe_err" + "---->");
				e.printStackTrace();
				speedList.remove(c.getFullUrl());
				tryNotify(true);
			}

			@Override
			public void onError(DlmC c, Throwable err) throws Exception {
				System.err.println("err" + "---->");
				err.printStackTrace();
				speedList.remove(c.getFullUrl());
				tryNotify(true);
			}
		});
	}

	public void start(View v) {
		this.addDown("http://pb.dev.jxzy.com/mcb-e.linux.zip", "l");
		for (int i = 0; i < 10; i++) {
			String url = String.format(Locale.ENGLISH, "http://pb.dev.jxzy.com/img/F100%02d.jpg", i);
			this.addDown(url, i + "");
		}
	}

	public void stop(View v) {
		H.dlm().poll(this.did);
	}
}
