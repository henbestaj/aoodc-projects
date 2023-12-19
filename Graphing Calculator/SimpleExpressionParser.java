import java.util.Arrays;
import java.util.function.*;

public class SimpleExpressionParser implements ExpressionParser {
	/*
	 * Attempts to create an expression tree from the specified String. Throws a
	 * ExpressionParseException if the specified string cannot be parsed. Grammar: S
	 * -> A | P, A -> A+M | A-M | M, M -> M*E | M/E | E, E -> P^E | P | log(P), P ->
	 * (S) | L | V, L -> <float>, and V -> x.
	 * 
	 * @param str the string to parse into an expression tree
	 * 
	 * @return the Expression object representing the parsed expression tree
	 */
	public Expression parse(String str) throws ExpressionParseException {
		str = str.replaceAll(" ", "");
		Expression expression = parseSumExpression(str);
		if (expression == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}
		return expression;
	}

	/**
	 * Checks the ends of an array of characters to ensure that they are valid.
	 * 
	 * @param charArray the array of characters to check.
	 * @return null if not valid, "!" if valid.
	 */
	protected String checkEnds(char[] charArray) {
		// Check to see if front is a valid character.
		if (charArray.length > 0 && charArray[0] != '.' && charArray[0] != '1' && charArray[0] != '2'
				&& charArray[0] != '3' && charArray[0] != '4' && charArray[0] != '5' && charArray[0] != '6'
				&& charArray[0] != '7' && charArray[0] != '8' && charArray[0] != '9' && charArray[0] != '0'
				&& charArray[0] != '-' && charArray[0] != '(' && charArray[0] != 'x' && charArray[0] != 'l') {
			return null;
		}

		// Check to see if end is a valid character.
		if (charArray.length > 0 && charArray[charArray.length - 1] != '.' && charArray[charArray.length - 1] != '1'
				&& charArray[charArray.length - 1] != '2' && charArray[charArray.length - 1] != '3'
				&& charArray[charArray.length - 1] != '4' && charArray[charArray.length - 1] != '5'
				&& charArray[charArray.length - 1] != '6' && charArray[charArray.length - 1] != '7'
				&& charArray[charArray.length - 1] != '8' && charArray[charArray.length - 1] != '9'
				&& charArray[charArray.length - 1] != '0' && charArray[charArray.length - 1] != ')'
				&& charArray[charArray.length - 1] != 'x') {
			return null;
		}

		// Check to see if front is two negative signs.
		if (charArray.length > 1 && charArray[0] == '-' && charArray[1] == '-') {
			return null;
		}

		// Return "!" if all tests are passed.
		return "!";
	}

	/**
	 * Check if there are repeating characters that should not repeat in the array
	 * of characters starting at the given index.
	 * 
	 * @param charArray the array of characters to check.
	 * @param i         the index to test at.
	 * @return null if not valid, "!" if valid.
	 */
	protected String checkRepeats(char[] charArray, int i) {
		// Check if operation symbols repeat.
		if (charArray.length > i + 1
				&& (charArray[i] == '+' || charArray[i] == '-' || charArray[i] == '*' || charArray[i] == '/'
						|| charArray[i] == '^')
				&& (charArray[i + 1] == '+' || charArray[i + 1] == '*' || charArray[i + 1] == '/'
						|| charArray[i + 1] == '^')) {
			return null;
		}

		// Check if logarithms repeat.
		if (charArray.length > i + 1 && charArray[i] == 'g' && charArray[i + 1] == 'l') {
			return null;
		}

		// Check if variables repeat.
		if (charArray.length > i + 1 && charArray[i] == 'x' && charArray[i + 1] == 'x') {
			return null;
		}

		// Check if two minus signs do not proceed a number.
		if (charArray.length > i + 2 && charArray[i] == '-' && charArray[i + 1] == '-' && charArray[i + 2] != '.'
				&& charArray[i + 2] != '0' && charArray[i + 2] != '1' && charArray[i + 2] != '2'
				&& charArray[i + 2] != '3' && charArray[i + 2] != '4' && charArray[i + 2] != '5'
				&& charArray[i + 2] != '6' && charArray[i + 2] != '7' && charArray[i + 2] != '8'
				&& charArray[i + 2] != '9') {
			return null;
		}

		// Check if decimal is surrounded by only symbols.
		if (i != 0 && charArray.length > 1 + 1 && charArray[i] == '.'
				&& (charArray[i - 1] == '^' || charArray[i - 1] == '+' || charArray[i - 1] == '-'
						|| charArray[i - 1] == '*' || charArray[i - 1] == '/')
				&& (charArray[i + 1] == '^' || charArray[i + 1] == '+' || charArray[i + 1] == '-'
						|| charArray[i + 1] == '*' || charArray[i + 1] == '/')) {
			return null;
		}

		// Return "!" if all tests are passed.
		return "!";
	}

