package parser.values;

import java.util.ArrayList;

import parser.Expression;
import semantics.Scope;

public class ListConst implements Expression{
	ArrayList<Expression> arguments;
	public ListConst() {
		arguments=new ArrayList<>();
	}
	public void addArgument(Expression expression){
		arguments.add(expression);
	}

	@Override
	public Object evaluate(Scope scope) {
		ArrayList<Object> arr=new ArrayList<>();
		for(Expression ex: arguments)
			arr.add(ex.evaluate(scope));
		return arr;
	}
}
