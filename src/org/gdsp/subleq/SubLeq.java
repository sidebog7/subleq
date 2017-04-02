package org.gdsp.subleq;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

public class SubLeq {

	private final CPU cpu;
	private final Interconnect ic;

	public SubLeq() {
		this.cpu = new CPU();

		this.ic = new Interconnect();
		this.ic.addUnit(this.cpu);
	}

	public void cycle() {
		this.ic.cycle();
	}

	public static void main(final String[] args) {
		final SubLeq sl = new SubLeq();
		final TextOutput to = new TextOutput();
		sl.ic.addUnit(to);

		final File f = new File(args[0]);
		try {
			final AssemblerLoader al = new AssemblerLoader(f);
			al.loadMem(sl.ic);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}

		final Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (sl.cpu.hasHalted()) {
					t.cancel();
				} else {
					sl.cycle();
				}

			}
		}, 0, 1);

	}
}
