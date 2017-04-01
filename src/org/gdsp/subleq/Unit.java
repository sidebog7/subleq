package org.gdsp.subleq;

public interface Unit {
	public void cycle(Interconnect ic);

	public int getPriority();
}
