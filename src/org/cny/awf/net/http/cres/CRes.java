package org.cny.awf.net.http.cres;

import java.util.List;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.HCallback.HCacheCallback;
import org.cny.awf.net.http.HResp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Package org.cny.awf.net.http.cres provide the the common HTTP result call
 * back and convert the json to object. <br/>
 * the json data format is:<br/>
 * {"code":0,"data":object or list}<br/>
 * <br/>
 * for example to see:org.cny.awf.example.CRes
 * 
 * @author cny
 *
 * @param <T>
 *            the target call will be parsed by Gson.
 */
public class CRes<T> {
	/**
	 * the response code.
	 */
	public int code;
	/**
	 * the data object.
	 */
	public T data;
	/**
	 * the error message.
	 */
	public String msg;
	/**
	 * the debug message.
	 */
	public String dmsg;

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the dmsg
	 */
	public String getDmsg() {
		return dmsg;
	}

	/**
	 * @param dmsg
	 *            the dmsg to set
	 */
	public void setDmsg(String dmsg) {
		this.dmsg = dmsg;
	}

	/**
	 * common result call back.
	 * 
	 * @author cny
	 *
	 * @param <T>
	 *            the target class will be parsed by Gson.
	 */
	public static abstract class HResCallback<T> extends HCacheCallback {

		protected Class<? extends Resable<?>> cls;
		protected Gson gs = new Gson();

		public HResCallback(Class<? extends Resable<?>> cls) {
			this.cls = cls;
		}

		@SuppressWarnings("unchecked")
		public CRes<T> fromJson(String data) throws Exception {
			CRes.Resable<T> rt = (Resable<T>) this.cls.newInstance();
			return (CRes<T>) this.gs.fromJson(data, rt.createToken().getType());
		}

		@Override
		public void onError(CBase c, String cache, Throwable err)
				throws Exception {
			if (cache == null || cache.isEmpty()) {
				this.onError(c, (CRes<T>) null, err);
			} else {
				this.onError(c, (CRes<T>) this.fromJson(cache), err);
			}
		}

		@Override
		public void onSuccess(CBase c, HResp res, String data) throws Exception {
			if (data == null || data.isEmpty()) {
				this.onSuccess(c, res, (CRes<T>) null);
			} else {
				this.onSuccess(c, res, (CRes<T>) this.fromJson(data));
			}
		}

		/**
		 * HTTP on error call back.
		 * 
		 * @param c
		 *            the CBase.
		 * @param cache
		 *            the data object.
		 * @param err
		 *            the error.
		 * @throws Exception
		 */
		public abstract void onError(CBase c, CRes<T> cache, Throwable err)
				throws Exception;

		/**
		 * HTTP on success call back.
		 * 
		 * @param c
		 *            the CBase.
		 * @param res
		 *            the HTTP result.
		 * @param cache
		 *            the data object.
		 * @throws Exception
		 */
		public abstract void onSuccess(CBase c, HResp res, CRes<T> data)
				throws Exception;

	}

	/**
	 * common result call back for list.
	 * 
	 * @author cny
	 *
	 * @param <T>
	 *            the target class will be parsed by Gson.
	 */
	public static abstract class HResCallbackL<T> extends HResCallback<List<T>> {

		/**
		 * the default constructor by target class.
		 * 
		 * @param cls
		 *            the target class.
		 */
		public HResCallbackL(Class<? extends Resable<T>> cls) {
			super(cls);
		}

		@SuppressWarnings("unchecked")
		@Override
		public CRes<List<T>> fromJson(String data) throws Exception {
			CRes.Resable<T> rt = (Resable<T>) this.cls.newInstance();
			return (CRes<List<T>>) this.gs.fromJson(data, rt.createTokenL()
					.getType());
		}

	}

	/**
	 * the Resable interface to mark the bean can convert from json by gson.<br/>
	 * it must return the gson TypeToken.
	 * 
	 * @author cny
	 *
	 * @param <T>
	 *            the target bean type
	 */
	public interface Resable<T> {
		TypeToken<CRes<T>> createToken();

		TypeToken<CRes<List<T>>> createTokenL();
	}
}
