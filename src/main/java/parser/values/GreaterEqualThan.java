package parser.values;

import semantics.Comparator;
import semantics.Scope;

public class GreaterEqualThan extends ComparisonOperator{

	@Override
	public Object evaluate(Scope scope) {
		return new Boolean(Comparator.compare(arguments.get(0).evaluate(scope), arguments.get(1).evaluate(scope))>=0);
	}

}
