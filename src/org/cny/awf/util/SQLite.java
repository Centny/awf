package org.cny.awf.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.cny.jwf.util.Orm;
import org.cny.jwf.util.Orm.OrderBuilder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLite {
	public static SQLite loadDb(Context ctx, String name, String tname,
			String script) {
		SQLite db = new SQLite(ctx, name, Context.MODE_PRIVATE);
		if (!db.check(tname)) {
			db.execS(script);
		}
		return db;
	}

	SQLiteDatabase db_;

	public SQLite(Context ctx, String name, int mode) {
		this.db_ = ctx.openOrCreateDatabase(name, mode, null);
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

	public <T> List<T> rawQuery(String sql, Class<T> cls, boolean toUpper) {
		return this.rawQuery(sql, (String) null, cls, toUpper);
	}

	public <T> List<T> rawQuery(String sql, String args, Class<T> cls,
			boolean toUpper) {
		if (args == null) {
			return this.rawQuery(sql, (String[]) null, cls, toUpper);
		} else {
			return this.rawQuery(sql, new String[] { args }, cls, toUpper);
		}
	}

	public <T> List<T> rawQuery(String sql, String[] args, Class<T> cls,
			boolean toUpper) {
		Cursor c = this.db_.rawQuery(sql, args);
		List<T> ls = Orm.builds(new CursorOrmBuilder(c, toUpper), cls);
		c.close();
		return ls;
	}

	public <T> T rawQueryOne(String sql, String[] args, Class<T> cls,
			boolean toUpper) {
		Cursor c = this.db_.rawQuery(sql, args);
		T val = Orm.build(new CursorOrmBuilder(c, toUpper), cls);
		c.close();
		return val;
	}

	public List<Long> longQuery(String sql, String[] args) {
		Cursor c = this.db_.rawQuery(sql, args);
		List<Long> lv = new ArrayList<Long>();
		while (c.moveToNext()) {
			lv.add(c.getLong(0));
		}
		return lv;
	}

	public Long longQueryOne(String sql, String[] args) {
		Cursor c = this.db_.rawQuery(sql, args);
		if (c.moveToNext()) {
			return c.getLong(0);
		} else {
			return null;
		}
	}

	public void close() {
		if (this.db_ != null) {
			this.db_.close();
		}
	}

	public class CursorOrmBuilder extends OrderBuilder {
		private final Cursor c;
		private final boolean toUpper;
		private final Map<String, Integer> cidx = new HashMap<String, Integer>();

		public CursorOrmBuilder(Cursor c, boolean toUpper) {
			this.c = c;
			this.toUpper = toUpper;
			for (String n : c.getColumnNames()) {
				this.cidx.put(n, c.getColumnIndex(n));
			}
		}

		@Override
		public boolean next() {
			return this.c.moveToNext();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T get(String name, Class<T> cls) {
			String tname = name;
			if (this.toUpper) {
				tname = name.toUpperCase(Locale.ENGLISH);
			}
			if (!this.cidx.containsKey(tname)) {
				return null;
			}
			int idx = this.cidx.get(tname);
			if (cls == String.class) {
				return (T) this.c.getString(idx);
			} else if (cls == long.class || cls == Long.class) {
				return (T) Long.valueOf(this.c.getLong(idx));
			} else if (cls == short.class || cls == Short.class) {
				return (T) Short.valueOf(this.c.getShort(idx));
			} else if (cls == int.class || cls == Integer.class) {
				return (T) Integer.valueOf(this.c.getInt(idx));
			} else if (cls == double.class || cls == Double.class) {
				return (T) Double.valueOf(this.c.getDouble(idx));
			} else if (cls == float.class || cls == Float.class) {
				return (T) Float.valueOf(this.c.getFloat(idx));
			} else if (cls == byte[].class) {
				return (T) this.c.getBlob(idx);
			} else {
				return null;
			}
		}
	}
}
