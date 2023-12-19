public class ParenthesesExpression implements Expression {
	// Instance variables.
	private Expression _innerExpression;

	/**
	 * Constructor for the class that accepts an expression that is then considered
	 * to be within parentheses.
	 * 
	 * @param innerExpression the expression that is within parentheses.
	 */
	public ParenthesesExpression(Expression innerExpression) {
		_innerExpression = innerExpression;
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

		// Append the parentheses and what is inside them.
		answer += "()\n" + this._innerExpression.convertToString(indentLevel + 1);

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
		// Return the value of the expression inside the parentheses evaluated at x.
		return this._innerExpression.evaluate(x);
	}

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees)
	 * Expression representing the derivative of this expression.
	 * 
	 * @return the derivative of this expression.
	 */
	public Expression differentiate() {
		// Return the differentiation of the expression inside the parentheses.
		return this._innerExpression.differentiate().deepCopy();
	}

	/**
	 * Creates and returns a deep copy of the expression. The entire tree rooted at
	 * the target node is copied, i.e., the copied Expression is as deep as
	 * possible.
	 * 
	 * @return the deep copy.
	 */
	public Expression deepCopy() {
		// Create a new copy of the expression.
		return new ParenthesesExpression(this._innerExpression.deepCopy());
	}

}