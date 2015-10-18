import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Polynomial {
	List<ExpressionAtom> infixExpression = new ArrayList<ExpressionAtom>();
	List<ExpressionAtom> finalExpression;

	final SimpleStack<ExpressionAtom> simplyfiedExpression = new SimpleStack<ExpressionAtom>();
	final SimpleStack<ExpressionAtom> operandsStack = new SimpleStack<ExpressionAtom>();

	ListRepresentation listRepresentation;

	private final static Map<String, Operators> operatorsMap = new HashMap<>();

	static {
		operatorsMap.put("+", Operators.ADD);
		operatorsMap.put("-", Operators.SUBTRACT);
		operatorsMap.put("*", Operators.MULTIPLY);
		operatorsMap.put("^", Operators.EXP);
		operatorsMap.put("%", Operators.NEGATION);
	}

	/**
	 * Method that identifies the unary minuses in a given expression
	 * 
	 * @param String
	 *            expression : The given expression
	 * @return String
	 */
	private String identifyUnaryMinuses(String expression) {
		if (expression.startsWith("-")) {
			expression = "%" + expression.substring(1);
		}

		char[] expressionAtoms = expression.toCharArray();
		String returnExpression = String.valueOf(expressionAtoms[0]);
		for (int i = 1; i < expressionAtoms.length; ++i) {
			if (expressionAtoms[i] == '-' && expressionAtoms[i - 1] == '(')
				returnExpression += "%";
			else
				returnExpression += String.valueOf(expressionAtoms[i]);
		}

		return returnExpression;
	}

	/**
	 * Method that takes care of inserting multiplication signs at appropriate
	 * places
	 * 
	 * @param String
	 *            expression : The given expression
	 * @return String
	 */
	private String insertMultiplicationSigns(String expression) {
		char[] expressionAtoms = expression.toCharArray();
		String returnExpression = String.valueOf(expressionAtoms[0]);

		for (int i = 1; i < expressionAtoms.length; ++i) {
			if (!isOperator(expressionAtoms[i])
					&& !Character.isDigit(expressionAtoms[i])
					&& Character.isDigit(expressionAtoms[i - 1]))
				returnExpression += "*";
			returnExpression += String.valueOf(expressionAtoms[i]);
		}

		return returnExpression;
	}

	/**
	 * Method that checks if a given token is an operator
	 * 
	 * @param Char
	 *            token : The given token
	 * @return Boolean
	 */
	private boolean isOperator(char token) {
		return token == '+' || token == '-' || token == '*' || token == '^'
				|| token == '(' || token == ')';
	}

	/**
	 * Method that takes care of parsing the polynomial
	 * 
	 * @param String
	 *            inputExpression : The given expression
	 * @return List<ExpressionAtom>
	 */
	private List<ExpressionAtom> parseInputPolynomial(String inputExpression) {
		inputExpression = identifyUnaryMinuses(inputExpression);
		inputExpression = insertMultiplicationSigns(inputExpression);

		List<ExpressionAtom> inputExpressionTokens = new ArrayList<ExpressionAtom>();

		char[] inputChars = inputExpression.toCharArray();
		for (int i = 0; i < inputChars.length; ++i) {
			if (isOperator(inputChars[i]) || inputChars[i] == '%') {
				inputExpressionTokens.add(new ExpressionAtom(String
						.valueOf(inputChars[i]), AtomType.OPERATOR, 1));
			} else {
				int lastIndex = inputExpressionTokens.size() - 1;
				if (lastIndex >= 0
						&& inputExpressionTokens.get(lastIndex).getAtomType() == AtomType.OPERAND) {
					ExpressionAtom lastElement = inputExpressionTokens
							.remove(lastIndex);
					if (Character.isDigit(inputChars[i])) {
						lastElement
								.setCoefficient(lastElement.getCoefficient()
										* 10
										+ Character
												.getNumericValue(inputChars[i]));
					} else {
						lastElement.setVariablesOrOperator(lastElement
								.getVariablesOrOperator()
								+ String.valueOf(inputChars[i]));
					}
					inputExpressionTokens.add(lastElement);
				} else if (Character.isDigit(inputChars[i])) {
					inputExpressionTokens.add(new ExpressionAtom("",
							AtomType.OPERAND, Character
									.getNumericValue(inputChars[i])));
				} else {
					inputExpressionTokens.add(new ExpressionAtom(String
							.valueOf(inputChars[i]), AtomType.OPERAND, 1));
				}
			}
		}
		return inputExpressionTokens;
	}

	/*
	 * Convert infix to postfix expression then simplify the expression
	 */
	/**
	 * Method that converts infix to postfix expression and then simplifies the
	 * expression
	 * 
	 * @param void
	 * @return ListRepresentation
	 */
	private ListRepresentation convertToListRepresentation() {

		// Use queue for the Postfix expression
		SimpleQueue<ExpressionAtom> postfix = new SimpleQueue<ExpressionAtom>();
		// Stack to hold the operators
		SimpleStack<ExpressionAtom> operatorStack = new SimpleStack<ExpressionAtom>();

		// Convert infix to Postfix and store the expression in Postfix queue
		for (ExpressionAtom operandOrOperator : infixExpression) {
			if (operandOrOperator.getAtomType() == AtomType.OPERAND) {
				postfix.enqueue(operandOrOperator);
			} else if (operandOrOperator.getAtomType() == AtomType.OPERATOR
					&& !operandOrOperator.getVariablesOrOperator().equals("(")
					&& !operandOrOperator.getVariablesOrOperator().equals(")")) {
				while (!operatorStack.isEmpty()) {
					ExpressionAtom peek = operatorStack.peek();
					if (isTopwithHigerPrecedence(peek, operandOrOperator)) {
						postfix.enqueue(operatorStack.pop());
					} else
						break;
				}
				operatorStack.push(operandOrOperator);
			} else if (operandOrOperator.getVariablesOrOperator().equals("(")) {
				operatorStack.push(operandOrOperator);
			} else if (operandOrOperator.getVariablesOrOperator().equals(")")) {
				boolean isLeftFound = false;
				while (!operatorStack.isEmpty()) {
					ExpressionAtom top = operatorStack.pop();
					if (!top.getVariablesOrOperator().equals("(")) {
						postfix.enqueue(top);
					} else {
						isLeftFound = true;
						break;
					}
				}
				if (!isLeftFound)
					throw new RuntimeException("Parantheses are not balanced");
			}
		}

		// Pop all the remaining operator in the stack
		while (!operatorStack.isEmpty()) {
			ExpressionAtom topOperator = operatorStack.pop();
			if (topOperator.getVariablesOrOperator().equals("(")
					|| topOperator.getVariablesOrOperator().equals(")")) {
				throw new RuntimeException("Parantheses are not balanced");
			}
			postfix.enqueue(topOperator);
		}

		// Contains all operands or operator while converting infix to Postfix
		SimpleStack<ListRepresentation> operands = new SimpleStack<ListRepresentation>();

		while (!postfix.isEmpty()) {
			ExpressionAtom operandOrOperator = postfix.dequeue();

			operandsStack.push(operandOrOperator);
			if (operandOrOperator.getAtomType() == AtomType.OPERAND) {
				ListRepresentation operand = new ListRepresentation();
				operand.setNodeVal(operandOrOperator);
				operands.push(operand);
			} else if (operandOrOperator.getAtomType() == AtomType.OPERATOR) {
				ListRepresentation operator = new ListRepresentation();
				operator.setNodeVal(operandOrOperator);

				if (operandOrOperator.getVariablesOrOperator().equals("%")) {
					operator.operands.add(operands.pop());
				} else {
					operator.operands.add(operands.pop());
					operator.operands.add(0, operands.pop());
				}

				operands.push(operator);
			}
		}

		// Pop the root to the operands Stack
		ListRepresentation root = operands.pop();
		listRepresentation = root;

		// Simplify the expression
		insertNegation(listRepresentation, listRepresentation);
		uniteEqualOperators(listRepresentation, listRepresentation);
		applyNegation(listRepresentation);

		// Remove % in the parent if it exist
		if (listRepresentation.getNodeVal().getVariablesOrOperator()
				.equals("%")) {
			listRepresentation = listRepresentation.operands.get(0);
		}

		return listRepresentation;
	}

	/**
	 * Method that applies negation in the given list expression. This function
	 * prevents unnecessary use of isNegative and tries to find other ways to
	 * negate the operands
	 * 
	 * @param ListRepresentation
	 *            listExpression : The given list expression
	 * @return void
	 */
	public static void applyNegation(ListRepresentation listExpression) {
		if (listExpression.operands.isEmpty()) {
		} else {
			ExpressionAtom atom = listExpression.getNodeVal();
			if (listExpression.isNegative()
					&& !atom.getVariablesOrOperator().equals("^")) {
				if (atom.getVariablesOrOperator().equals("-")
						|| atom.getVariablesOrOperator().equals("+")
						|| atom.getVariablesOrOperator().equals("%")) {
					negateOperands(listExpression, 0);
				} else {
					negateOperands(listExpression, -1);
				}
				listExpression.setNegative(false);
			}

			for (int i = 0; i < listExpression.operands.size(); ++i) {
				applyNegation(listExpression.operands.get(i));
			}
		}
	}

	/**
	 * By using Infix to Postfix we need to simplify the use of same Operators
	 * eg. a*b*c by creating a ListExpression and combine like operators The
	 * method takes care of that
	 * 
	 * @param ListRepresentation
	 *            listExpression : The given list expression
	 * @param ListRepresentation
	 *            parent : The parent list representation
	 * @return void
	 */
	public static void uniteEqualOperators(ListRepresentation listExpression,
			ListRepresentation parent) {
		if (listExpression.operands.isEmpty()) {
		} else {
			for (int i = 0; i < listExpression.operands.size(); ++i) {
				uniteEqualOperators(listExpression.operands.get(i),
						listExpression);
			}
			if (!(parent == listExpression)
					&& !listExpression.isNegative()
					&& parent
							.getNodeVal()
							.getVariablesOrOperator()
							.equals(listExpression.getNodeVal()
									.getVariablesOrOperator())) {
				parent.operands.addAll(parent.operands.indexOf(listExpression),
						listExpression.operands);
				parent.operands.remove(listExpression);
			}
		}
	}

	/**
	 * Method that inserts negation to those who are on the right operand of
	 * subtraction or on negation
	 * 
	 * @param ListRepresentation
	 *            listExpression : The given list expression
	 * @param ListRepresentation
	 *            parent : The parent list representation
	 * @return void
	 */
	public static void insertNegation(ListRepresentation listExpression,
			ListRepresentation parent) {
		if (listExpression.operands.isEmpty()) {
		} else {
			cleanTerms(listExpression, parent);
			for (int i = 0; i < listExpression.operands.size(); ++i) {
				insertNegation(listExpression.operands.get(i), listExpression);
			}
		}
	}

	/**
	 * Method that cleans each term by removing the % sign and negating them,
	 * and removing - sign and negating the right operand
	 * 
	 * @param ListRepresentation
	 *            list : The given list expression
	 * @param ListRepresentation
	 *            parent : The parent list representation
	 * @return void
	 */
	public static void cleanTerms(ListRepresentation list,
			ListRepresentation parent) {
		ExpressionAtom atom = list.getNodeVal();
		if (atom.getVariablesOrOperator().equals("%")) {
			negateOperands(list, 0);
			if (list.equals(parent)) {
				list = list.operands.get(0);
				return;
			} else {
				parent.operands.addAll(parent.operands.indexOf(list),
						list.operands);
				parent.operands.remove(list);
			}
		}
		if (atom.getVariablesOrOperator().equals("-")) {
			negateOperands(list, 1);
			atom.setVariablesOrOperator("+");
		}
	}

	/**
	 * Method to negate operands of the ListRepresentation specified
	 * 
	 * @param ListRepresentation
	 *            list : The given list expression
	 * @param int start : The starting index
	 * @return void
	 */
	public static void negateOperands(ListRepresentation list, int start) {
		int end;
		if (start == -1) {
			start = 0;
			end = list.operands.size() - 1;
		} else {
			end = list.operands.size();
		}
		for (int x = start; x < end; x++) {
			ListRepresentation current = list.operands.get(x);
			ExpressionAtom atom = current.getNodeVal();
			if (atom.getAtomType() == AtomType.OPERATOR) {
				if (!current.isNegative()) {
					current.setNegative(true);
				} else {
					current.setNegative(false);
				}
			} else {
				atom.setCoefficient(atom.getCoefficient() * -1);
			}
		}
	}

	/**
	 * Method that checks if the operator is higher in precedence than the
	 * second one by using mapping
	 * 
	 * @param ExpressionAtom
	 *            peek : Where we need to peek
	 * @param ExpressionAtom
	 *            token : The specified token
	 * @return boolean
	 */
	private static boolean isTopwithHigerPrecedence(ExpressionAtom peek,
			ExpressionAtom token) {
		if (!(peek.getAtomType() == AtomType.OPERATOR)
				|| peek.getVariablesOrOperator().equals("(")
				|| peek.getVariablesOrOperator().equals(")"))
			return false;

		Operators op1 = operatorsMap.get(peek.getVariablesOrOperator());
		Operators op2 = operatorsMap.get(token.getVariablesOrOperator());

		if (op1.getPrecedence() >= op2.getPrecedence())
			return true;
		else
			return false;

	}

	/**
	 * Method that evaluates the expression or say simplifies it
	 * 
	 * @param void
	 * @return List<ExpressionAtom>
	 */
	private List<ExpressionAtom> evaluateExpression() {
		return listRepresentation.simplify();
	}

	/**
	 * Method that simplifies and further normalizes a given expression
	 * 
	 * @param List
	 *            <ExpressoinAtom> : The specified expression
	 * @return List<ExpressoinAtom>
	 */
	private List<ExpressionAtom> simplifyAndNormalize(
			List<ExpressionAtom> evaluatedExpression) {
		for (int x = 0; x < evaluatedExpression.size(); x++) {
			ExpressionAtom left = evaluatedExpression.get(x);
			for (int y = 0; y < evaluatedExpression.size(); y++) {
				ExpressionAtom right = evaluatedExpression.get(y);
				if (left.equals(right))
					continue;

				if (left.getVariablesOrOperator().equals(
						right.getVariablesOrOperator())) {
					left.setCoefficient(left.getCoefficient()
							+ right.getCoefficient());
					evaluatedExpression.remove(right);
				}
			}
		}
		return evaluatedExpression;
	}

	/**
	 * Constructor to set the state for the specified String representation of a
	 * polynomial
	 */
	public Polynomial(String inputPolynomial) {
		this.infixExpression = parseInputPolynomial(inputPolynomial);

		this.listRepresentation = convertToListRepresentation();
	}

	/**
	 * Method to sort strings
	 * 
	 * @param String
	 *            temVars : The specified string
	 * @return String
	 */
	private String sortString(String termVars) {
		char[] ar = termVars.toCharArray();
		Arrays.sort(ar);
		return String.valueOf(ar);
	}

	/**
	 * Method to invoke to evaluate a particular expression
	 * 
	 * @param void
	 * @return void
	 */
	public void evaluate() {
		List<ExpressionAtom> evaluatedExpression = evaluateExpression();

		for (int i = 0; i < evaluatedExpression.size(); ++i) {
			evaluatedExpression.get(i).setVariablesOrOperator(
					(sortString(evaluatedExpression.get(i)
							.getVariablesOrOperator())));
		}

		this.finalExpression = simplifyAndNormalize(evaluatedExpression);
	}
}