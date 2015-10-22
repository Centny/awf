package org.cny.awf.base;

import java.util.HashMap;
import java.util.Map;

import org.cny.awf.er.ActType;
import org.cny.awf.er.ER;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;

@SuppressLint("Registered")
public class BaseAty extends Activity {

	@Override
	protected void onPause() {
		super.onPause();
		ER.writem(this.getClass(), ER.ACT_OUT, ActType.ATY.getVal());
	}

	@Override
	protected void onResume() {
		super.onResume();
		ER.writem(this.getClass(), ER.ACT_IN, ActType.ATY.getVal());
	}

	public void addm(String action, String key, Object val) {
		Map<String, Object> kvs = new HashMap<String, Object>();
		kvs.put(key, val);
		this.addm(action, kvs);
	}

	public void addm(String action, Map<String, Object> kvs) {
		ER.writem(this.getClass(), action, ActType.N.getVal(), kvs);
	}

	public void onClkRet(View v) {
		this.finish();
	}
}
