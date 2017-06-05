package parser.values;

import java.util.concurrent.CancellationException;

import parser.Expression;
import semantics.Scope;

public class AndOperator extends OperatorExpression implements Expression{

	@Override
	public Object evaluate(Scope scope) {
		Object ob1=arguments.get(0).evaluate(scope);
		Object ob2=arguments.get(1).evaluate(scope);
		if(!(ob1 instanceof Boolean && ob2 instanceof Boolean))
			throw new CancellationException("Nie mozna wykona operacji and na obiektach klasy "+ob1.getClass()+" i "+ob2.getClass());
		return new Boolean((Boolean)ob1 && (Boolean)ob2);
	}

}
