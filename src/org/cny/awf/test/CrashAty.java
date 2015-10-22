package org.cny.awf.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class CrashAty extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crash);
	}

	public void clkCrash(View v) {
		throw new RuntimeException("crash");
	}
}
