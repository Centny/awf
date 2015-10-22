package org.cny.awf.util;

public class CodeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8292563655560528579L;
	public int code;

	public CodeException(int code) {
		super();
		this.code = code;
	}

	public CodeException(int code, String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		this.code = code;
	}

	public CodeException(int code, String detailMessage) {
		super(detailMessage);
		this.code = code;
	}

	public CodeException(int code, Throwable throwable) {
		super(throwable);
		this.code = code;
	}

}
