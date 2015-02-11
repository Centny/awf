package org.cny.awf.util;

import java.util.List;

import org.cny.awf.test.MainActivity;
import org.cny.jwf.util.Orm.Name;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class SQLiteTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public SQLiteTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private static final String dbs = ""
			+ "CREATE TABLE NVL ("
			+ "	 N TEXT(256,0) NOT NULL,V TEXT(256,0) NOT NULL,L INTEGER(64,0) NOT NULL,B BLOB"
			+ ")";

	public static class Nvl {

		public String n;
		public String v;
		public int iv;
		public long lv;
		public short sv;
		public float fv;
		public double dv;
		public String ssv;
		public String abc;
		public byte[] bys;
		public Nvl nvl;

		@Name(name = "N")
		public void setN(String n) {
			this.n = n;
		}

		@Name(name = "V")
		public void setV(String v) {
			this.v = v;
		}

		@Name(name = "L")
		public void setIv(int iv) {
			this.iv = iv;
		}

		@Name(name = "L")
		public void setLv(long lv) {
			this.lv = lv;
		}

		@Name(name = "L")
		public void setSv(short sv) {
			this.sv = sv;
		}

		@Name(name = "L")
		public void setFv(float fv) {
			this.fv = fv;
		}

		@Name(name = "L")
		public void setDv(double dv) {
			this.dv = dv;
		}

		@Name(name = "L")
		public void setSsv(String ssv) {
			this.ssv = ssv;
		}

		@Name(name = "B")
		public void setBys(byte[] bys) {
			this.bys = bys;
		}

		@Name(name = "L")
		public void setNvl(Nvl nvl) {
			this.nvl = nvl;
		}

		public void setAbc(String abc) {
			this.abc = abc;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(n).append(',').append(v).append(',').append(iv)
					.append(',').append(lv).append(',').append(sv).append(',')
					.append(fv).append(',').append(dv).append(',').append(ssv);
			return sb.toString();
		}

	}

	public void testQuery() throws Exception {
		SQLite db = SQLite.loadDb(this.getActivity(), "ab_db", "NVL", dbs);
		db.exec("DELETE FROM NVL");
		db.exec("INSERT INTO NVL VALUES('N1','V1',1,null)");
		db.exec("INSERT INTO NVL VALUES('N2','V2',2,null)");
		db.exec("INSERT INTO NVL VALUES('N3','V3',342324,null)");
		List<Nvl> vls = db.rawQuery("SELECT * FROM NVL", Nvl.class, true);
		for (Nvl vl : vls) {
			Log.d("SQLite", vl.toString());
		}
		Nvl nvl = db.rawQueryOne("SELECT * FROM NVL", null, Nvl.class, true);
		assertNotNull(nvl);
		assertEquals(3, db.longQuery("SELECT L FROM NVL", null).size());
		assertNotNull(db.longQueryOne("SELECT L FROM NVL", null));
		assertNull(db.longQueryOne("SELECT L FROM NVL WHERE 1=0", null));
		db.close();
	}
}
