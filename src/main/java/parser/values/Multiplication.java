package parser.values;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import semantics.Scope;

public class Multiplication  extends MultiplicativeExpression{

	@Override
	public Object evaluate(Scope scope) {
		return mul(arguments.get(0).evaluate(scope), arguments.get(1).evaluate(scope));
	}
	public static Object mul(Object ob, Object expr) {
		if ((ob instanceof Number) && expr instanceof Number) {
			BigDecimal result = new BigDecimal(ob.toString()).multiply(new BigDecimal(expr.toString()));
			if (ob instanceof Long)
				return result.longValue();
			else
				return result.doubleValue();
		}
		throw new CancellationException("Bledne argumenty mnozenia. ("+ob.getClass()+", "+expr.getClass()+")");
	}
}
