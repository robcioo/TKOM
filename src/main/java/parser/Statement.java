package parser;

import java.util.ArrayList;

import semantics.Scope;

public interface Statement  {
	public Object execute(Scope scope, ArrayList<Object> args) ;
}
