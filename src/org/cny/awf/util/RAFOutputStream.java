package org.cny.awf.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RAFOutputStream extends FileOutputStream {
	protected RandomAccessFile raf;

	public RAFOutputStream(RandomAccessFile raf) throws IOException {
		super(raf.getFD());
		this.raf = raf;
	}

	@Override
	public void close() throws IOException {
		this.raf.close();
		super.close();
	}

}
