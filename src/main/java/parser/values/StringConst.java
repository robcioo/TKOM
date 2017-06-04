package parser.values;

import parser.Expression;
import semantics.Scope;

public class StringConst implements Expression{
	private String value;
	public StringConst(String value) {
		this.value=value;
	}
	@Override
	public Object evaluate(Scope scope) {
		return value.substring(1, value.length()-1);
	}

}
