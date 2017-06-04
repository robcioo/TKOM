package parser;

import semantics.Scope;

public interface Expression{
	public Object evaluate(Scope scope);
}
