public class MultiplicationExpression implements Expression {
	// Instance variables.
	private Expression _leftExpression;
	private Expression _rightExpression;
	private String _multiplyOrDivide;

	/**
	 * Constructor for the class that accepts two expressions, where one is the left
	 * portion and the other is the left portion of a multiplication or division
	 * expression. The third accepted variable determines if it is multiplication or
	 * division.
	 * 
	 * @param leftExpression   the left portion of the multiplication or division
	 *                         (also the numerator in the division case).
	 * @param rightExpression  the right portion of the multiplication or division
	 *                         (also the denominator in the division case).
	 * @param multiplyOrDivide * if multiplication, / if division.
	 */
	public MultiplicationExpression(Expression leftExpression, Expression rightExpression, String multiplyOrDivide) {
		_leftExpression = leftExpression;
		_rightExpression = rightExpression;
		_multiplyOrDivide = multiplyOrDivide;
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

		// Append the * or / and the two expressions.
		answer += this._multiplyOrDivide + "\n" + this._leftExpression.convertToString(indentLevel + 1)
				+ this._rightExpression.convertToString(indentLevel + 1);

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
		// Return the value of the left expression evaluated at x multiplied by the
		// right expression evaluated at x if needed.
		if (this._multiplyOrDivide.equals("*")) {
			// This code is needed to ensure that derivatives of exponents are able to be
			// evaluated when x is 0 or negative. In simple cases of derivatives of
			// exponents, one half of a summation is a logarithm is multiplied by 0.
			// However, this creates an issue because the logarithm cannot be evaluated when
			// less than or equal to 0. So, when this occurs the expression is simply
			// evaluated as 0.
			if ((x <= 0 && this._leftExpression instanceof ExponentiationExpression
					&& this._rightExpression.evaluate(x) == 0)
					|| (x <= 0 && this._rightExpression instanceof ExponentiationExpression
							&& this._leftExpression.evaluate(x) == 0)) {
				return 0;
			}

			return this._leftExpression.evaluate(x) * this._rightExpression.evaluate(x);
		}

		// Return the value of the left expression evaluated at x divided by the right
		// expression evaluated at x.
		return this._leftExpression.evaluate(x) / this._rightExpression.evaluate(x);
	}

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees)
	 * Expression representing the derivative of this expression.
	 * 
	 * @return the derivative of this expression.
	 */
	public Expression differentiate() {
		// Return the differentiation of the multiplication if needed using
		// differentiation rules.
		if (this._multiplyOrDivide.equals("*")) {
			MultiplicationExpression leftSide = new MultiplicationExpression(this._leftExpression.differentiate(),
					this._rightExpression, "*");
			MultiplicationExpression rightSide = new MultiplicationExpression(this._leftExpression,
					this._rightExpression.differentiate(), "*");
			SumExpression answer = new SumExpression(leftSide, rightSide, "+");
			return answer.deepCopy();
		}

		// Return the differentiation of the division using differentiation rules.
		MultiplicationExpression leftSide = new MultiplicationExpression(this._leftExpression.differentiate(),
				this._rightExpression, "*");
		MultiplicationExpression rightSide = new MultiplicationExpression(this._leftExpression,
				this._rightExpression.differentiate(), "*");
		SumExpression numerator = new SumExpression(leftSide, rightSide, "-");
		LiteralExpression exponent = new LiteralExpression("2");
		ExponentiationExpression denominator = new ExponentiationExpression(this._rightExpression, exponent, "^");
		MultiplicationExpression answer = new MultiplicationExpression(numerator, denominator, "/");
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
		// Create a new copy of the multiplication if needed.
		if (this._multiplyOrDivide.equals("*")) {
			return new MultiplicationExpression(this._leftExpression.deepCopy(), this._rightExpression.deepCopy(), "*");
		}

		// Create a new copy of the division.
		return new MultiplicationExpression(this._leftExpression.deepCopy(), this._rightExpression.deepCopy(), "/");
	}
}