	/**
	 * Check the string to ensure that it can be parsed correctly.
	 * 
	 * @param str the string to check.
	 * @return null if not valid, original string if valid.
	 */
	protected String checkExpression(String str) {
		// Convert string to array.
		char[] charArray = str.toCharArray();

		// Depth of parentheses and decimals at the current time in evaluation.
		int parenthesesDepth = 0;
		int decimalDepth = 0;

		if (checkEnds(charArray) == null) {
			return null;
		}

		for (int i = 0; i < charArray.length; i++) {
			// Check for repeating characters that should not repeat.
			if (checkRepeats(charArray, i) == null) {
				return null;
			}

			// Check if multiple decimals are in a number.
			if (decimalDepth > 1) {
				return null;
			} else if (charArray[i] == '.') {
				decimalDepth++;
			} else if (charArray[i] == '^' || charArray[i] == '+' || charArray[i] == '-' || charArray[i] == '*'
					|| charArray[i] == '/') {
				decimalDepth = 0;
			}

			// Check if parentheses make sense.
			if (parenthesesDepth < 0) {
				return null;
			} else if (charArray[i] == '(') {
				parenthesesDepth++;

				// Check if a close parentheses comes immediately after an open parentheses.
				if (i + 1 < charArray.length && charArray[i + 1] == ')') {
					return null;
				}
			} else if (charArray[i] == ')') {
				parenthesesDepth--;
			}
		}

		// Check if there are an even number of open and close parentheses.
		if (parenthesesDepth != 0) {
			return null;
		}

		// Return original string if all tests pass.
		return str;
	}

