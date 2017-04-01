package org.gdsp.subleq;

public class CPU implements Unit {

	private int pc = 0;

	public CPU() {
	}

	@Override
	public void cycle(final Interconnect ic) {
		// System.out.println("PC " + this.pc);
		final int a = ic.readMem(this.pc++);
		final int b = ic.readMem(this.pc++);
		final int c = ic.readMem(this.pc++);
		if (a < 0 || b < 0) {
			this.pc = -1;
		} else {
			// System.out.println(a + "(" + ic.readMem(a) + ")," + b + "(" +
			// ic.readMem(b) + ")," + c);
			final int newMem = ic.readMem(b) - ic.readMem(a);
			// System.out.println("NM " + newMem + " at " + b);
			ic.setMem(b, newMem);
			if (newMem <= 0) {
				if (c < 0) {
					this.pc = -1;
				} else {
					this.pc = c;
				}
			}
		}

	}

	@Override
	public int getPriority() {
		return 1;
	}

	public boolean hasHalted() {
		return this.pc < 0;
	}
}
