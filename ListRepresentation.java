import java.util.ArrayList;
import java.util.List;

public class ListRepresentation {
	private ExpressionAtom nodeVal = null;
	public List<ListRepresentation> operands = new ArrayList<ListRepresentation>();
	private boolean isNegative = false;

	public void setNodeVal(ExpressionAtom inputNodeVal) {
		this.nodeVal = inputNodeVal;
	}

	public void setNegative(boolean inputIsNegative) {
		this.isNegative = inputIsNegative;
	}

	public ExpressionAtom getNodeVal() {
		return this.nodeVal;
	}

	public boolean isNegative() {
		return this.isNegative;
	}

	public List<ExpressionAtom> simplify() {
		List<ExpressionAtom> allTerms = new ArrayList<ExpressionAtom>();
		List<List<ExpressionAtom>> calculatedResult = solve();
		for (List<ExpressionAtom> value : calculatedResult) {
			allTerms.addAll(value);
		}
		return allTerms;
	}

	// a recursion method that will call each child nodes solve
	private List<List<ExpressionAtom>> solve() {
		List<List<ExpressionAtom>> result = new ArrayList<List<ExpressionAtom>>();
		if (nodeVal.getAtomType() == AtomType.OPERAND) {
			List<ExpressionAtom> node = new ArrayList<ExpressionAtom>();
			node.add(nodeVal);
			result.add(node);
			return result;
		} else {
			for (int i = 0; i < operands.size(); ++i) {
				result.addAll(operands.get(i).solve());
			}
			return ((isNegative) ? negate(calculate(result))
					: calculate(result));
		}
	}

	// negate all the terms in the list
	private List<List<ExpressionAtom>> negate(List<List<ExpressionAtom>> list) {
		List<ExpressionAtom> negativeList = new ArrayList<ExpressionAtom>();
		negativeList.add(new ExpressionAtom("", AtomType.OPERAND, -1));
		list.add(negativeList);
		return multiply(list, false);
	}

	// the backbone of calculation applying addition , multiplication , exp
	private List<List<ExpressionAtom>> calculate(List<List<ExpressionAtom>> list) {
		if (nodeVal.getVariablesOrOperator().equals("+")) {
			List<ExpressionAtom> all = new ArrayList<ExpressionAtom>();
			for (List<ExpressionAtom> node : list) {
				all.addAll(node);
			}
			for (int l = 0; l < all.size(); l++) {
				ExpressionAtom left = all.get(l);
				for (int r = 0; r < all.size(); r++) {
					ExpressionAtom right = all.get(r);
					if (left.equals(right))
						continue;

					if (left.getVariablesOrOperator().equals(
							right.getVariablesOrOperator())) {
						left.setCoefficient((left.getCoefficient() + right
								.getCoefficient()));
						all.remove(right);
					}
				}
			}
			List<List<ExpressionAtom>> result = new ArrayList<List<ExpressionAtom>>();
			result.add(all);
			return result;
		} else if (nodeVal.getVariablesOrOperator().equals("*")) {
			List<List<ExpressionAtom>> result = null;
			while (list.size() != 0) {
				List<ExpressionAtom> currentList = list.get(0);
				if (result == null) {
					List<ExpressionAtom> nextList = list.get(1);
					result = new ArrayList<List<ExpressionAtom>>();
					result.add(currentList);
					result.add(nextList);
					result = multiply(result, false);
					list.remove(currentList);
					list.remove(nextList);
				} else {
					result.add(currentList);
					result = multiply(result, false);
					list.remove(currentList);
				}
			}
			return result;
		} else if (nodeVal.getVariablesOrOperator().equals("^")) {
			int degree = list.get(list.size() - 1).get(0).getCoefficient();
			if (degree == 0) {
				List<List<ExpressionAtom>> result = new ArrayList<List<ExpressionAtom>>();
				List<ExpressionAtom> one = new ArrayList<ExpressionAtom>();
				one.add(new ExpressionAtom("", AtomType.OPERAND, 1));
				result.add(one);
				return result;
			}
			list.remove(list.size() - 1);
			List<List<ExpressionAtom>> result = null;
			for (int l = 0; l < list.size(); l++) {
				List<ExpressionAtom> currentList = list.get(l);
				for (int x = 1; x < degree; x++) {
					if (result == null) {
						result = new ArrayList<List<ExpressionAtom>>();
						result.add(currentList);
						result = multiply(result, true);
					} else {
						result.add(currentList);
						result = multiply(result, false);
					}
				}
				list = result;
				result = null;
			}

		}

		return list;
	}

	// multiple the contents of the list , canRepeat is flag use eg. if the
	// first list can be multiplied by itself
	private List<List<ExpressionAtom>> multiply(
			List<List<ExpressionAtom>> list, boolean canRepeat) {
		for (int l = 0; l < list.size(); l++) {
			List<ExpressionAtom> left = list.get(l);
			for (int r = 0; r < list.size(); r++) {
				List<ExpressionAtom> right = list.get(r);
				if (left.equals(right) && !canRepeat)
					continue;

				List<ExpressionAtom> all = new ArrayList<ExpressionAtom>();

				for (int l1 = 0; l1 < left.size(); l1++) {
					ExpressionAtom left1 = left.get(l1);
					for (int r1 = 0; r1 < right.size(); r1++) {
						ExpressionAtom right1 = right.get(r1);
						if (left1 == right1 && !canRepeat)
							continue;
						ExpressionAtom newAtom = new ExpressionAtom(
								left1.getVariablesOrOperator()
										+ right1.getVariablesOrOperator(),
								AtomType.OPERAND,
								(left1.getCoefficient() * right1
										.getCoefficient()));

						all.add(newAtom);
					}
				}
				list.remove(left);
				list.remove(right);
				list.add(all);
			}
		}

		if (isNegative) {

		}

		return list;
	}
}