package parser.values;

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
		// TODO Auto-generated method stub
		return null;
	}
	
}
