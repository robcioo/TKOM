package parser.values;

import semantics.Scope;

public class NotEquals  extends ComparisonOperator{

	@Override
	public Object evaluate(Scope scope) {
		return new Boolean(!arguments.get(0).evaluate(scope).equals(arguments.get(1).evaluate(scope)));
	}

}
