package org.cny.awf.test;

import org.cny.awf.R;
import org.cny.awf.base.BaseAty;
import org.cny.awf.util.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends BaseAty {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Intent service = new Intent(this,ImSrv.class);
		// this.startService(service);
		// throw new RuntimeException();
		System.err.println(Util.DevInfo(this));
		System.err.println(Util.SysInfo(this));
		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {

					@Override
					public void onReceive(Context arg0, Intent arg1) {
						System.err.println("----->");
						addm("Broadcast", "a", "val");
					}
				}, new IntentFilter("sss"));
		System.out.println(LocalBroadcastManager.getInstance(this)
				.sendBroadcast(new Intent("sss")));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
