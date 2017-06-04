package parser.values;

import parser.Expression;
import semantics.Scope;

public class Const implements Expression{
	private Double value;
	public Const(String value) {
		this.value=Double.parseDouble(value);
	}
	@Override
	public Object evaluate(Scope scope) {
		return value;
	}

}
