package org.cny.amf.net.http.hdb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cny.amf.util.SQLite;
import org.cny.amf.util.Util;

import android.app.Activity;
import android.database.Cursor;

public class HDb {
	public static final String DB_F_NAME = "_hcache_.dbf";

	private static HDb HDB_;

	public static HDb loadDb(Activity aty) throws IOException {
		if (HDB_ == null) {
			HDB_ = new HDb(aty);
		}
		return HDB_;
	}

	public static void free() {
		if (HDB_ != null) {
			HDB_.close();
			HDB_ = null;
		}
	}

	private SQLite db_;
	private Activity aty;

	public HDb(Activity aty) throws IOException {
		InputStream ic = HDb.class.getResourceAsStream("_hc_.sql");
		if (ic == null) {
			throw new RuntimeException("_hc_.sql not found");
		}
		String script = Util.readAll(ic);
		this.db_ = SQLite.loadDb(aty, DB_F_NAME, "_HC_R_", script);
		this.aty = aty;
	}

	public synchronized void close() {
		if (this.db_ != null) {
			this.db_.close();
		}
	}

	public R find(String url, String m, String args) {
		List<R> rs = this.findv(url, m, args);
		if (rs.isEmpty()) {
			return null;
		} else {
			return rs.get(0);
		}
	}

	public synchronized List<R> findv(String url, String m, String args) {
		String sql = "SELECT * FROM _HC_R_ WHERE U=? AND M=? AND ARG=? "
				+ " ORDER BY TIME DESC";
		Cursor cur = this.db_.Db().rawQuery(sql, new String[] { url, m, args });
		List<R> rs = new ArrayList<R>();
		while (cur.moveToNext()) {
			rs.add(new R(cur));
		}
		cur.close();
		return rs;
	}

	public synchronized void add(R r) {
		String sql = "INSERT INTO _HC_R_ (" + R.COLS
				+ ") VALUES(?,?,?,?,?,?,?,?,?,?)";
		this.db_.exec(sql, new Object[] { null, r.u, r.m, r.arg, r.lmt, r.etag,
				r.type, r.len, r.path, r.time });
	}

	public synchronized void del(long tid) {
		String sql = "DELETE FROM _HC_R_ WHERE TID=?";
		this.db_.exec(sql, new Object[] { tid });
	}

	public synchronized void del(String u, String m, String arg) {
		String sql = "DELETE FROM _HC_R_ WHERE U=? AND M=? AND ARG=?";
		this.db_.exec(sql, new Object[] { u, m, arg });
	}

	public synchronized void update(R r) {
		String sql = "UPDATE _HC_R_ SET U=?,M=?,ARG=?,LMT=?,"
				+ "ETAG=?,TYPE=?,LEN=?,PATH=?,TIME=? WHERE TID=?";
		this.db_.exec(sql, new Object[] { r.u, r.m, r.arg, r.lmt, r.etag,
				r.type, r.len, r.path, r.time, r.tid });
	}

	public synchronized void flush(R r) {
		r.time = new Date().getTime();
		this.update(r);
	}

	public synchronized String fname() {
		String name = "";
		String sql = "";
		sql = "SELECT VAL FROM _HC_ENV_ WHERE NAME='_HC_F_I' AND TYPE='_HC_'";
		Cursor cur = this.db_.Db().rawQuery(sql, null);
		if (cur.moveToNext()) {
			name = cur.getString(0);
			sql = "UPDATE _HC_ENV_ SET VAL=VAL+1 WHERE NAME='_HC_F_I' AND TYPE='_HC_'";
		} else {
			name = "0";
			sql = "INSERT INTO _HC_ENV_ VALUES('_HC_F_I','0','_HC_')";
		}
		this.db_.exec(sql);
		return name;
	}

	public File newCacheF() {
		File hc = new File(this.aty.getExternalCacheDir(), "_hc_");
		if (!hc.exists()) {
			hc.mkdirs();
		}
		return new File(hc, this.fname() + ".hc_");
	}

	public File openCacheF(String name) {
		File hc = new File(this.aty.getExternalCacheDir(), "_hc_");
		if (!hc.exists()) {
			hc.mkdirs();
		}
		return new File(hc, name);
	}
}
