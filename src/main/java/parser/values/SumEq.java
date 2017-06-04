package parser.values;


import java.util.ArrayList;

import parser.Expression;
import parser.Statement;
import semantics.Scope;

public class SumEq  implements Statement{

	private String var;
	private Expression expression;
	
	
	public SumEq(String var, Expression expression) {
		super();
		this.var = var;
		this.expression = expression;
	}


	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		Object ob = scope.getValue(var);
		Object expr = expression.evaluate(scope);
		return scope.setValue(var, Sum.sum(ob, expr));
	}
}
