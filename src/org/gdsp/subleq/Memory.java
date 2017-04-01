package org.gdsp.subleq;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Memory implements Unit {

	private final static Pattern INT = Pattern.compile("^-?[0-9]+$");
	private final static Pattern NONINT = Pattern.compile("[^\\-0-9]*");
	public final static int MAXMEMORY = 16384;

	private final int[] memory = new int[MAXMEMORY];

	private void betterLoad(final File file) throws FileNotFoundException {
		final Map<String, Integer> tokenPos = new HashMap<>();
		int i = 0;
		// Initial parse
		try (final Scanner scanner = new Scanner(file)) {
			while (scanner.hasNext()) {
				final String next = scanner.next();
				if ("#".equals(next)) {
					scanner.nextLine();
				} else if (next.endsWith(":")) {
					tokenPos.put(next.substring(0, next.length() - 1).toUpperCase(), i);
				} else {
					i++;
				}
			}
		}
		i = 0;
		try (final Scanner scanner = new Scanner(file)) {
			while (scanner.hasNext()) {
				final String nextLine = scanner.nextLine().trim();
				if (nextLine.startsWith("\"") && nextLine.endsWith("\"")) {
					for (int j = 1, n = nextLine.length() - 1; j < n; j++) {
						this.memory[i++] = nextLine.charAt(j);
					}
				} else {
					try (Scanner scanner2 = new Scanner(nextLine)) {
						while (scanner2.hasNext()) {
							final String next = scanner2.next();
							if (INT.matcher(next).find()) {
								this.memory[i++] = Integer.parseInt(next);
							} else if ("#".equals(next)) {
								break;
							} else if (next.endsWith(":")) {
								break;
							} else {
								final int calc = eval(next, tokenPos);
								this.memory[i++] = calc;
							}
						}
					}
				}
			}
		}

		// for (int j = 0; j < i; j += 3) {
		// System.out.println(j + ": " + this.memory[j] + " " + this.memory[j +
		// 1] + " " + this.memory[j + 2]);
		// }
		// System.exit(0);
	}

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

	public void load(final File file) throws FileNotFoundException {
		this.betterLoad(file);
	}

	public void set(final int pos, final int val) {
		this.memory[pos] = val;
	}

	private void simpleLoad(final File file) throws FileNotFoundException {
		try (final Scanner scanner = new Scanner(file)) {
			int i = 0;
			scanner.skip(NONINT);
			while (scanner.hasNextInt()) {
				this.memory[i++] = scanner.nextInt();
				scanner.skip(NONINT);
			}
		}
	}

	public static int eval(final String str, final Map<String, Integer> tokenPos) {
		return new Object() {
			int pos = -1, ch;

			boolean eat(final int charToEat) {
				while (this.ch == ' ') {
					this.nextChar();
				}
				if (this.ch == charToEat) {
					this.nextChar();
					return true;
				}
				return false;
			}

			void nextChar() {
				this.ch = ++this.pos < str.length() ? str.charAt(this.pos) : -1;
			}

			int parse() {
				this.nextChar();
				final int x = this.parseExpression();
				if (this.pos < str.length()) {
					throw new RuntimeException("Unexpected: " + (char) this.ch);
				}
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)`
			// | number | functionName factor | factor `^` factor

			int parseExpression() {
				int x = this.parseTerm();
				for (;;) {
					if (this.eat('+')) {
						x += this.parseTerm(); // addition
					} else if (this.eat('-')) {
						x -= this.parseTerm(); // subtraction
					} else {
						return x;
					}
				}
			}

			int parseFactor() {
				if (this.eat('+')) {
					return this.parseFactor(); // unary plus
				}
				if (this.eat('-')) {
					return -this.parseFactor(); // unary minus
				}

				int x;
				final int startPos = this.pos;
				if (this.eat('(')) { // parentheses
					x = this.parseExpression();
					this.eat(')');
				} else if (this.ch >= '0' && this.ch <= '9') { // numbers
					while (this.ch >= '0' && this.ch <= '9') {
						this.nextChar();
					}
					x = Integer.parseInt(str.substring(startPos, this.pos));
				} else if (Character.isLetter(this.ch)) { // functions
					while (Character.isLetter(this.ch)) {
						this.nextChar();
					}
					final String func = str.substring(startPos, this.pos);
					final Integer pos = tokenPos.get(func.toUpperCase());
					if (pos == null) {
						throw new RuntimeException("Unknown token: " + func);
					}
					x = pos;
				} else {
					throw new RuntimeException("Unexpected: " + (char) this.ch);
				}

				return x;
			}

			int parseTerm() {
				int x = this.parseFactor();
				for (;;) {
					if (this.eat('*')) {
						x *= this.parseFactor(); // multiplication
					} else if (this.eat('/')) {
						x /= this.parseFactor(); // division
					} else {
						return x;
					}
				}
			}
		}.parse();
	}
}
