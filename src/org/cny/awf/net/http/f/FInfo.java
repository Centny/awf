package org.cny.awf.net.http.f;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * the file info POJO.
 * 
 * @author cny
 *
 */
public class FInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5875836620905851685L;
	protected String url;
	protected String path;
	protected String name;
	protected String sha;
	protected long size;
	protected String type;

	public FInfo() {
	}

	public FInfo(FPis pis) {
		this.path = pis.getPath();
		this.name = pis.getFilename();
		this.sha = pis.Sha1();
		this.size = pis.getLength();
		this.type = pis.getCt().getMimeType();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toJson() {
		return new Gson().toJson(this);
	}

	public static FInfo fromJson(String json) {
		return new Gson().fromJson(json, FInfo.class);
	}
}
