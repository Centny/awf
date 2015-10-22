package org.cny.awf.mr;

import java.util.List;

import org.cny.awf.net.http.Args;
import org.cny.awf.net.http.Args.V;
import org.cny.awf.net.http.SyncH;
import org.cny.awf.net.http.cres.CRes;
import org.cny.awf.net.http.cres.CRes.Resable;

import com.google.gson.Gson;

public class Mr {
	public static final String DEFAULT_ID = "_default";
	protected String base;
	protected String id = DEFAULT_ID;

	public Mr(String base) {
		this.base = base;
		this.setBase(base);
	}

	public Mr(String base, String id) {
		this.id = id;
		this.setBase(base);
	}

	public int intv(String key) {
		Number res = this.numv(key, "I");
		return res.intValue();
	}

	public long longv(String key) {
		Number res = this.numv(key, "I");
		return res.longValue();
	}

	public float floatv(String key) {
		Number res = this.numv(key, "F");
		return res.floatValue();
	}

	public double doublev(String key) {
		Number res = this.numv(key, "F");
		return res.doubleValue();
	}

	public String stringv(String key) {
		return this.strv(key, "S");
	}

	public void inc(String key, int v) {
		this.inc(key, "I", v);
	}

	public void inc(String key, float v) {
		this.inc(key, "F", v);
	}

	public void inc(String key, double v) {
		this.inc(key, "F", v);
	}

	public void inc(String key, String type, Object v) {
		CRes<String> res = this.strv(key,
				Args.A("data", v).A("exec", "I").A("type", type));
		if (res.code == 0) {
			return;
		} else {
			throw new RuntimeException(res.toString());
		}
	}

	public void set(String key, int v) {
		this.set(key, "I", v);
	}

	public void set(String key, float v) {
		this.set(key, "F", v);
	}

	public void set(String key, double v) {
		this.set(key, "F", v);
	}

	public void set(String key, String v) {
		this.set(key, "S", v);
	}

	public void set(String key, Object v) {
		this.set(key, "J", new Gson().toJson(v));
	}

	public void set(String key, String type, Object v) {
		CRes<String> res = this.strv(key,
				Args.A("data", v).A("exec", "S").A("type", type));
		if (res.code == 0) {
			return;
		} else {
			throw new RuntimeException(res.toString());
		}
	}

	public void push(String key, int v) {
		this.push(key, "I", v);
	}

	public void push(String key, float v) {
		this.push(key, "F", v);
	}

	public void push(String key, double v) {
		this.push(key, "F", v);
	}

	public void push(String key, String v) {
		this.push(key, "S", v);
	}

	public void push(String key, Object v) {
		this.push(key, "J", new Gson().toJson(v));
	}

	public void push(String key, String type, Object v) {
		CRes<String> res = this.strv(key,
				Args.A("data", v).A("exec", "P").A("type", type));
		if (res.code == 0) {
			return;
		} else {
			throw new RuntimeException(res.toString());
		}
	}

	public void del(String key) {
		CRes<String> res = this.strv(key, Args.A("exec", "D"));
		if (res.code == 0) {
			return;
		} else {
			throw new RuntimeException(res.toString());
		}
	}

	public String strv(String key, String type) {
		CRes<String> res = this.strv(key, Args.A("exec", "G").A("type", type));
		if (res.code == 0) {
			return res.data;
		} else {
			throw new RuntimeException(res.toString());
		}
	}

	public Number numv(String key, String type) {
		CRes<Number> res = this.numv(key, Args.A("exec", "G").A("type", type));
		if (res.code == 0) {
			return res.data;
		} else {
			throw new RuntimeException(res.toString());
		}
	}

	public <T> T objv(String key, Class<? extends Resable<?>> cls) {
		CRes<T> res = this.objv(key, Args.A("exec", "G").A("type", "J"), cls);
		if (res.code == 0) {
			return res.data;
		} else {
			throw new RuntimeException(res.toString());
		}
	}

	public <T> List<T> objvs(String key, Class<? extends Resable<T>> cls) {
		CRes<List<T>> res = this.objvs(key, Args.A("exec", "G").A("type", "J"),
				cls);
		if (res.code == 0) {
			return res.data;
		} else {
			throw new RuntimeException(res.toString());
		}
	}

	public CRes<Number> numv(String key, V args) {
		args.A("id", this.id).A("_hc_", "NO");
		CRes.HResNumCallbackS res = new CRes.HResNumCallbackS();
		SyncH.doGet(this.base + key, args, res);
		if (res.err != null) {
			throw new RuntimeException(res.err);
		}
		return res.data;
	}

	public CRes<String> strv(String key, V args) {
		args.A("id", this.id).A("_hc_", "NO");
		CRes.HResStrCallbackS res = new CRes.HResStrCallbackS();
		SyncH.doGet(this.base + key, args, res);
		if (res.err != null) {
			throw new RuntimeException(res.err);
		}
		return res.data;
	}

	public <T> CRes<T> objv(String key, V args, Class<? extends Resable<?>> cls) {
		args.A("id", this.id).A("_hc_", "NO");
		CRes.HResCallbackS<T> res = new CRes.HResCallbackS<T>(cls);
		SyncH.doGet(this.base + key, args, res);
		if (res.err != null) {
			throw new RuntimeException(res.err);
		}
		return res.data;
	}

	public <T> CRes<List<T>> objvs(String key, V args,
			Class<? extends Resable<T>> cls) {
		args.A("id", this.id).A("_hc_", "NO");
		CRes.HResCallbackLS<T> res = new CRes.HResCallbackLS<T>(cls);
		SyncH.doGet(this.base + key, args, res);
		if (res.err != null) {
			throw new RuntimeException(res.err);
		}
		return res.data;
	}

	public void setBase(String base) {
		if (base.charAt(base.length() - 1) == '/') {
			this.base = base;
		} else {
			this.base = base + "/";
		}
	}
}
