public class SumExpression implements Expression {
	// Instance variables.
	private Expression _leftExpression;
	private Expression _rightExpression;
	private String _addOrSubract;

	/**
	 * Constructor for the class that accepts two expressions, where one is the left
	 * portion and the other is the left portion of an addition or subtraction
	 * expression. The third accepted variable determines if it is addition or
	 * subtraction.
	 * 
	 * @param leftExpression  the left portion of the addition or subtraction.
	 * @param rightExpression the right portion of the addition or subtraction.
	 * @param addOrSubract    + if addition, - if subtraction.
	 */
	public SumExpression(Expression leftExpression, Expression rightExpression, String addOrSubract) {
		_leftExpression = leftExpression;
		_rightExpression = rightExpression;
		_addOrSubract = addOrSubract;
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

		// Append the + or - and the two expressions.
		answer += this._addOrSubract + "\n" + this._leftExpression.convertToString(indentLevel + 1)
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
		// Return the value of the left expression evaluated at x added to the right
		// expression evaluated at x if needed.
		if (this._addOrSubract.equals("+")) {
			return this._leftExpression.evaluate(x) + this._rightExpression.evaluate(x);
		}

		// Return the value of the right expression evaluated at x subtracted from the
		// left expression evaluated at x.
		return this._leftExpression.evaluate(x) - this._rightExpression.evaluate(x);
	}

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees)
	 * Expression representing the derivative of this expression.
	 * 
	 * @return the derivative of this expression.
	 */
	public Expression differentiate() {
		// Return the differentiation of the addition if needed using differentiation
		// rules.
		if (this._addOrSubract.equals("+")) {
			SumExpression answer = new SumExpression(this._leftExpression.differentiate(),
					this._rightExpression.differentiate(), "+");
			return answer.deepCopy();
		}

		// Return the differentiation of the subtraction using differentiation rules.
		SumExpression answer = new SumExpression(this._leftExpression.differentiate(),
				this._rightExpression.differentiate(), "-");
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
		// Create a new copy of the addition if needed.
		if (this._addOrSubract.equals("+")) {
			return new SumExpression(this._leftExpression.deepCopy(), this._rightExpression.deepCopy(), "+");
		}
		
		// Create a new copy of the subtraction.
		return new SumExpression(this._leftExpression.deepCopy(), this._rightExpression.deepCopy(), "-");
	}
}