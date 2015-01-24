package org.cny.awf.net.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cny.awf.util.SQLite;
import org.cny.awf.util.Util;

import android.app.Activity;
import android.database.Cursor;

public class HDb {
	public static final String DB_F_NAME = "_hcache_.dbf";
	public static final String COLS = "TID,U,M,ARG,LMT,ETAG,TYPE,LEN,ENC,PATH,TIME";
	private static HDb HDB_;

	public static HDb loadDb_(Activity aty) {
		try {
			return loadDb(aty);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

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

	public HResp find(String url, String m, String args) {
		List<HResp> rs = this.findv(url, m, args);
		if (rs.isEmpty()) {
			return null;
		} else {
			return rs.get(0);
		}
	}

	public synchronized List<HResp> findv(String url, String m, String args) {
		String sql = "SELECT * FROM _HC_R_ WHERE U=? AND M=? AND ARG=? "
				+ " ORDER BY TIME DESC";
		Cursor cur = this.db_.Db().rawQuery(sql, new String[] { url, m, args });
		List<HResp> rs = new ArrayList<HResp>();
		while (cur.moveToNext()) {
			rs.add(new HResp().init(cur));
		}
		cur.close();
		return rs;
	}

	public synchronized void add(HResp r) {
		String sql = "INSERT INTO _HC_R_ (" + COLS
				+ ") VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		this.db_.exec(sql, r.toObjects(true));
	}

	public synchronized void del(long tid) {
		String sql = "DELETE FROM _HC_R_ WHERE TID=?";
		this.db_.exec(sql, new Object[] { tid });
	}

	public synchronized void del(String u, String m, String arg) {
		String sql = "DELETE FROM _HC_R_ WHERE U=? AND M=? AND ARG=?";
		this.db_.exec(sql, new Object[] { u, m, arg });
	}

	public synchronized void update(HResp r) {
		String sql = "UPDATE _HC_R_ SET U=?,M=?,ARG=?,LMT=?,"
				+ "ETAG=?,TYPE=?,LEN=?,PATH=?,TIME=? WHERE TID=?";
		this.db_.exec(sql, r.toObjects(false));
	}

	public synchronized void flush(HResp r) {
		r.time = new Date().getTime();
		this.update(r);
	}

	public synchronized void clearR() {
		String sql = "DELETE FROM _HC_R_";
		this.db_.exec(sql, new Object[] {});
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

	public boolean CacheExist(HResp r) {
		if (r == null || Util.isNullOrEmpty(r.path)) {
			return false;
		}
		return this.openCacheF(r.path).exists();
	}
}
