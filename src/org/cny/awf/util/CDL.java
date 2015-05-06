package org.cny.awf.util;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDL extends CountDownLatch {

	private String name = "";
	private int current = 0;
	private Integer wc = null;
	private boolean log = false;
	private final Logger L;

	public CDL(int count) {
		super(count);
		L = LoggerFactory.getLogger("CDL");
	}

	public CDL(int count, String name) {
		super(count);
		this.name = name;
		L = LoggerFactory.getLogger("CDL(" + name + ")");
	}

	@Override
	public void countDown() {
		super.countDown();
		synchronized (this) {
			this.current++;
			if (this.log) {
				L.debug("CountDown current({}),wait({})", this.current, this.wc);
			}
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

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public String getName() {
		return name;
	}

}
