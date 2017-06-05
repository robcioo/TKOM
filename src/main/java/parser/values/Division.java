package parser.values;

import java.math.BigDecimal;
import java.util.concurrent.CancellationException;

import semantics.Scope;

public class Division extends MultiplicativeExpression{

	@Override
	public Object evaluate(Scope scope) {
		return divide(arguments.get(0).evaluate(scope), arguments.get(1).evaluate(scope));
	}
	public static Object divide(Object ob, Object expr) {
		if ((ob instanceof Number) && expr instanceof Number) {
			BigDecimal result = new BigDecimal(ob.toString()).divide(new BigDecimal(expr.toString()));
			if (ob instanceof Long)
				return result.longValue();
			else
				return result.doubleValue();
		}
		throw new CancellationException("Bledne argumenty dzielenia. ("+ob.getClass()+", "+expr.getClass()+")");
	}
}
