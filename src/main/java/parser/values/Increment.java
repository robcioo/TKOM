package parser.values;

import parser.Expression;
import semantics.Scope;

public class Increment  implements Expression{
	private String varName;
	
	
	public Increment(String varName) {
		super();
		this.varName = varName;
	}


	@Override
	public Object evaluate(Scope scope) {
		// TODO Auto-generated method stub
		return null;
	}
	
}