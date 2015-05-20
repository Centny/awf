package org.cny.awf.net.http.cres;

import java.util.List;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.cres.CRes.BaseRes;
import org.cny.awf.net.http.cres.CRes.HResCallback;
import org.cny.awf.net.http.cres.CRes.HResCallbackL;
import org.cny.awf.net.http.cres.CRes.ObjectDeserializer;
import org.cny.awf.net.http.cres.CRes.Pa;
import org.cny.awf.test.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class CResTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public CResTest() {
		super(MainActivity.class);
	}

	public static class Abc extends BaseRes<Abc> implements CRes.Resable<Abc> {
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

	public String tdata4 = "{\"code\":11}";
	public String tdata5 = "{\"code\":11,\"data\":\"sdfs\"}";
	public String tdata6 = "{\"code\":11,\"data\":111}";
	public String tdata7 = "{\"code\":11,\"data\":{\"key\":\"ss\"}}";

	public void testRes2() throws Exception {
		HResCallback<Abc> res = new HResCallback<Abc>(Abc.class) {

			@Override
			public void onError(CBase c, CRes<Abc> cache, Throwable err)
					throws Exception {

			}

			@Override
			public void onSuccess(CBase c, HResp res, CRes<Abc> data)
					throws Exception {
				if (data.data == null) {
					System.err
							.println("sss->" + data.code + "<" + this.sdata());
				} else {
					System.err.println("sss->" + data.code + "<"
							+ data.data.key);
				}
			}

		};
		res.onSuccess(null, null, tdata4);
		res.onSuccess(null, null, tdata5);
		res.onSuccess(null, null, tdata6);
		res.onSuccess(null, null, tdata7);
	}

	public class AXX<T> extends BaseRes<T> implements CRes.Resable<T> {

		@Override
		public TypeToken<CRes<T>> createToken() {
			return new TypeToken<CRes<T>>() {
			};
		}

		@Override
		public TypeToken<CRes<List<T>>> createTokenL() {
			return new TypeToken<CRes<List<T>>>() {
			};
		}
	}

	public void testRes3() {
		TypeToken<CRes<Abc>> tt = new TypeToken<CRes<Abc>>() {
		};
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(Abc.class, new ObjectDeserializer<Abc>());
		Gson gs = gb.create();
		CRes<Abc> ss = gs.fromJson(tdata5, tt.getType());
		System.err.println(ss + "" + ss.data);
	}

//	public void testRes4() {
//		String usr = "testing";
//		String pwd = "123";
//		final Gson gs = new Gson();
//		H.CTX = this.getActivity();
//		SyncH.doGet("http://192.168.2.57:7700/sso/api/login", Args
//				.A("usr", usr).A("pwd", pwd), new HCallback.HDataCallback() {
//
//			@Override
//			public void onError(CBase c, Throwable err) throws Exception {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onSuccess(CBase c, HResp res, String data)
//					throws Exception {
//				CRes<Map<String, Object>> ccc = gs.fromJson(data,
//						new TypeToken<Map<String, Object>>() {
//						}.getType());
//
//				System.err.println(ccc);
//			}
//		});
//	}
}
