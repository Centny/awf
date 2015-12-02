package org.cny.awf.test;

import java.lang.reflect.Type;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.cres.CRes;
import org.cny.awf.view.ImageView;

import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ImageViewActivity extends Activity {

	private ImageView tiv1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view);
		this.tiv1 = (ImageView) this.findViewById(R.id.tiv1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_view, menu);
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

	public void clkShow1(View v) {
		this.tiv1.setUrl("http://f8.topit.me/8/29/96/119867884632a96298o.jpg");
		// for (int i = 0; i < 10; i++) {
		// this.tiv1.setUrl(String.format(Locale.ENGLISH,
		// "http://pb.dev.jxzy.com/img/F1%04d.jpg", i));
		// }
		// this.tiv1.setUrl("");
	}

	public void clkUpload(View v) {
		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		H.doPost(
				"http://fs.dev.jxzy.com/Fsrv/srv/api/uload?token=ae2b1fca515949e5d54fb22b8ed95575-0072e836-cbf9-4027-9910-d489726ba48d&m=C&pub=1",
				"file", bm, new CRes.HResCallbackN<String>() {

					@Override
					protected Type createToken() throws Exception {
						return new TypeToken<CRes<String>>() {
						}.getType();
					}

					@Override
					public void onError(CBase c, CRes<String> cache, Throwable err) throws Exception {

					}

					@Override
					public void onSuccess(CBase c, HResp res, CRes<String> data) throws Exception {
						tiv1.setUrl(data.data);
					}

				});
		// &picType=2
	}
}
