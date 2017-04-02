package org.gdsp.subleq;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AssemblerLoader implements MemoryLoader {

	private final static Pattern INT = Pattern.compile("^-?[0-9]+$");
	private final static Pattern NONINT = Pattern.compile("[^\\-0-9]*");

	private final File file;

	public AssemblerLoader(final File file) {
		super();
		this.file = file;
	}

	@Override
	public void loadMem(final Interconnect ic) throws FileNotFoundException {
		final Map<String, Integer> tokenPos = new HashMap<>();
		int i = 0;
		// Initial parse
		try (final Scanner scanner = new Scanner(this.file)) {
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
		try (final Scanner scanner = new Scanner(this.file)) {
			while (scanner.hasNext()) {
				final String nextLine = scanner.nextLine().trim();
				if (nextLine.startsWith("\"") && nextLine.endsWith("\"")) {
					for (int j = 1, n = nextLine.length() - 1; j < n; j++) {
						ic.setMem(i++, nextLine.charAt(j));
					}
				} else {
					try (Scanner scanner2 = new Scanner(nextLine)) {
						while (scanner2.hasNext()) {
							final String next = scanner2.next();
							if (INT.matcher(next).find()) {
								ic.setMem(i++, Integer.parseInt(next));
							} else if ("#".equals(next)) {
								break;
							} else if (next.endsWith(":")) {
								break;
							} else {
								final int calc = eval(next, tokenPos);
								ic.setMem(i++, calc);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Simple eval method taken from http://stackoverflow.com/a/26227947
	 * Simplified to work only with integers and simple operators
	 *
	 * @param str
	 *            Calculation string to evaluate
	 * @param tokenPos
	 *            Map of all the token names to position integer
	 * @return memory location referred to in str
	 */
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
