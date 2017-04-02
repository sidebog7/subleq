package org.gdsp.subleq;

import java.util.Collection;
import java.util.TreeSet;

public class Interconnect {

	private final Memory memory;
	private final Memory[] banks;
	private final int currentBank = 0;

	private final Collection<Unit> units = new TreeSet<>(
			(o1, o2) -> o1.getPriority() > o2.getPriority() ? 1 : o1.getPriority() < o2.getPriority() ? -1 : 0);

	public Interconnect() {
		this.memory = new Memory();
		this.banks = new Memory[4];
		for (int i = this.banks.length; --i >= 0;) {
			this.banks[i] = new Memory();
		}
		this.addUnit(this.memory);
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
		if (pos >= this.memory.size()) {
			return this.banks[this.currentBank].get(pos - this.memory.size());
		} else {
			return this.memory.get(pos);
		}
	}

	public void setMem(final int pos, final int val) {
		if (pos >= this.memory.size()) {
			this.banks[this.currentBank].set(pos - this.memory.size(), val);
		} else {
			this.memory.set(pos, val);
		}
	}
}
