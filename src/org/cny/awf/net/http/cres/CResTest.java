package org.cny.awf.net.http.cres;

import java.util.List;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.cres.CRes.HResCallback;
import org.cny.awf.net.http.cres.CRes.HResCallbackL;
import org.cny.awf.net.http.cres.CRes.Pa;
import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

import com.google.gson.reflect.TypeToken;

public class CResTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public CResTest() {
		super(MainActivity.class);
	}

	public static class Abc implements CRes.Resable<Abc> {
		public String key;
		public String val;
		public int type;

		@Override
		public TypeToken<CRes<Abc>> createToken() {
			return new TypeToken<CRes<Abc>>() {
			};
		}

		@Override
		public TypeToken<CRes<List<Abc>>> createTokenL() {
			return new TypeToken<CRes<List<Abc>>>() {
			};
		}
	}

	public String tdata1 = "{\"code\":0,\"data\":[{\"key\":\"a1\",\"val\":\"123\",\"type\":1},{\"key\":\"a2\",\"val\":\"124\",\"type\":2}]}";
	public String tdata2 = "{\"code\":0,\"data\":{\"key\":\"a1\",\"val\":\"123\",\"type\":1}}";
	public String tdata3 = "{\"code\":0,\"pa\":{\"pn\":0,\"ps\":20,\"total\":100},\"data\":[{\"key\":\"a1\",\"val\":\"123\",\"type\":1},{\"key\":\"a2\",\"val\":\"124\",\"type\":2}]}";

	public void testRes() throws Exception {
		// HResCallbackL.Res res = new Gson().fromJson(tdata1,
		// new TypeToken<HResCallbackL.Res<Abc>>() {
		// }.getType());
		new HResCallbackL<Abc>(Abc.class) {

			@Override
			public void onError(CBase c, CRes<List<Abc>> cache, Throwable err)
					throws Exception {

			}

			@Override
			public void onSuccess(CBase c, HResp res, CRes<List<Abc>> data)
					throws Exception {
				assertEquals(0, data.code);
				data.setCode(data.getCode());
				data.setData(data.getData());
				data.setDmsg(data.getDmsg());
				data.setMsg(data.getMsg());
				for (Abc abc : data.data) {
					System.err.println(abc.key + "->" + abc.val + ","
							+ abc.type);
				}
			}

		}.onSuccess(null, null, tdata1);
		HResCallback<Abc> rcl;
		rcl = new HResCallback<Abc>(Abc.class) {

			@Override
			public void onError(CBase c, CRes<Abc> cache, Throwable err)
					throws Exception {

			}

			@Override
			public void onSuccess(CBase c, HResp res, CRes<Abc> data)
					throws Exception {
				if (data == null) {
					return;
				}
				data.setCode(data.getCode());
				data.setData(data.getData());
				data.setDmsg(data.getDmsg());
				data.setMsg(data.getMsg());
				Abc abc = data.data;
				System.err.println(abc.key + "->" + abc.val + "," + abc.type);

			}

		};
		rcl.onSuccess(null, null, tdata2);
		rcl.onSuccess(null, null, (String) null);
		rcl.onSuccess(null, null, "");
		rcl.onError(null, tdata2, null);
		rcl.onError(null, (String) null, null);
		rcl.onError(null, "", null);
		new HResCallbackL<Abc>(Abc.class) {

			@Override
			public void onError(CBase c, CRes<List<Abc>> cache, Throwable err)
					throws Exception {

			}

			@Override
			public void onSuccess(CBase c, HResp res, CRes<List<Abc>> data)
					throws Exception {
				assertEquals(0, data.code);
				assertEquals(0, data.pa.pn);
				assertEquals(20, data.pa.ps);
				assertEquals(100, data.pa.total);
				Pa pa = new Pa();
				pa.setPn(10);
				pa.setPs(10);
				pa.setTotal(100);
				data.setPa(pa);
			}

		}.onSuccess(null, null, tdata3);
	}
}
