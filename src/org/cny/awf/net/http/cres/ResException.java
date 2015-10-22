package org.cny.awf.net.http.cres;

public class ResException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 475451589826558331L;
	protected CRes<?> res;

	public ResException(CRes<?> res) {
		super("result code is " + res.code);
		this.res = res;
	}

	/**
	 * @return the res
	 */
	public CRes<?> getRes() {
		return res;
	}

	/**
	 * @param res
	 *            the res to set
	 */
	public void setRes(CRes<?> res) {
		this.res = res;
	}

}
