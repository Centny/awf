package org.cny.awf.im;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.cny.awf.util.SQLite;
import org.cny.awf.util.Util;
import org.cny.jwf.im.Msg;

import android.content.Context;

public class ImDb {
	public static final String DB_F_NAME = "_imdb_.dbf";
	public static final String DB_SCRIPT_F = "_im_.sql";
	public static final String COLS = "I,S,R,D,T,C,TIME";
	private static ImDb IDB_;

	public static ImDb loadDb_(Context ctx) {
		return loadDb_(ctx, DB_SCRIPT_F);
	}

	public static ImDb loadDb_(Context ctx, String file) {
		try {
			return loadDb(ctx, file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static ImDb loadDb(Context ctx, String file) throws IOException {
		if (IDB_ == null) {
			IDB_ = new ImDb(ctx, file);
		}
		return IDB_;
	}

	public static void free() {
		if (IDB_ != null) {
			IDB_.close();
			IDB_ = null;
		}
	}

	private SQLite db_;
	private Context ctx;

	public ImDb(Context ctx, String file) throws IOException {
		InputStream ic = ImDb.class.getResourceAsStream(file);
		if (ic == null) {
			throw new RuntimeException("_im_.sql not found");
		}
		String script = Util.readAll(ic);
		this.ctx = ctx;
		this.db_ = SQLite.loadDb(this.ctx, DB_F_NAME, "_IM_M_", script);
	}

	public synchronized void close() {
		if (this.db_ != null) {
			this.db_.close();
		}
	}

	/**
	 * store one message.
	 * 
	 * @param m
	 *            the ImMsg.
	 */
	public void add(Msg m) {
		this.db_.exec(
				"INSERT INTO _IM_M_ (" + COLS + ") VALUES(?,?,?,?,?,?,?)",
				m.toObjects());
	}

	/**
	 * clear message by R, if r is empty clear all message.
	 * 
	 * @param r
	 *            R.
	 */
	public void clearMsg(String r) {
		this.db_.exec("DELETE FROM _IM_M_ WHERE R LIKE ?", new Object[] { "%"
				+ r + "%" });
	}

	public List<Msg> listMsgS(String s) throws Exception {
		return this.db_.rawQuery("SELECT * FROM _IM_M_ WHERE S = ?", s,
				Msg.class);
	}

	public List<Msg> listMsgR(String r) throws Exception {
		return this.db_.rawQuery("SELECT * FROM _IM_M_ WHERE R LIKE ?", "%" + r
				+ "%", Msg.class);
	}

	public List<Msg> listMsgT(int t) throws Exception {
		return this.db_.rawQuery("SELECT * FROM _IM_M_ WHERE T = ?", "" + t,
				Msg.class);
	}
}
