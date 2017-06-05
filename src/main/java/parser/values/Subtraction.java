package parser.values;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import semantics.Scope;

public class Subtraction extends AdditiveExpression {

	@Override
	public Object evaluate(Scope scope) {
		return subtract(arguments.get(0).evaluate(scope), arguments.get(1).evaluate(scope));
	}

	public static Object subtract(Object ob, Object expr) {
		if ((ob instanceof Number) && expr instanceof Number) {
			BigDecimal result = new BigDecimal(ob.toString()).subtract(new BigDecimal(expr.toString()));
			if (ob instanceof Long)
				return result.longValue();
			else
				return result.doubleValue();
		}
		if (ob instanceof ArrayList) {
			if (expr instanceof ArrayList) {
				ArrayList<Object> arr1 = (ArrayList<Object>) ob;
				ArrayList<Object> arr2 = (ArrayList<Object>) expr;
				for (Object o : arr2) {
					for (int i = 0; i < arr1.size(); ++i) {
						if (o.equals(arr1.get(i))) {
							arr1.remove(i);
							--i;
						}
					}
				}
				return arr1;
			} else {
				ArrayList<Object> arr1 = (ArrayList<Object>) ob;
				for (int i = 0; i < arr1.size(); ++i) {
					if (expr.equals(arr1.get(i))) {
						arr1.remove(i);
						--i;
					}
				}
				return arr1;
			}
		}
		throw new CancellationException("Bledne argumenty odejmowania.  ("+ob.getClass()+", "+expr.getClass()+")");
	}
}
