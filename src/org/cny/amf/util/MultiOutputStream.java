package org.cny.amf.util;

import java.io.IOException;
import java.io.OutputStream;

public class MultiOutputStream extends OutputStream {
	OutputStream[] outs;

	public MultiOutputStream(OutputStream... outs) {
		this.outs = outs;
	}

	@Override
	public void write(int arg0) throws IOException {
		for (OutputStream out : outs) {
			out.write(arg0);
		}
	}

	@Override
	public void close() throws IOException {
		for (OutputStream out : outs) {
			out.close();
		}
	}

	@Override
	public void flush() throws IOException {
		for (OutputStream out : outs) {
			out.flush();
		}
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		for (OutputStream out : outs) {
			out.write(buffer, offset, count);
		}
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		for (OutputStream out : outs) {
			out.write(buffer);
		}
	}

}
