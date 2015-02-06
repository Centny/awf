package org.cny.awf.exmaple;

import java.util.List;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.H;
import org.cny.awf.net.http.HResp;
import org.cny.awf.net.http.cres.CRes;

import com.google.gson.reflect.TypeToken;

public class ExCRes {

	public static class Ex implements CRes.Resable<Ex> {
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

	public static void example1() {
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
