package org.cny.awf.er;

public enum ActType {
	N(100), ATY(101), APP(102);

	private int val;

	private ActType(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}
}
