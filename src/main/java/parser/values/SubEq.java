package parser.values;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.jws.Oneway;

import parser.Expression;
import parser.Statement;
import semantics.Scope;

public class SubEq implements Statement {

	private String var;
	private Expression expression;

	public SubEq(String var, Expression expression) {
		super();
		this.var = var;
		this.expression = expression;
	}

	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		Object ob = scope.getValue(var);
		Object expr = expression.evaluate(scope);
		scope.setValue(var, Subtraction.subtract(ob, expr));
		return null;
	}
}
