package org.cny.awf.net.http.cres;

import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.cny.awf.net.http.cres.CRes.HResCallbackLS;
import org.cny.awf.net.http.cres.CRes.HResCallbackS;
import org.cny.awf.net.http.cres.CRes.Resable;

public class SyncH {

	public static <T> CRes<T> doGetS(String url, List<BasicNameValuePair> args,
			Class<? extends Resable<?>> cls) {
		HResCallbackS<T> res = new HResCallbackS<T>(cls);
		org.cny.awf.net.http.SyncH.doGet(url, args, res);
		if (res.err == null) {
			return res.data;
		} else {
			throw new RuntimeException(res.err);
		}
	}

	public static <T> CRes<List<T>> doGetLS(String url,
			List<BasicNameValuePair> args, Class<? extends Resable<T>> cls) {
		HResCallbackLS<T> res = new HResCallbackLS<T>(cls);
		org.cny.awf.net.http.SyncH.doGet(url, args, res);
		if (res.err == null) {
			return res.data;
		} else {
			throw new RuntimeException(res.err);
		}
	}
}
