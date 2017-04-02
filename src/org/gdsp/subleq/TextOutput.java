package org.gdsp.subleq;

public class TextOutput implements Unit {
	private final static int WIDTH = 32;
	private final static int HEIGHT = 20;

	private boolean active = true;

	public char[][] previousScreen = new char[WIDTH][HEIGHT];

	public TextOutput() {
	}

	@Override
	public void cycle(final Interconnect ic) {
		if (!this.active) {
			return;
		}
		final char[][] screen = new char[WIDTH][HEIGHT];
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				final int mem = ic.readMem(0, i * WIDTH + j);
				if (mem != 0) {
					screen[j][i] = (char) mem;
				} else {
					screen[j][i] = ' ';
				}
			}
		}
		if (this.hasChanged(screen)) {
			for (int i = 0; i < HEIGHT; i++) {
				for (int j = 0; j < WIDTH; j++) {
					System.out.print(screen[j][i]);
				}
				System.out.println();
			}
			System.out.println("======================");
			this.previousScreen = screen;
		}

	}

	@Override
	public int getPriority() {
		return 2;
	}

	private boolean hasChanged(final char[][] newScreen) {
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				if (newScreen[j][i] != this.previousScreen[j][i]) {
					return true;
				}
			}
		}
		return false;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

}
