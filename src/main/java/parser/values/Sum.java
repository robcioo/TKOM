package parser.values;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import semantics.Scope;

public class Sum extends AdditiveExpression {

	@Override
	public Object evaluate(Scope scope) {
		return sum(arguments.get(0), arguments.get(1));
	}

	public static Object sum(Object ob, Object expr) {
		if ((ob instanceof Number) && expr instanceof Number) {
			BigDecimal result = new BigDecimal(ob.toString()).add(new BigDecimal(expr.toString()));
			if (ob instanceof Long)
				return result.longValue();
			else
				return result.doubleValue();
		}
		if (ob instanceof String) {
			return ob.toString() + expr.toString();
		}
		if (ob instanceof ArrayList) {
			StringBuilder sb = new StringBuilder(ob.toString());
			if (expr instanceof ArrayList) {
				ArrayList<Object> arr1 = (ArrayList<Object>) ob;
				ArrayList<Object> arr2 = (ArrayList<Object>) expr;
				arr1.addAll(arr2);
				return arr1;
			} else {
				ArrayList<Object> arr1 = (ArrayList<Object>) ob;
				arr1.add(expr);
				return arr1;
			}
		}
		throw new CancellationException("Bledne argumenty sumowania.");
	}
}
