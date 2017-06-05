package parser.statements;

import java.util.ArrayList;

import parser.Expression;
import parser.Statement;
import semantics.Scope;

public class ReturnStatement implements Statement{
	private Expression expresion;
	

	public Expression getExpresion() {
		return expresion;
	}

	public void setExpresion(Expression expresion) {
		this.expresion = expresion;
	}

	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		return expresion.evaluate(scope);
	}
	
}
