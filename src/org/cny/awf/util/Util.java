package org.cny.awf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.util.InetAddressUtils;
import org.cny.jwf.util.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * the external util for android.
 * 
 * @author Centny.
 * 
 */
public class Util {

	public static Context CTX;

	public static String listMac() throws SocketException {
		List<String> macs = new ArrayList<String>();
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface itf = en.nextElement();
			if (itf.isLoopback() || itf.isPointToPoint() || itf.isVirtual()) {
				continue;
			}
			byte[] mac = itf.getHardwareAddress();
			if (mac == null) {
				continue;
			}
			macs.add(Utils.byte2hex(mac));
		}
		Collections.sort(macs);
		return Utils.join(macs);
	}

	public static Map<String, String> listMacv() throws SocketException {
		Map<String, String> macs = new HashMap<String, String>();
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface itf = en.nextElement();
			if (itf.isLoopback() || itf.isPointToPoint() || itf.isVirtual()) {
				continue;
			}
			byte[] mac = itf.getHardwareAddress();
			if (mac == null) {
				continue;
			}
			macs.put(itf.getDisplayName(), Utils.byte2hex(mac));
		}
		return macs;
	}

	/**
	 * get the local IP address.<br/>
	 * check the order:WIFI,Mobile,or null.
	 * 
	 * @param aty
	 *            the ContextWrapper for android.
	 * @return IP.
	 */
	public static String localIpAddress(ContextWrapper aty) {
		return localIpAddress(aty, false);
	}

	/**
	 * @param aty
	 *            the ContextWrapper for android.
	 * @param onlyWifi
	 *            only check wifi.
	 * @return IP.
	 */
	public static String localIpAddress(Context ctx, boolean onlyWifi) {
		try {
			WifiManager wifi;
			wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
			if (wifi.isWifiEnabled()) {
				WifiInfo winfo = wifi.getConnectionInfo();
				return intToIp(winfo.getIpAddress());
			}
			if (onlyWifi) {
				return null;
			}
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(Util.class.getName(), ex + "");
		}
		return null;
	}

	public static String localIpAddress() {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase(Locale.ENGLISH);
						if (InetAddressUtils.isIPv4Address(sAddr))
							return sAddr;
					}
				}
			}
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * convert int IP Address format to normal format.
	 * 
	 * @param i
	 *            the int IP address.
	 * @return the normal IP address.
	 */
	public static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		if (bitmap == null) {
			return null;
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scale = 1;
		if (w > width) {
			scale = (float) width / (float) w;
		}
		if (h * scale > height) {
			scale = (float) height / (float) h;
		}
		matrix.postScale(scale, scale);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	public static Bitmap readBitmap(String file) throws FileNotFoundException {
		return readBitmap(file, 0, 0);
	}

	public static Bitmap readBitmap(String file, int w, int h) throws FileNotFoundException {
		return readBitmap(file, w, h, -1, -1);
	}

	public static Bitmap readBitmap(String file, int w, int h, int maxw, int maxh) throws FileNotFoundException {
		if (w < 1 && h < 1 && maxw < 1 && maxh < 1) {
			return BitmapFactory.decodeFile(file);
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, opts);
		if (opts.outWidth < 0) {
			return null;
		}
		int inSampleSize = 1;
		if (w > 0 && opts.outWidth > w) {
			while ((opts.outWidth / inSampleSize) > w) {
				inSampleSize *= 2;
			}
		}
		if (h > 0 && opts.outHeight > h) {
			while ((opts.outHeight / inSampleSize) > h) {
				inSampleSize *= 2;
			}
		}
		if (maxw > 0 && opts.outWidth > maxw) {
			while ((opts.outWidth / inSampleSize) > maxw) {
				inSampleSize *= 2;
			}
		}
		if (maxh > 0 && opts.outHeight > maxh) {
			while ((opts.outHeight / inSampleSize) > maxh) {
				inSampleSize *= 2;
			}
		}
		opts.inSampleSize = inSampleSize;
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(file, opts);
	}

	/**
	 * get the px by dp
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * get the dp by px.
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static String readAll(InputStream in) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		return sb.toString();
	}

	public static String readAll(File in) throws Exception {
		FileInputStream fis = new FileInputStream(in);
		try {
			return readAll(fis);
		} catch (Exception e) {
			throw e;
		} finally {
			fis.close();
		}

	}

	public static void write(File f, byte[] bys) throws Exception {
		FileOutputStream fis = new FileOutputStream(f);
		try {
			fis.write(bys);
		} catch (Exception e) {
			throw e;
		} finally {
			fis.close();
		}
	}

	public static String uri(HttpUriRequest r) {
		URI u = r.getURI();
		if (u.getPort() < 0) {
			return u.getScheme() + "://" + u.getHost() + u.getPath();
		} else {
			return u.getScheme() + "://" + u.getHost() + ":" + u.getPort() + u.getPath();
		}
	}

	public static boolean isNullOrEmpty(String t) {
		return t == null || t.trim().isEmpty();
	}

	public static boolean isNoEmpty(String t) {
		return !(t == null || t.trim().isEmpty());
	}

	public static Map<String, String> DevInfo(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		Map<String, String> kvs = new HashMap<String, String>();
		kvs.put("IMEI", tm.getDeviceId());
		kvs.put("dver", tm.getDeviceSoftwareVersion());
		kvs.put("phone", tm.getLine1Number());
		kvs.put("ciso", tm.getNetworkCountryIso());
		kvs.put("nopt", tm.getNetworkOperator());
		kvs.put("nopn", tm.getNetworkOperatorName());
		kvs.put("ntype", tm.getNetworkType() + "");
		kvs.put("ptype", tm.getPhoneType() + "");
		kvs.put("sciso", tm.getSimCountryIso());
		kvs.put("sopt", tm.getSimOperator());
		kvs.put("sopm", tm.getSimOperatorName());
		kvs.put("serial", tm.getSimSerialNumber());
		kvs.put("sims", tm.getSimState() + "");
		kvs.put("IMSI", tm.getSubscriberId());
		return kvs;
	}

	public static String AppVer(Context ctx) {
		return AppVer(ctx, ctx.getPackageName());
	}

	public static String AppVer(Context ctx, String name) {
		try {
			return AppVer_(ctx, name);
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	public static String AppVer_(Context ctx, String name) throws NameNotFoundException {
		PackageManager pm = ctx.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(name, 0);
		return pi.versionName;
	}

	@SuppressWarnings("deprecation")
	public static Map<String, String> SysInfo(Context ctx) {
		Map<String, String> kvs = new HashMap<String, String>();
		kvs.put("appver", AppVer(ctx));
		kvs.put("BOOTLOADER", Build.BOOTLOADER);
		kvs.put("DEVICE", Build.DEVICE);
		kvs.put("DISPLAY", Build.DISPLAY);
		kvs.put("FINGERPRINT", Build.FINGERPRINT);
		kvs.put("ID", Build.ID);
		kvs.put("MANUFACTURER", Build.MANUFACTURER);
		kvs.put("MODEL", Build.MODEL);
		kvs.put("PRODUCT", Build.PRODUCT);
		kvs.put("TAGS", Build.TAGS);
		kvs.put("TIME", Build.TIME + "");
		kvs.put("TAGS", Build.TAGS);
		kvs.put("CODENAME", Build.VERSION.CODENAME);
		kvs.put("RELEASE", Build.VERSION.RELEASE);
		kvs.put("SDK", Build.VERSION.SDK);
		kvs.put("SDK_INT", Build.VERSION.SDK_INT + "");
		return kvs;
	}

	public static Map<String, Object> ListInfo(Context ctx) {
		Map<String, Object> inf = new HashMap<String, Object>();
		PutInfo_(ctx, inf);
		return inf;
	}

	public static void PutInfo_(Context ctx, Map<String, Object> inf) {
		try {
			inf.put("dev", DevInfo(ctx));
			inf.put("sys", SysInfo(ctx));
			inf.put("mac", listMacv());
		} catch (Exception e) {
		}
	}

	/**
	 * inflate the view by resource id and view group.using common context.
	 * 
	 * @param resource
	 *            the resource id.
	 * @param vg
	 *            the ViewGorup
	 * @return the view.
	 */
	public static View inflate(int resource, ViewGroup vg) {
		return View.inflate(CTX, resource, vg);
	}

	public static void sendTouch(View v) {
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis() + 100;
		MotionEvent env = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, 0, 0, 0);
		v.dispatchTouchEvent(env);
		env.recycle();
	}

	public static String base64(byte[] bys) {
		return Base64.encodeToString(bys, Base64.DEFAULT);
	}

	public static Uri findResUri(Context context, int res) throws NameNotFoundException {
		Context packageContext = context.createPackageContext(context.getPackageName(), Context.CONTEXT_RESTRICTED);
		Resources resources = packageContext.getResources();
		String appPkg = packageContext.getPackageName();
		String resPkg = resources.getResourcePackageName(res);
		String type = resources.getResourceTypeName(res);
		String name = resources.getResourceEntryName(res);
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme(ContentResolver.SCHEME_ANDROID_RESOURCE);
		uriBuilder.encodedAuthority(appPkg);
		uriBuilder.appendEncodedPath(type);
		if (appPkg.equals(resPkg)) {
			uriBuilder.appendEncodedPath(name);
		} else {
			uriBuilder.appendEncodedPath(resPkg + ":" + name);
		}
		return uriBuilder.build();
	}

	public static int findRId(String id) {
		try {
			String[] all = id.split("\\.");
			if (all.length < 3) {
				throw new Exception("invalid class name");
			}
			Class<?> cls = Class.forName(Utils.join(all, 0, all.length - 2, ".") + "$" + all[all.length - 2]);
			Field f = cls.getField(all[all.length - 1]);
			return f.getInt(cls);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
