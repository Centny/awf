package org.cny.awf.util;

import java.io.IOException;
import java.io.OutputStream;

public class MultiOutputStream extends OutputStream {
	protected OutputStream[] outs;
	protected boolean[] unactivated;

	public MultiOutputStream(OutputStream... outs) {
		this.outs = outs;
		this.unactivated = new boolean[outs.length];
	}

	public OutputStream at(int idx) {
		return this.outs[idx];
	}

	public MultiOutputStream mark(int idx, boolean activated) {
		this.unactivated[idx] = !activated;
		return this;
	}

	@Override
	public void write(int arg0) throws IOException {
		for (int i = 0; i < this.unactivated.length; i++) {
			if (this.unactivated[i]) {
				continue;
			}
			this.outs[i].write(arg0);
		}
	}

	@Override
	public void close() throws IOException {
		for (int i = 0; i < this.unactivated.length; i++) {
			if (this.unactivated[i]) {
				continue;
			}
			this.outs[i].close();
		}
	}

	@Override
	public void flush() throws IOException {
		for (int i = 0; i < this.unactivated.length; i++) {
			if (this.unactivated[i]) {
				continue;
			}
			this.outs[i].flush();
		}
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		for (int i = 0; i < this.unactivated.length; i++) {
			if (this.unactivated[i]) {
				continue;
			}
			this.outs[i].write(buffer, offset, count);
		}
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		for (int i = 0; i < this.unactivated.length; i++) {
			if (this.unactivated[i]) {
				continue;
			}
			this.outs[i].write(buffer);
		}
	}

	public void close(int idx) throws IOException {
		if (this.unactivated[idx]) {
			return;
		}
		this.outs[idx].close();
	}
}
