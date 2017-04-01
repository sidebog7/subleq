package org.gdsp.subleq;

import java.util.Collection;
import java.util.TreeSet;

public class Interconnect {

	private final Memory memory;
	private final Collection<Unit> units = new TreeSet<>(
			(o1, o2) -> o1.getPriority() > o2.getPriority() ? 1 : o1.getPriority() < o2.getPriority() ? -1 : 0);

	public Interconnect(final Memory memory) {
		this.memory = memory;
		this.addUnit(memory);
	}

	public void addUnit(final Unit u) {
		this.units.add(u);
	}

	public void cycle() {
		for (final Unit unit : this.units) {
			unit.cycle(this);
		}
	}

	public int readMem(final int pos) {
		return this.memory.get(pos);
	}

	public void setMem(final int pos, final int val) {
		this.memory.set(pos, val);
	}
}
