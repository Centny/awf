package org.cny.awf.util;

import java.util.concurrent.CountDownLatch;

public class CDL extends CountDownLatch {

	private int current = 0;
	private Integer wc = null;

	public CDL(int count) {
		super(count);
	}

	@Override
	public void countDown() {
		super.countDown();
		synchronized (this) {
			this.current++;
			if (this.wc != null && this.current == this.wc) {
				synchronized (this.wc) {
					this.wc.notifyAll();
				}
			}
		}
	}

	public int getCurrent() {
		return current;
	}

	public void waitc(int c) throws InterruptedException {
		synchronized (this) {
			if (this.current >= c) {
				return;
			}
			if (this.wc != null) {
				throw new RuntimeException("already waiting...");
			}
			this.wc = Integer.valueOf(c);
		}
		synchronized (this.wc) {
			this.wc.wait();
			this.wc = null;
		}
	}

}
