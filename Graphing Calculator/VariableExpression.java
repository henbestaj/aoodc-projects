public class VariableExpression implements Expression {
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

		// Append the variable.
		answer += "x\n";

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
		// Return the value of the variable.
		return x;
	}

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees)
	 * Expression representing the derivative of this expression.
	 * 
	 * @return the derivative of this expression.
	 */
	public Expression differentiate() {
		// Return 1 as variables always differentiate to 1.
		return new LiteralExpression("1");
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
		return new VariableExpression();
	}

}