package org.gdsp.subleq;

public class TextOutput implements Unit {
	private final static int WIDTH = 32;
	private final static int HEIGHT = 20;
	private final static int BASEMEM = Memory.MAXMEMORY - WIDTH * HEIGHT - 1;

	private boolean active = true;

	public TextOutput() {
	}

	@Override
	public void cycle(final Interconnect ic) {
		if (!this.active) {
			return;
		}
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				final int mem = ic.readMem(BASEMEM + i * WIDTH + j);
				if (mem != 0) {
					System.out.print((char) mem);
				} else {
					System.out.print(' ');
				}
			}
			System.out.println();
		}
		System.out.println("======================");
	}

	@Override
	public int getPriority() {
		return 2;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

}
