package org.cny.awf.net.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;

import org.apache.http.entity.ContentType;
import org.cny.awf.net.http.PIS.PisH;
import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class PISTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public PISTest() {
		super(MainActivity.class);
	}

	@SuppressWarnings("resource")
	public void testPis() throws Exception {
		File dir = this.getActivity().getExternalFilesDir("testing");
		File f = new File(dir, "tt.txt");
		FileWriter fw = new FileWriter(f);
		fw.write("abc");
		fw.close();
		PIS.create("aaa", f).close();
		PIS.create("bbb", f, "text/HTML").close();
		PIS.create("bbb", "sss", f, "text/HTML").close();
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[100]);
		PIS.create("s", "a", bais, 100);
		PIS.create("s", "a", bais, 100, "text/HTML");
		PIS.create("s", "a", bais, 100, ContentType.DEFAULT_BINARY);
		PIS pis = PIS.create("s", "a", bais, 100);
		pis.setCt(pis.getCt());
		pis.setFilename(pis.getFilename());
		pis.setH(pis.getH());
		pis.setName(pis.getName());
		//
		byte[] buf = new byte[10];
		//
		pis = PIS.create("s", "a", bais, 100);
		pis.read(buf, 0, 5);
		pis.read(buf, 0, 5);
		//
		pis = PIS.create("s", "a", bais, 100);
		pis.markSupported();
		pis.mark(100);
		pis.available();
		pis.available();
		pis.markSupported();
		pis.mark(100);
		//
		pis = PIS.create("s", "a", bais, 100);
		pis.read();
		pis.read();
		//
		pis = PIS.create("s", "a", bais, 100);
		pis.reset();
		pis.reset();

		//
		pis = PIS.create("s", "a", bais, 100);
		pis.skip(1);
		pis.skip(1);

		pis = PIS.create("s", "a", bais, 100);
		pis.setH(new PisH() {

			@Override
			public void onProcess(PIS pis, float rate) {

			}
		});
		pis.onProcess(pis, 10);

		PIS.FileInputStream ff = new PIS.FileInputStream();
		ff.f = f;
		ff.createIn().close();
	}
}
