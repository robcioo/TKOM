package parser.values;

import parser.Expression;
import semantics.Scope;

public class Not implements Expression {
	private Expression ex;

	public Not(Expression ex) {
		this.ex = ex;
	}

	@Override
	public Object evaluate(Scope scope) {
		// TODO Auto-generated method stub
		return null;
	}

}
