package parser.values;

import java.math.BigDecimal;
import java.util.concurrent.CancellationException;

import parser.Expression;
import semantics.Scope;

public class Decrement implements Expression{
	private String varName;
	
	
	public Decrement(String varName) {
		super();
		this.varName = varName;
	}


	@Override
	public Object evaluate(Scope scope) {
		Object ob = scope.getValue(varName);
		if(ob instanceof Long)
			return scope.setValue(varName, new BigDecimal(ob.toString()).subtract(new BigDecimal(1)).longValue());
		throw new CancellationException("Nie mozna decrementowac zmiennej typu "+scope.getVar(varName).getDataType());
	}
	
}
