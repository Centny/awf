package org.cny.awf.er;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.cny.awf.sr.SR;
import org.cny.jwf.im.pb.Msg.Evn;
import org.cny.jwf.im.pb.Msg.KV;
import org.cny.jwf.util.PbOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

import com.google.protobuf.GeneratedMessage;

public class ER {
	// public static final String ATY_IN = "ATY_IN";
	// public static final String ATY_OUT = "ATY_OUT";
	public static final String ACT_IN = "IN";
	public static final String ACT_OUT = "OUT";
	public static final String CRASH = "CRASH";
	private static ER ER_;
	private static String UID = "NONE";

	private static Logger L = LoggerFactory.getLogger(ER.class);
	protected Context ctx;
	protected PbOutputStream out;

	public ER(Context ctx) throws FileNotFoundException {
		this.ctx = ctx;
		this.open();
	}

	private File getf(boolean t) {
		File ddir = ctx.getExternalFilesDir(SR.ER_DIR);
		if (t) {
			return new File(ddir, "er" + new Date().getTime() + ".dat");
		} else {
			return new File(ddir, SR.ER_FN);
		}
	}

	private void open() throws FileNotFoundException {
		this.out = new PbOutputStream(new FileOutputStream(getf(false), true),
				512);
	}

	// private void check_u() {
	// File tf=this.getf();
	// if(tf.si)
	// }

	public void write(GeneratedMessage gm) throws IOException {
		synchronized (this) {
			// this.check_u();
			try {
				this.out.write(gm);
				this.out.flush();
			} catch (IOException e) {
				L.warn("write message error, will retry :", e);
				this.close();
				this.open();
				this.out.write(gm);
				this.out.flush();
			}
		}
	}

	public boolean backup_() throws FileNotFoundException {
		return this.backup_(this.getf(true));
	}

	public boolean backup_(File bf) throws FileNotFoundException {
		synchronized (this) {
			this.close();
			boolean rv = this.getf(false).renameTo(bf);
			this.open();
			return rv;
		}
	}

	public void close() {
		try {
			this.out.close();
		} catch (Exception e) {
		}
	}

	public static ER init(Context ctx) throws FileNotFoundException {
		if (ER_ == null) {
			ER_ = new ER(ctx);
		}
		return ER_;
	}

	public static boolean backup() throws FileNotFoundException {
		if (ER_ == null) {
			L.warn("the ER not init, it will do nothing");
			return false;
		}
		return ER_.backup_();
	}

	public static boolean backup(File bf) throws FileNotFoundException {
		if (ER_ == null) {
			L.warn("the ER not init, it will do nothing");
			return false;
		}
		return ER_.backup_(bf);
	}

	public static void writem(GeneratedMessage gm) throws Exception {
		if (ER_ == null) {
			L.warn("the ER not init, it will do nothing");
			return;
		}
		ER_.write(gm);
	}

	public static void free() throws Exception {
		if (ER_ == null) {
			L.warn("the ER not init, it will do nothing");
			return;
		}
		ER_.close();
		ER_ = null;
	}

	public static void writem_(String uid, String name, String action,
			int type, Map<String, Object> kvs) throws Exception {
		Evn.Builder eb = Evn.newBuilder();
		eb.setUid(uid);
		eb.setName(name);
		eb.setAction(action);
		eb.setType(type);
		eb.setTime(new Date().getTime());
		if (kvs != null && !kvs.isEmpty()) {
			KV.Builder kb;
			for (String key : kvs.keySet()) {
				kb = KV.newBuilder();
				kb.setKey(key);
				kb.setVal(kvs.get(key).toString());
				eb.addKvs(kb.build());
			}
		}
		writem(eb.build());
	}

	public static void writem(String uid, String name, String action, int type,
			Map<String, Object> kvs) {
		if (uid == null || uid.isEmpty() || name == null || name.isEmpty()
				|| action == null || action.isEmpty()) {
			throw new InvalidParameterException(
					"contain null or empty value in uid/name/action");
		}
		try {
			writem_(uid, name, action, type, kvs);
		} catch (Exception e) {
			L.warn("write message by {},{},{},{},{} err:", uid, name, action,
					type, kvs, e);
		}
	}

	public static void writem(String name, String action, int type,
			Map<String, Object> kvs) {
		writem(UID, name, action, type, kvs);
	}

	public static void writem(String name, String action, int type) {
		writem(name, action, type, null);
	}

	public static void writem(String uid, Class<?> cls, String action,
			int type, Map<String, Object> kvs) {
		if (uid == null || uid.isEmpty() || cls == null || action == null
				|| action.isEmpty()) {
			throw new InvalidParameterException(
					"contain null or empty value in uid/cls/action");
		}
		Info in = cls.getAnnotation(Info.class);
		String name;
		Map<String, Object> tkvs = new HashMap<String, Object>();
		tkvs.put("class", cls.getName());
		if (kvs != null) {
			tkvs.putAll(kvs);
		}
		if (in == null) {
			name = cls.getName();
		} else {
			name = in.name();
			if (name.isEmpty()) {
				name = cls.getName();
			}
			String[] info = in.info();
			int len = info.length / 2;
			for (int i = 0; i < len; i++) {
				tkvs.put(info[i * 2], info[i * 2 + 1]);
			}
		}
		writem(uid, name, action, type, tkvs);
	}

	public static void writem(Class<?> cls, String action, int type,
			Map<String, Object> kvs) {
		writem(UID, cls, action, type, kvs);
	}

	public static void writem(Class<?> cls, String action, int type) {
		writem(UID, cls, action, type, null);
	}

	public static void setUid(String uid) {
		UID = uid;
	}

}
