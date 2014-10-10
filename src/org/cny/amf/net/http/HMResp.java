package org.cny.amf.net.http;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public class HMResp extends HResp {
	/**
	 * the constructor by HttpResponse.
	 * 
	 * @param reponse
	 *            the HttpResponse.
	 */
	public HMResp(HttpResponse reponse) {
		this.init(reponse, "UTF-8");
	}

	/**
	 * the constructor by HttpResponse and encoding.
	 * 
	 * @param reponse
	 *            the HttpResponse.
	 * @param encoding
	 *            the encoding.
	 */
	public HMResp(HttpResponse reponse, String encoding) {
		this.init(reponse, encoding);
	}


	@Override
	public InputStream getInput() throws Exception {
		HttpEntity entity = this.reponse.getEntity();
		return entity.getContent();
	}

}