	/**
	 * Parses a given string into an expression based on addition (+) and
	 * subtraction (-) symbols.
	 * 
	 * @param str the string to parse.
	 * @return the expression parsed from the string.
	 * @throws ExpressionParseException
	 */
	protected Expression parseSumExpression(String str) throws ExpressionParseException {
		// Ensure string is usable.
		str = checkExpression(str);

		// Check if empty.
		if (str == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Convert string to array.
		char[] charArray = str.toCharArray();

		// Depth of parentheses at the current time in evaluation.
		int parenthesesDepth = 0;

		// Loop through array of characters.
		for (int i = charArray.length - 1; i > 0; i--) {
			// Ensure parentheses make sense at all times and keep track of depth.
			if (parenthesesDepth < 0) {
				return null;
			} else if (charArray[i] == ')') {
				parenthesesDepth++;
			} else if (charArray[i] == '(') {
				parenthesesDepth--;
			}

			// Parse when an addition or subtraction symbol is found.
			else if ((charArray[i] == '+' || charArray[i] == '-') && parenthesesDepth == 0) {
				if (i != 0 && charArray[i] == '-' && charArray[i - 1] == '-') {
					i--;
				}
				String firstPart = new String(Arrays.copyOfRange(charArray, 0, i));
				String secondPart = new String(Arrays.copyOfRange(charArray, i + 1, charArray.length));
				return new SumExpression(parseSumExpression(firstPart), parseMultiplicationExpression(secondPart),
						"" + charArray[i]);
			}
		}

		// Move up PEMDAS if no addition or subtraction symbols.
		return parseMultiplicationExpression(str);
	}

	/**
	 * Parses a given string into an expression based on multiplication (*) and
	 * division (/) symbols.
	 * 
	 * @param str the string to parse.
	 * @return the expression parsed from the string.
	 * @throws ExpressionParseException
	 */
	protected Expression parseMultiplicationExpression(String str) throws ExpressionParseException {
		// Ensure string is usable.
		str = checkExpression(str);

		// Check if empty.
		if (str == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Convert string to array.
		char[] charArray = str.toCharArray();

		// Depth of parentheses at the current time in evaluation.
		int parenthesesDepth = 0;

		// Loop through array of characters.
		for (int i = charArray.length - 1; i > 0; i--) {
			// Ensure parentheses make sense at all times and keep track of depth.
			if (parenthesesDepth < 0) {
				return null;
			} else if (charArray[i] == ')') {
				parenthesesDepth++;
			} else if (charArray[i] == '(') {
				parenthesesDepth--;
			}

			// Parse when a multiplication or division symbol is found.
			else if ((charArray[i] == '*' || charArray[i] == '/') && parenthesesDepth == 0) {
				String firstPart = new String(Arrays.copyOfRange(charArray, 0, i));
				String secondPart = new String(Arrays.copyOfRange(charArray, i + 1, charArray.length));
				return new MultiplicationExpression(parseMultiplicationExpression(firstPart),
						parseExponentiationExpression(secondPart), "" + charArray[i]);
			}
		}

		// Move up PEMDAS if no multiplication or division symbols.
		return parseExponentiationExpression(str);
	}

	/**
	 * Parses a given string into an expression based on exponent (^) and logarithm
	 * (log) symbols.
	 * 
	 * @param str the string to parse.
	 * @return the expression parsed from the string.
	 * @throws ExpressionParseException
	 */
	protected Expression parseExponentiationExpression(String str) throws ExpressionParseException {
		// Ensure string is usable.
		str = checkExpression(str);

		// Check if empty.
		if (str == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Convert string to array.
		char[] charArray = str.toCharArray();

		// Depth of parentheses at the current time in evaluation.
		int parenthesesDepth = 0;

		// Loop through array of characters.
		for (int i = 0; i < charArray.length - 1; i++) {
			// Ensure parentheses make sense at all times and keep track of depth.
			if (parenthesesDepth < 0) {
				return null;
			} else if (charArray[i] == '(') {
				parenthesesDepth++;
			} else if (charArray[i] == ')') {
				parenthesesDepth--;
			}

			// Parse when a exponent symbol is found.
			else if (charArray[i] == '^' && parenthesesDepth == 0) {
				String firstPart = new String(Arrays.copyOfRange(charArray, 0, i));
				String secondPart = new String(Arrays.copyOfRange(charArray, i + 1, charArray.length));
				return new ExponentiationExpression(parseParenthesesExpression(firstPart),
						parseExponentiationExpression(secondPart), "^");
			}
		}

		// Parse when a logarithm symbol is found.
		if (charArray.length >= 3 && charArray[0] == 'l' && charArray[1] == 'o' && charArray[2] == 'g') {
			String logged = new String(Arrays.copyOfRange(charArray, 3, charArray.length));
			return new ExponentiationExpression(parseParenthesesExpression(logged), null, "log");
		}

		// Move up PEMDAS if no exponent or logarithm symbols.
		return parseParenthesesExpression(str);
	}

	/**
	 * Parses a given string into an expression based on parentheses.
	 * 
	 * @param str the string to parse.
	 * @return the expression parsed from the string.
	 * @throws ExpressionParseException
	 */
	protected Expression parseParenthesesExpression(String str) throws ExpressionParseException {
		// Ensure string is usable.
		str = checkExpression(str);

		// Check if empty.
		if (str == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Convert string to array.
		char[] charArray = str.toCharArray();

		// Parse when parentheses are found.
		if (charArray.length > 2 && charArray[0] == '(' && charArray[charArray.length - 1] == ')') {
			String inside = new String(Arrays.copyOfRange(charArray, 1, charArray.length - 1));
			return new ParenthesesExpression(parseSumExpression(inside));
		}

		// Send to variable parser if the string is a variable.
		else if (charArray.length == 1 && charArray[0] == 'x') {
			return parseVariableExpression(str);
		}

		// Send to literal parser if the string is not in parentheses and is not a
		// variable.
		return parseLiteralExpression(str);
	}

	protected VariableExpression parseVariableExpression(String str) throws ExpressionParseException {
		// Ensure string is usable.
		str = checkExpression(str);

		// Check if empty.
		if (str == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		if (str.equals("x")) {
			return new VariableExpression();
		}
		return null;
	}

	protected LiteralExpression parseLiteralExpression(String str) throws ExpressionParseException {
		// Ensure string is usable.
		str = checkExpression(str);

		// Check if empty.
		if (str == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// From
		// https://stackoverflow.com/questions/3543729/how-to-check-that-a-string-is-parseable-to-a-double/22936891:
		final String Digits = "(\\p{Digit}+)";
		final String HexDigits = "(\\p{XDigit}+)";
		// an exponent is 'e' or 'E' followed by an optionally
		// signed decimal integer.
		final String Exp = "[eE][+-]?" + Digits;
		final String fpRegex = ("[\\x00-\\x20]*" + // Optional leading "whitespace"
				"[+-]?(" + // Optional sign character
				"NaN|" + // "NaN" string
				"Infinity|" + // "Infinity" string

				// A decimal floating-point string representing a finite positive
				// number without a leading sign has at most five basic pieces:
				// Digits . Digits ExponentPart FloatTypeSuffix
				//
				// Since this method allows integer-only strings as input
				// in addition to strings of floating-point literals, the
				// two sub-patterns below are simplifications of the grammar
				// productions from the Java Language Specification, 2nd
				// edition, section 3.10.2.

				// Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
				"(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +

				// . Digits ExponentPart_opt FloatTypeSuffix_opt
				"(\\.(" + Digits + ")(" + Exp + ")?)|" +

				// Hexadecimal strings
				"((" +
				// 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
				"(0[xX]" + HexDigits + "(\\.)?)|" +

				// 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
				"(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

				")[pP][+-]?" + Digits + "))" + "[fFdD]?))" + "[\\x00-\\x20]*");// Optional trailing "whitespace"

		if (str.matches(fpRegex)) {
			return new LiteralExpression(str);
		}
		return null;
	}

	public static void main(String[] args) throws ExpressionParseException {
		final ExpressionParser parser = new SimpleExpressionParser();
		System.out.println(parser.parse("10*2+12-4.").convertToString(0));
	}
}