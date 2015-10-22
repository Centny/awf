package org.cny.awf.example;

import java.util.List;

import org.cny.awf.net.http.Args;
import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HCallback.HCacheCallback;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.cres.CRes;
import org.cny.awf.net.http.cres.CRes.BaseRes;

import com.google.gson.reflect.TypeToken;

public class ExCRes {

	/**
	 * 
	 * 
	 * ---------------- normal require ----------------
	 * 
	 * 
	 */
	protected HCacheCallback cback = new HCacheCallback() {

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			// do success
		}

		@Override
		public void onError(CBase c, String cache, Throwable err)
				throws Exception {
			// do error.
		}
	};

	public void example1() {
		H.doGet("http://xxx", cback);
		H.doPost("http://www", Args.A("v1", "val1").A("v2", "val2"), cback);
		//
	}

	// using cache policy.
	public void example1_1() {
		// the general HTTP cache,which is this default policy.
		H.doGet("http://xxx?_hc_=N", cback);
		// the image policy,it will not return response data instead of file
		// path.
		H.doGet("http://xxx?_hc_=I", cback);
		// cache only policy.
		H.doGet("http://xxx?_hc_=C", cback);
		// not cache policy
		H.doGet("http://xxx?_hc_=NO", cback);
		//
		//
		H.doPost("http://www", Args.A("v1", "val1").A("_hc_", "I"), cback);
	}

	/**
	 * 
	 * 
	 * ---------------- orm require ----------------
	 * 
	 * 
	 */
	// define object.
	public static class Ex extends BaseRes<Ex> {
		public String key;
		public String val;
		public int type;

		@Override
		public TypeToken<CRes<Ex>> createToken() {
			return new TypeToken<CRes<Ex>>() {
			};
		}

		@Override
		public TypeToken<CRes<List<Ex>>> createTokenL() {
			return new TypeToken<CRes<List<Ex>>>() {
			};
		}
	}

	public static void example2() {
		/*
		 * for data:
		 * {"code":0,"data":[{"key":"a1","val":"123","type":1},{"key":"a2",
		 * "val":"124","type":2}]}
		 */
		H.doGet("http://wwsww", new CRes.HResCallbackL<Ex>(Ex.class) {

			@Override
			public void onError(CBase c, CRes<List<Ex>> cache, Throwable err)
					throws Exception {

			}

			@Override
			public void onSuccess(CBase c, HResp res, CRes<List<Ex>> data)
					throws Exception {

			}

		});
		/*
		 * for data: {"code":0,"data":{"key":"a1","val":"123","type":1}}
		 */
		H.doGet("http://wwsww", new CRes.HResCallback<Ex>(Ex.class) {

			@Override
			public void onError(CBase c, CRes<Ex> cache, Throwable err)
					throws Exception {

			}

			@Override
			public void onSuccess(CBase c, HResp res, CRes<Ex> data)
					throws Exception {

			}

		});
	}
}
