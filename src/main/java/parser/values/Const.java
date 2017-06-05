package parser.values;

import parser.Expression;
import semantics.Scope;

public class Const implements Expression {
	private Double doubleValue;
	private Long longValue;
	private boolean sign;

	public Const(String value) {
		if (value.contains("."))
			this.doubleValue = Double.parseDouble(value);
		else
			this.longValue = Long.parseLong(value);
		sign = true;
	}

	public void changeSign() {
		if (sign)
			sign = false;
		else
			sign = true;
	}

	@Override
	public Object evaluate(Scope scope) {
		int signValue=(sign?1:-1);
		if (doubleValue != null)
			return signValue*doubleValue;
		return signValue*longValue;
	}

}
