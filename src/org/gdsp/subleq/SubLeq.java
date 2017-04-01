package org.gdsp.subleq;

import java.io.File;
import java.io.FileNotFoundException;

public class SubLeq {

	private final Memory memory;
	private final CPU cpu;
	private final Interconnect ic;

	public SubLeq() {
		this.memory = new Memory();

		this.cpu = new CPU();

		this.ic = new Interconnect(this.memory);
		this.ic.addUnit(this.cpu);
	}

	public void cycle() {
		this.ic.cycle();
	}

	public void loadMemory(final File f) throws FileNotFoundException {
		this.memory.load(f);
	}

	public static void main(final String[] args) {
		final SubLeq sl = new SubLeq();
		final TextOutput to = new TextOutput();
		// to.setActive(false);
		sl.ic.addUnit(to);

		final File f = new File(args[0]);
		try {
			sl.loadMemory(f);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}

		while (!sl.cpu.hasHalted()) {
			sl.cycle();
		}

	}
}
