package org.cny.awf.net.http.cres;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.cny.awf.net.http.CBase;
import org.cny.awf.net.http.HCallback.HCacheCallback;
import org.cny.awf.net.http.HResp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
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

	public Pa pa = new Pa();

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
	 * @param pa
	 *            the pa to set
	 */
	public void setPa(Pa pa) {
		this.pa = pa;
	}

	@Override
	public String toString() {
		return "code:" + this.code + ",msg:" + this.msg + ",dmsg:" + this.dmsg
				+ ",data:" + this.data;
	}

	/**
	 * set PA.
	 * 
	 * @author cny
	 *
	 */
	public static class Pa {
		public int pn;
		public int ps;
		public int total;

		public void setPn(int pn) {
			this.pn = pn;
		}

		public void setPs(int ps) {
			this.ps = ps;
		}

		public void setTotal(int total) {
			this.total = total;
		}

	}

	public static interface HResCallbackNable<T> {
		Type createToken(HResCallbackNCaller<T> caller) throws Exception;

		void onError(HResCallbackNCaller<T> caller, CBase c, CRes<T> cache,
				Throwable err) throws Exception;

		void onSuccess(HResCallbackNCaller<T> caller, CBase c, HResp res,
				CRes<T> data) throws Exception;
	}

	public static abstract class HResCallbackN<T> extends HCacheCallback {

		protected abstract Type createToken() throws Exception;

		protected Gson createGson() throws Exception {
			return new Gson();
		}

		@Override
		public void onCache(CBase c, HResp res) throws Exception {
			this.onCache(c, res, this.fromJson(c.readCahce(res)));
		}

		@SuppressWarnings("unchecked")
		public CRes<T> fromJson(String data) throws Exception {
			Gson gs = this.createGson();
			try {
				return (CRes<T>) gs.fromJson(data, this.createToken());
			} catch (Exception e) {
				throw new Exception(data, e);
			}
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

		public void onCache(CBase c, HResp res, CRes<T> data) throws Exception {

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

	public abstract static class HResMapCallback extends
			HResCallbackN<Map<String, Object>> {

		@Override
		protected Type createToken() throws Exception {
			return new TypeToken<CRes<Map<String, Object>>>() {
			}.getType();
		}
	}

	public abstract static class HResStrCallback extends HResCallbackN<String> {

		@Override
		protected Type createToken() throws Exception {
			return new TypeToken<CRes<String>>() {
			}.getType();
		}
	}

	public static class HResStrCallbackS extends HResStrCallback {
		public Throwable err;
		public CRes<String> data;

		@Override
		public void onError(CBase c, CRes<String> cache, Throwable err)
				throws Exception {
			this.data = cache;
			this.err = err;

		}

		@Override
		public void onSuccess(CBase c, HResp res, CRes<String> data)
				throws Exception {
			this.data = data;

		}
	}

	public abstract static class HResNumCallback extends HResCallbackN<Number> {

		@Override
		protected Type createToken() throws Exception {
			return new TypeToken<CRes<Number>>() {
			}.getType();
		}
	}

	public static class HResNumCallbackS extends HResNumCallback {
		public Throwable err;
		public CRes<Number> data;

		@Override
		public void onError(CBase c, CRes<Number> cache, Throwable err)
				throws Exception {
			this.data = cache;
			this.err = err;
		}

		@Override
		public void onSuccess(CBase c, HResp res, CRes<Number> data)
				throws Exception {
			this.data = data;
		}

	}

	public static class HResCallbackNCaller<T> extends HResCallbackN<T> {
		protected HResCallbackNable<T> rcn;

		public HResCallbackNCaller(HResCallbackNable<T> rcn) {
			this.rcn = rcn;
		}

		@Override
		protected Type createToken() throws Exception {
			return this.rcn.createToken(this);
		}

		@Override
		public void onError(CBase c, CRes<T> cache, Throwable err)
				throws Exception {
			this.rcn.onError(this, c, cache, err);
		}

		@Override
		public void onSuccess(CBase c, HResp res, CRes<T> data)
				throws Exception {
			this.rcn.onSuccess(this, c, res, data);
		}

	}

	/**
	 * common result call back.
	 * 
	 * @author cny
	 *
	 * @param <T>
	 *            the target class will be parsed by Gson.
	 */
	public static abstract class HResCallback<T> extends HResCallbackN<T> {

		protected Class<? extends Resable<?>> cls;
		protected ObjectDeserializer<List<T>> des;
		protected GsonBuilder gb = new GsonBuilder();
		protected CRes.Resable<T> rt;

		public HResCallback(Class<? extends Resable<?>> cls) {
			this.cls = cls;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Gson createGson() throws Exception {
			this.rt = (Resable<T>) this.cls.newInstance();
			if (rt instanceof BaseRes) {
				gb.registerTypeAdapter(this.cls,
						((BaseRes<T>) rt).createDeserializer());
			}
			return this.gb.create();
		}

		protected Type createToken() throws Exception {
			return this.rt.createToken().getType();
		}

		public String sdata() {
			if (this.des == null || this.des.je == null) {
				return null;
			} else {
				return this.des.je.getAsString();
			}
		}
	}

	public static class HResCallbackS<T> extends HResCallback<T> {
		public Throwable err;
		public CRes<T> data;

		public HResCallbackS(Class<? extends Resable<?>> cls) {
			super(cls);
		}

		@Override
		public void onError(CBase c, CRes<T> cache, Throwable err)
				throws Exception {
			this.data = cache;
			this.err = err;
		}

		@Override
		public void onSuccess(CBase c, HResp res, CRes<T> data)
				throws Exception {
			this.data = data;
		}

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

		protected Type createToken() throws Exception {
			@SuppressWarnings("unchecked")
			CRes.Resable<T> rt = (Resable<T>) this.cls.newInstance();
			return rt.createTokenL().getType();
		}

	}

	public static class HResCallbackLS<T> extends HResCallbackL<T> {
		public Throwable err;
		public CRes<List<T>> data;

		public HResCallbackLS(Class<? extends Resable<T>> cls) {
			super(cls);
		}

		@Override
		public void onError(CBase c, CRes<List<T>> cache, Throwable err)
				throws Exception {
			this.err = err;
			this.data = cache;
		}

		@Override
		public void onSuccess(CBase c, HResp res, CRes<List<T>> data)
				throws Exception {
			this.data = data;

		}

	}

	public static class ObjectDeserializer<T> implements JsonDeserializer<T> {
		protected Gson gs = new Gson();
		protected JsonElement je;

		@Override
		public T deserialize(JsonElement je, Type type,
				JsonDeserializationContext ctx) throws JsonParseException {
			this.je = je;
			if (je.isJsonObject() || je.isJsonArray()) {
				return this.gs.fromJson(je, type);
			} else {
				return null;
			}
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

		ObjectDeserializer<T> createDeserializer();
	}

	public static abstract class BaseRes<T> implements Resable<T> {
		@Override
		public ObjectDeserializer<T> createDeserializer() {
			return new ObjectDeserializer<T>();
		}
	}
}
