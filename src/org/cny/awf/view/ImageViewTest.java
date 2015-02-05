package org.cny.awf.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class ImageViewTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public ImageViewTest() {
		super(MainActivity.class);
	}

	private String ts_ip;

	@Override
	protected void setUp() throws Exception {
		System.out.println("-------------->");
		System.out.println("-------------->");
		super.setUp();
		InputStream in = this.getActivity().getAssets().open("ts_ip.dat");
		assertNotNull("the TServer ip config file is not found", in);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		this.ts_ip = reader.readLine();
		in.close();
		assertNotNull("the TServer ip is not found", this.ts_ip);
		assertFalse("the TServer ip is not found", this.ts_ip.isEmpty());
		System.out.println("test ip:" + ts_ip);
	}

	public void testImageView() throws InterruptedException {
		new ImageView(getActivity()).setUrl("http://1932.332.33");
		ImageView iv = new ImageView(getActivity());
		iv.setUrl("http://" + this.ts_ip + ":8000/ss.png");
		Thread.sleep(1000);
		iv.setUrl("http://" + this.ts_ip + ":8000/ss.png");
		Thread.sleep(1000);
	}
}
