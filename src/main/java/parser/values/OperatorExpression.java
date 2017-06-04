package parser.values;

import java.util.ArrayList;

import parser.Expression;
import semantics.Scope;

public abstract class OperatorExpression implements Expression{
	protected ArrayList<Expression> arguments;
	public OperatorExpression() {
		arguments=new ArrayList<>();
	}
	public void addArgument(Expression ex){
		arguments.add(ex);
	}
	@Override
	public abstract Object evaluate(Scope scope) ;
}
