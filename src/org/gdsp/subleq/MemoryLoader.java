package org.gdsp.subleq;

import java.io.FileNotFoundException;

public interface MemoryLoader {
	public void loadMem(Interconnect ic) throws FileNotFoundException;
}
