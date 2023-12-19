public class ExponentiationExpression implements Expression {
	// Instance variables.
	private Expression _baseExpression;
	private Expression _exponentExpression;
	private String _logOrExponent;

	/**
	 * Constructor for the class that accepts two expressions, where one is the base
	 * and one is the exponent for exponentiation or one is the inside of a
	 * logarithm and one is null for logarithms. The third accepted variable
	 * determines if it is exponentiation or a logarithm.
	 * 
	 * @param baseExpression     the base expression or the inside of the logarithm.
	 * @param exponentExpression the exponent or null.
	 * @param logOrExponent      ^ if exponentiation, log if logarithm.
	 */
	public ExponentiationExpression(Expression baseExpression, Expression exponentExpression, String logOrExponent) {
		_baseExpression = baseExpression;
		_exponentExpression = exponentExpression;
		_logOrExponent = logOrExponent;
	}

	/**
	 * Creates a String representation of this expression with a given starting
	 * indent level. If indentLevel is 0, then the produced string should have no
	 * indent; if the indentLevel is 1, then there should be 1 tab '\t' character at
	 * the start of every line produced by this method; etc.
	 * 
	 * @param indentLevel how many tab characters should appear at the beginning of
	 *                    each line.
	 * @return the String representing this expression.
	 */
	public String convertToString(int indentLevel) {
		// Where the answer will be stored.
		String answer = "";

		// Add the initial tabs.
		for (int i = 0; i < indentLevel; i++) {
			answer += "\t";
		}

		// Append the ^ and the base and exponent if needed.
		if (this._logOrExponent.equals("^")) {
			answer += this._logOrExponent + "\n" + this._baseExpression.convertToString(indentLevel + 1)
					+ this._exponentExpression.convertToString(indentLevel + 1);
		}

		// Append the log and expression inside the logarithm if needed.
		else if (this._logOrExponent.equals("log")) {
			answer += this._logOrExponent + "\n" + this._baseExpression.convertToString(indentLevel + 1);
		}

		// Return the answer.
		return answer;
	}

	/**
	 * Given the value of the independent variable x, compute the value of this
	 * expression.
	 * 
	 * @param x the value of the independent variable x.
	 * @return the value of this expression.
	 */
	public double evaluate(double x) {
		// Return the value of the base evaluated at x brought to the power of the
		// exponent evaluated at x if needed.
		if (this._logOrExponent.equals("^")) {
			return Math.pow(this._baseExpression.evaluate(x), this._exponentExpression.evaluate(x));
		}

		// Return the value of the natural logarithm of the expression inside the
		// logarithm evaluated at x.
		return Math.log(this._baseExpression.evaluate(x));
	}

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees)
	 * Expression representing the derivative of this expression.
	 * 
	 * @return the derivative of this expression.
	 */
	public Expression differentiate() {
		// Return the differentiation of the exponentiation if needed using
		// differentiation rules.
		if (this._logOrExponent.equals("^")) {
			ExponentiationExpression logBase = new ExponentiationExpression(this._baseExpression, null, "log");
			MultiplicationExpression innerMultiply = new MultiplicationExpression(logBase, this._exponentExpression,
					"*");
			MultiplicationExpression answer = new MultiplicationExpression(this, innerMultiply.differentiate(), "*");
			return answer.deepCopy();
		}

		// Return the differentiation of the logarithm using differentiation rules.
		MultiplicationExpression answer = new MultiplicationExpression(this._baseExpression.differentiate(),
				this._baseExpression, "/");
		return answer.deepCopy();
	}

	/**
	 * Creates and returns a deep copy of the expression. The entire tree rooted at
	 * the target node is copied, i.e., the copied Expression is as deep as
	 * possible.
	 * 
	 * @return the deep copy.
	 */
	public Expression deepCopy() {
		// Create a new copy of the exponentiation if needed.
		if (this._logOrExponent.equals("^")) {
			return new ExponentiationExpression(this._baseExpression.deepCopy(), this._exponentExpression.deepCopy(),
					"^");
		}

		// Create a new copy of the logarithm.
		return new ExponentiationExpression(this._baseExpression.deepCopy(), null, "log");
	}
}