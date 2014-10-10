package org.cny.amf.util;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLite {
	public static SQLite loadDb(Activity aty, String name, String tname,
			String script) {
		SQLite db = new SQLite(aty, name, Context.MODE_PRIVATE);
		if (!db.check(tname)) {
			db.execS(script);
		}
		return db;
	}

	SQLiteDatabase db_;

	public SQLite(Activity aty, String name, int mode) {
		this.db_ = aty.openOrCreateDatabase(name, mode, null);
	}

	public SQLiteDatabase Db() {
		return this.db_;
	}

	/**
	 * check the table if existed.
	 * 
	 * @param tname
	 *            the table name.
	 * 
	 * @return if existed.
	 */
	public boolean check(String tname) {
		try {
			this.db_.execSQL("SELECT * FROM " + tname + " WHERE 0=1");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void exec(String sql) {
		this.db_.execSQL(sql);
	}

	public void exec(String sql, Object[] bindArgs) {
		this.db_.execSQL(sql, bindArgs);
	}

	public void execS(String script) {
		script = script.replaceAll("(?m)/\\*.*\\*/\n?", "");
		script = script.replaceAll("--.*\n?", "");
		script = script.replaceAll("\n{2,}", "");
		String[] sqls = script.split(";");
		for (String sql : sqls) {
			if (sql.trim().isEmpty()) {
				continue;
			}
			this.db_.execSQL(sql);
		}
	}

	// public Cursor rawQuery(String sql, String args) {
	// if (args == null) {
	// return this.db_.rawQuery(sql, null);
	// } else {
	// return this.db_.rawQuery(sql, args.split(","));
	// }
	// }

	public void close() {
		if (this.db_ != null) {
			this.db_.close();
		}
	}

}
