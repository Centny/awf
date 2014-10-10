package org.cny.amf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.Enumeration;

import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * the external util for android.
 * 
 * @author Centny.
 * 
 */
public class Util {

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
	public static String localIpAddress(ContextWrapper aty, boolean onlyWifi) {
		try {
			WifiManager wifi;
			wifi = (WifiManager) aty.getSystemService(Context.WIFI_SERVICE);
			if (wifi.isWifiEnabled()) {
				WifiInfo winfo = wifi.getConnectionInfo();
				return intToIp(winfo.getIpAddress());
			}
			if (onlyWifi) {
				return null;
			}
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
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

	/**
	 * convert int IP Address format to normal format.
	 * 
	 * @param i
	 *            the int IP address.
	 * @return the normal IP address.
	 */
	public static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
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
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
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

	public static String uri(HttpUriRequest r) {
		URI u = r.getURI();
		if (u.getPort() < 0) {
			return u.getScheme() + "://" + u.getHost() + u.getPath();
		} else {
			return u.getScheme() + "://" + u.getHost() + ":" + u.getPort()
					+ u.getPath();
		}
	}

	public static boolean isNullOrEmpty(String t) {
		return t == null || t.trim().isEmpty();
	}
}
