package parser.values;

import parser.Expression;
import semantics.Scope;

public class Value implements Expression {
	private String name;

	public Value(String name) {
		super();
		this.name = name;
	}

	@Override
	public Object evaluate(Scope scope) {
		return scope.getValue(name);
	}

}
