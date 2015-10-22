package org.cny.awf.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Providers util to get set data from SharedPreference.
 * 
 * @author cny
 *
 */
public class Pref {
	/**
	 * the public context.
	 */
	public static Context CTX = null;
	/**
	 * the default preference name.
	 */
	public static final String DEFAULT = "pref_";

	/**
	 * get string from SharedPreference by name and key.
	 * 
	 * @param name
	 *            target name.
	 * @param key
	 *            key.
	 * @return string data, return null if not found.
	 */
	public static String strv(String name, String key) {
		SharedPreferences sp = CTX.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}

	/**
	 * get string from SharedPreference by default name and key.
	 * 
	 * @param key
	 *            key.
	 * @return string data, return null if not found.
	 */
	public static String strv(String key) {
		return strv(DEFAULT, key);
	}

	public static int intv(String name, String key) {
		SharedPreferences sp = CTX.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}

	public static int intv(String key) {
		return intv(DEFAULT, key);
	}

	public static float floatv(String name, String key) {
		SharedPreferences sp = CTX.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		return sp.getFloat(key, 0);
	}

	public static float floatv(String key) {
		return floatv(DEFAULT, key);
	}

	public static long longv(String name, String key) {
		SharedPreferences sp = CTX.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		return sp.getLong(key, 0);
	}

	public static long longv(String key) {
		return longv(DEFAULT, key);
	}

	public static boolean boolv(String name, String key) {
		SharedPreferences sp = CTX.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}

	public static boolean boolv(String key) {
		return boolv(DEFAULT, key);
	}

	public static void set(String name, String key, String value) {
		SharedPreferences sp = CTX.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		sp.edit().putString(key, value).apply();
	}

	public static void set(String key, String value) {
		set(DEFAULT, key, value);
	}

	public static void set(String name, String key, int value) {
		SharedPreferences sp = CTX.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		sp.edit().putInt(key, value).apply();
	}

	public static void set(String key, int value) {
		set(DEFAULT, key, value);
	}

	public static void set(String name, String key, float value) {
		SharedPreferences sp = CTX.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		sp.edit().putFloat(key, value).apply();
	}

	public static void set(String key, float value) {
		set(DEFAULT, key, value);
	}

	public static void set(String name, String key, long value) {
		SharedPreferences sp = CTX.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		sp.edit().putLong(key, value).apply();
	}

	public static void set(String key, long value) {
		set(DEFAULT, key, value);
	}

	public static void set(String name, String key, boolean value) {
		SharedPreferences sp = CTX.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).apply();
	}

	public static void set(String key, boolean value) {
		set(DEFAULT, key, value);
	}
}
