package org.cny.awf.im;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.cny.awf.util.SQLite;
import org.cny.awf.util.Util;
import org.cny.jwf.im.Msg;
import org.cny.jwf.util.Utils;

import android.content.Context;

public class ImDb {
	public static final String DB_F_NAME = "_imdb_.dbf";
	public static final String DB_SCRIPT_F = "_im_.sql";
	public static final String COLS = "I,S,R,D,T,C,A,TIME,STATUS";
	// protected static ImDb IDB_;
	//
	// public static ImDb loadDb_(Context ctx) {
	// return loadDb_(ctx, DB_SCRIPT_F);
	// }
	//
	// public static ImDb loadDb_(Context ctx, String file) {
	// try {
	// return loadDb(ctx, file);
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// public static ImDb loadDb(Context ctx, String file) throws IOException {
	// if (IDB_ == null) {
	// IDB_ = new ImDb(ctx, file);
	// }
	// return IDB_;
	// }
	//
	// public static void free() {
	// if (IDB_ != null) {
	// IDB_.close();
	// IDB_ = null;
	// }
	// }

	protected SQLite db_;
	protected Context ctx;

	protected ImDb() {

	}

	public ImDb load(Context ctx) throws IOException {
		return load(ctx, DB_SCRIPT_F);
	}

	public ImDb load(Context ctx, String file) throws IOException {
		InputStream ic = ImDb.class.getResourceAsStream(file);
		if (ic == null) {
			throw new RuntimeException("_im_.sql not found");
		}
		String script = Util.readAll(ic);
		this.ctx = ctx;
		this.db_ = SQLite.loadDb(this.ctx, DB_F_NAME, "_IM_M_", script);
		return this;
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
		this.db_.exec("INSERT INTO _IM_M_ (" + COLS
				+ ") VALUES(?,?,?,?,?,?,?,?,?)", m.toObjects());
	}

	public void update(String i, int status) {
		this.db_.exec("UPDATE _IM_M_ SET STATUS=? WHERE I=?", new String[] {
				status + "", i });
	}

	public void update(String i, String c, int status) {
		this.db_.exec("UPDATE _IM_M_ SET STATUS=?,C=? WHERE I=?", new String[] {
				status + "", c, i });
	}

	public void update(String i, byte[] c, int status) {
		this.update(i, new String(c), status);
	}

	public void update(List<String> is, int status) {
		this.db_.exec(
				"UPDATE _IM_M_ SET STATUS=? WHERE I IN (" + Utils.joinSQL(is)
						+ ")", new Object[] { status });
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

	public List<Msg> listMsgS(String s) {
		return this.db_.rawQuery("SELECT * FROM _IM_M_ WHERE S = ?", s,
				Msg.class, true);
	}

	public List<Msg> listMsgA(String a) {
		return this.db_.rawQuery("SELECT * FROM _IM_M_ WHERE A=? ",
				new String[] { a }, Msg.class, true);
	}

	// public List<Msg> listMsgS(String s, String a) {
	// return this.db_.rawQuery("SELECT * FROM _IM_M_ WHERE S=? AND A=? ",
	// new String[] { s, a }, Msg.class, true);
	// }

	public List<Msg> listMsgR(String r) {
		return this.db_.rawQuery("SELECT * FROM _IM_M_ WHERE R LIKE ?", "%" + r
				+ "%", Msg.class, true);
	}

	/**
	 * list message by type.
	 * 
	 * @param t
	 *            target type.
	 * @return message list.
	 * @throws Exception
	 */
	public List<Msg> listMsgT(int t) {
		return this.db_.rawQuery("SELECT * FROM _IM_M_ WHERE T = ?", "" + t,
				Msg.class, true);
	}

	// public void mark(String is, int status) {
	// this.db_.exec("UPDATE _IM_M_ SET STATUS=? WHERE I IN ('" + is + "')",
	// new Object[] { status });
	// }

	/**
	 * sum message by status.
	 * 
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public Long sumMsgS(int s) {
		return this.db_.longQueryOne(
				"SELECT COUNT(*) FROM _IM_M_ WHERE STATUS = ?",
				new String[] { "" + s });
	}

	/**
	 * sum message by status.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Long sumNoReadMsg() {
		return this.db_.longQueryOne(
				"SELECT COUNT(*) FROM _IM_M_ WHERE STATUS <= ?",
				new String[] { "" + Msg.MS_MARK });
	}

	/**
	 * sum message by status.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Long sumNoReadMsg(String a) {
		return this.db_.longQueryOne(
				"SELECT COUNT(*) FROM _IM_M_ WHERE STATUS <= ? AND A=?",
				new String[] { "" + Msg.MS_MARK, a });
	}

	public void markReaded(String a) {
		this.db_.exec("UPDATE _IM_M_ SET STATUS=? WHERE STATUS<=? AND A=?",
				new Object[] { Msg.MS_READED, Msg.MS_MARK, a });
	}
}
