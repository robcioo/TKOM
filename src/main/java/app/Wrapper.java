package app;

import parser.Expression;
import semantics.Scope;

public class Wrapper implements Expression{
private Object o;
	@Override
	public Object evaluate(Scope scope) {
		return o;
	}
	public Wrapper(Object o) {
		super();
		this.o = o;
	}

}
