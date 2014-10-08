package org.cny.amf.net.http;

import java.io.OutputStream;

import org.apache.http.client.methods.HttpUriRequest;

public interface HCallback {
	/**
	 * called it before request network.<br/>
	 * (not UI thread)
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 * @param r
	 *            the HttpUriRequest instance.
	 */
	public void onRequest(HClient c, HttpUriRequest r);

	/**
	 * called it before data transfer.return the OutputStream to store the HTTP
	 * response data.<br/>
	 * return null or throw exception will stop data transfer and call onErr.<br/>
	 * (not UI thread)
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 * @param r
	 *            the HTTPReponse instance.
	 * @return the OutputStream for store response data.
	 * @throws Exception
	 *             all Exception.
	 */
	public OutputStream onBebin(HClient c, HResp r) throws Exception;

	/**
	 * called it after data transfered.<br/>
	 * throw exception will call onErr.<br/>
	 * (not UI thread)
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 * @param out
	 *            which OutpuStream is created by onBegin.
	 * @throws Exception
	 *             all Exception.
	 */
	public void onEnd(HClient c, OutputStream out) throws Exception;

	//
	/**
	 * called it on data transfer rate changed. (UI thread)
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 * @param rate
	 *            data transfer rate.
	 */
	public void onProcess(HClient c, float rate);

	/**
	 * called it on HTTP completed success.
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 */
	public void onSuccess(HClient c);

	/**
	 * called it on HTTP completed error.
	 * 
	 * @param c
	 *            the HTTPClient instance.
	 * @param err
	 *            target exception.
	 */
	public void onError(HClient c, Throwable err);
}
