package org.gdsp.subleq;

public class Memory implements Unit {

	public final static int MAXMEMORY = 16384;

	private final int[] memory = new int[MAXMEMORY];

	@Override
	public void cycle(final Interconnect ic) {
	}

	public int get(final int pos) {
		return this.memory[pos];
	}

	@Override
	public int getPriority() {
		return 0;
	}

	public void set(final int pos, final int val) {
		this.memory[pos] = val;
	}

	public int size() {
		return this.memory.length;
	}

}
