package parser.values;

import java.util.concurrent.CancellationException;

import parser.Expression;
import semantics.Scope;

public class Not implements Expression {
	private Expression ex;

	public Not(Expression ex) {
		this.ex = ex;
	}

	@Override
	public Object evaluate(Scope scope) {
		Object ob=ex.evaluate(scope);
		if(!(ob instanceof Boolean))
			throw new CancellationException("Nie mozna zanegowac obiektu klasy "+ob.getClass());
		return new Boolean(!(Boolean)ob);
	}

}
