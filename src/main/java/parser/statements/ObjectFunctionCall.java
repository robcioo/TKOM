package parser.statements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import parser.Expression;
import parser.Statement;
import semantics.Scope;

public class ObjectFunctionCall implements Expression, Statement{
	private String varName;
	private HashMap<String, FunctionStatement> functions;
	private FuctionCallStatement functionCall;
	public ObjectFunctionCall(String varName , HashMap<String, FunctionStatement> functions) {
		super();
		this.varName = varName;
		this.functions=functions;
	}

	public void addFunctionCall(FuctionCallStatement functionCall) {
		this.functionCall=functionCall;
	}

	@Override
	public Object evaluate(Scope scope) {
		return execute(scope, null);
	}


	@Override
	public Object execute(Scope scope, ArrayList<Object> args) {
		return functionCall.execute(scope,new ArrayList<>(Arrays.asList(true,varName)));
	}

}
