package parser.values;


import java.util.ArrayList;

import parser.Expression;
import parser.Statement;
import semantics.Scope;

public class Equal implements Statement{

	private String var;
	private Expression expression;
	
	
	public Equal(String var, Expression expression) {
		super();
		this.var = var;
		this.expression = expression;
	}


	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		scope.setValue(var, expression.evaluate(scope));
		return null;
	}
}
